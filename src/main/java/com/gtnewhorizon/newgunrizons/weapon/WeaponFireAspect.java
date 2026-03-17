package com.gtnewhorizon.newgunrizons.weapon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstance;
import com.gtnewhorizon.newgunrizons.items.instances.ItemInstanceRegistry;
import com.gtnewhorizon.newgunrizons.items.instances.ItemWeaponInstance;
import com.gtnewhorizon.newgunrizons.network.StatusMessageManager;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessage;
import com.gtnewhorizon.newgunrizons.registry.Sounds;
import com.gtnewhorizon.newgunrizons.state.StateAspect;
import com.gtnewhorizon.newgunrizons.state.StateManager;

public class WeaponFireAspect implements StateAspect<WeaponState, ItemWeaponInstance> {

    private static final long ALERT_TIMEOUT = 500L;

    private static final Set<WeaponState> allowedFireFromStates = new HashSet<>(
        Arrays.asList(
            WeaponState.IDLE,
            WeaponState.SHOOTING
        ));

    private static final Set<WeaponState> allowedUpdateFromStates = new HashSet<>(
        Arrays.asList(
            WeaponState.SHOOTING,
            WeaponState.NO_AMMO
        ));

    private static final Predicate<ItemWeaponInstance> readyToShootAccordingToFireRate = (instance)
        -> (float) (System.currentTimeMillis() - instance.getLastFireTimestamp()) >= 50.0F / instance.getWeapon().getFireRate();

    private static final Predicate<ItemWeaponInstance> readyToShootAccordingToFireMode = (instance)
        -> instance.getSeriesShotCount() < instance.getMaxShots();

    private static final Predicate<ItemWeaponInstance> hasAmmo = (instance)
        -> instance.getAmmo() > 0;

    private static final Predicate<ItemWeaponInstance> alertTimeoutExpired = (instance)
        -> System.currentTimeMillis() >= ALERT_TIMEOUT + instance.getStateUpdateTimestamp();

    private static final Predicate<ItemWeaponInstance> sprinting = (instance)
        -> instance.getPlayer().isSprinting();

    public static final WeaponFireAspect INSTANCE = new WeaponFireAspect();

    private StateManager<WeaponState, ? super ItemWeaponInstance> stateManager;

    public WeaponFireAspect() {}

    public void setStateManager(StateManager<WeaponState, ? super ItemWeaponInstance> stateManager) {
        this.stateManager = stateManager;
        stateManager.in(this)
            .change(WeaponState.IDLE)
            .to(WeaponState.NO_AMMO)
            .when(hasAmmo.negate())
            .withAction(this::cannotFire)
            .manual()
            .in(this)
            .change(WeaponState.NO_AMMO)
            .to(WeaponState.IDLE)
            .when(alertTimeoutExpired)
            .automatic()
            .in(this)
            .change(WeaponState.IDLE)
            .to(WeaponState.SHOOTING)
            .when(
                hasAmmo.and(sprinting.negate())
                    .and(readyToShootAccordingToFireRate))
            .withAction(this::fire)
            .manual()
            .in(this)
            .change(WeaponState.SHOOTING)
            .to(WeaponState.SHOOTING)
            .when(
                hasAmmo.and(sprinting.negate())
                    .and(readyToShootAccordingToFireMode)
                    .and(readyToShootAccordingToFireRate))
            .withAction(this::fire)
            .manual()
            .in(this)
            .change(WeaponState.SHOOTING)
            .to(WeaponState.IDLE)
            .withAction(ItemWeaponInstance::resetCurrentSeries)
            .manual();
    }

    @SideOnly(Side.CLIENT)
    public void onFireButtonClick(EntityPlayer player) {
        ItemWeaponInstance weaponInstance = ItemInstanceRegistry
            .getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (weaponInstance != null) {
            this.stateManager.changeStateFromAnyOf(
                this,
                weaponInstance,
                allowedFireFromStates,
                WeaponState.SHOOTING,
                WeaponState.NO_AMMO);
        }
    }

    @SideOnly(Side.CLIENT)
    public void onFireButtonRelease(EntityPlayer player) {
        ItemWeaponInstance weaponInstance = ItemInstanceRegistry
            .getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (weaponInstance != null) {
            this.stateManager.changeState(this, weaponInstance, WeaponState.IDLE);
        }
    }

    public void onUpdate(EntityPlayer player) {
        ItemWeaponInstance weaponInstance = ItemInstanceRegistry
            .getMainHandItemInstance(player, ItemWeaponInstance.class);
        if (weaponInstance != null) {
            this.stateManager.changeStateFromAnyOf(this, weaponInstance, allowedUpdateFromStates);
        }
    }

    private void cannotFire(ItemWeaponInstance weaponInstance) {
        if (weaponInstance.getAmmo() == 0) {
            String message = StatCollector.translateToLocalFormatted("gui.noAmmo");

            StatusMessageManager.INSTANCE.addAlertMessage(message, 3, 250L, 200L);
            if (weaponInstance.getPlayer() instanceof EntityPlayer) {
                if (Sounds.DRY_FIRE != null) {
                    weaponInstance.getPlayer()
                        .playSound(Sounds.DRY_FIRE, 1.0F, 1.0F);
                }
            }
        }
    }

    private void fire(ItemWeaponInstance weaponInstance) {
        EntityLivingBase player = weaponInstance.getPlayer();
        ItemWeapon weapon = (ItemWeapon) weaponInstance.getItem();
        Random random = player.getRNG();
        NewGunrizonsMod.CHANNEL.sendToServer(
            new WeaponActionMessage(WeaponActionMessage.FIRE,
                ((EntityPlayer) player).inventory.currentItem));

        boolean silencerOn = weaponInstance.isSilencerOn();

        String snd = silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound();
        float vol = silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume();

        if (snd != null) {
            player.playSound(snd, vol, 1.0F);
        }

        if (weaponInstance.getAmmo() == 1 && weapon.getEndOfShootSound() != null) {
            player.playSound(weapon.getEndOfShootSound(), 1.0F, 1.0F);
        }

        float recoilAmount = weapon.getRecoil();
        if (recoilAmount > 0) {
            float rotationYawFactor = -1.0F + random.nextFloat() * 2.0F;
            NewGunrizonsMod.proxy.applyCameraRecoil(-recoilAmount, recoilAmount * rotationYawFactor,
                weapon.getCameraRecoilDurationMs());
        }

        if (weapon.isSmokeEnabled()) {
            NewGunrizonsMod.proxy.onWeaponFireEffects(player);
        }

        weaponInstance.setSeriesShotCount(weaponInstance.getSeriesShotCount() + 1);
        weaponInstance.setLastFireTimestamp(System.currentTimeMillis());
        weaponInstance.setAmmo(weaponInstance.getAmmo() - 1);
    }

    public void serverFire(EntityPlayer player, ItemStack itemStack, int slotIndex) {

        ItemWeapon weapon = (ItemWeapon) itemStack.getItem();
        ItemWeaponInstance instance = ItemInstance.fromStack(itemStack);

        if (instance == null) {
            instance = weapon.createItemInstance(player, itemStack, slotIndex);
            ItemInstance.toStack(itemStack, instance);
        }

        instance.setPlayer(player);

        int ammo = instance.getAmmo();
        if (ammo <= 0) {
            return;
        }
        instance.setAmmo(ammo - 1);
        ItemWeaponInstance.setAmmo(itemStack, ammo - 1);
        ItemInstance.toStack(itemStack, instance);

        for (int i = 0; i < weapon.getPellets(); ++i) {
            weapon.spawnBullet(player);
        }

        if (weapon.isShellCasingEjectEnabled()) {
            weapon.spawnShell(instance, player);
        }

        boolean silencerOn = instance.isSilencerOn();
        String snd = silencerOn ? weapon.getSilencedShootSound() : weapon.getShootSound();
        float vol = silencerOn ? weapon.getSilencedShootSoundVolume() : weapon.getShootSoundVolume();

        player.worldObj.playSoundToNearExcept(player, snd, vol, 1.0F);
    }
}

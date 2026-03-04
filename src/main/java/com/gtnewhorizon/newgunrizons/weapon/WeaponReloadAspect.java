package com.gtnewhorizon.newgunrizons.weapon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gtnewhorizon.newgunrizons.attachment.AttachmentCategory;
import com.gtnewhorizon.newgunrizons.config.ModContext;
import com.gtnewhorizon.newgunrizons.config.Tags;
import com.gtnewhorizon.newgunrizons.items.ItemAttachment;
import com.gtnewhorizon.newgunrizons.items.ItemBullet;
import com.gtnewhorizon.newgunrizons.items.ItemMagazine;
import com.gtnewhorizon.newgunrizons.network.WeaponActionMessage;
import com.gtnewhorizon.newgunrizons.state.Aspect;
import com.gtnewhorizon.newgunrizons.state.StateManager;
import com.gtnewhorizon.newgunrizons.util.InventoryUtils;

public class WeaponReloadAspect implements Aspect<WeaponState, PlayerWeaponInstance> {

    private static final Logger logger = LogManager.getLogger(WeaponReloadAspect.class);
    private static final long ALERT_TIMEOUT = 500L;
    private static final Set<WeaponState> allowedUpdateFromStates;

    private static final Predicate<PlayerWeaponInstance> sprinting;
    private static final Predicate<PlayerWeaponInstance> hasNextLoadIteration;
    private static final Predicate<PlayerWeaponInstance> supportsDirectBulletLoad;
    private static final Predicate<PlayerWeaponInstance> magazineAttached;
    private static final Predicate<PlayerWeaponInstance> loadIterationCompleted;
    private static final Predicate<PlayerWeaponInstance> allLoadIterationsCompleted;
    private static final Predicate<PlayerWeaponInstance> reloadAnimationCompleted;
    private static final Predicate<PlayerWeaponInstance> unloadAnimationCompleted;
    private static final Predicate<PlayerWeaponInstance> prepareFirstLoadIterationAnimationCompleted;
    private final Predicate<PlayerWeaponInstance> inventoryHasFreeSlots = (
        weaponInstance) -> weaponInstance.getPlayer() instanceof EntityPlayer
            && InventoryUtils.inventoryHasFreeSlots((EntityPlayer) weaponInstance.getPlayer());
    private static final Predicate<PlayerWeaponInstance> alertTimeoutExpired;
    private final Predicate<ItemStack> magazineNotEmpty = (magazineStack) -> Tags.getAmmo(magazineStack) > 0;
    private final ModContext modContext;
    private StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager;

    /** Client-side guard: checks if player has compatible ammo or magazine in inventory. */
    private final Predicate<PlayerWeaponInstance> hasCompatibleAmmo = (weaponInstance) -> {
        if (!(weaponInstance.getPlayer() instanceof EntityPlayer player)) {
            return false;
        }
        ItemWeapon weapon = weaponInstance.getWeapon();
        List<ItemMagazine> compatibleMagazines = weapon.getCompatibleMagazines();
        if (!compatibleMagazines.isEmpty()) {
            ItemAttachment existingMagazine = WeaponAttachmentAspect
                .getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance);
            if (existingMagazine != null) {
                return true; // Magazine already attached, reload will use its ammo
            }
            return InventoryUtils.hasCompatibleItem(compatibleMagazines, player, this.magazineNotEmpty, (s) -> true);
        }
        List<ItemAttachment> compatibleBullets = weapon.getCompatibleAttachments(ItemBullet.class);
        if (!compatibleBullets.isEmpty()) {
            return InventoryUtils.hasCompatibleItem(compatibleBullets, player, (s) -> true);
        }
        return weapon.builder.ammo != null && player.inventory.hasItem(weapon.builder.ammo);
    };

    public WeaponReloadAspect(ModContext modContext) {
        this.modContext = modContext;
    }

    public void setStateManager(StateManager<WeaponState, ? super PlayerWeaponInstance> stateManager) {
        this.stateManager = stateManager.in(this)
            .change(WeaponState.READY)
            .to(WeaponState.LOAD)
            .when(supportsDirectBulletLoad.or(magazineAttached.negate())
                .and(this.hasCompatibleAmmo))
            .withAction(this::performLoad)
            .manual()
            .in(this)
            .change(WeaponState.LOAD)
            .to(WeaponState.READY)
            .when(reloadAnimationCompleted.and(hasNextLoadIteration.negate()))
            .automatic()
            .in(this)
            .change(WeaponState.LOAD)
            .to(WeaponState.LOAD_ITERATION)
            .when(hasNextLoadIteration.and(prepareFirstLoadIterationAnimationCompleted))
            .withAction(this::startLoadIteration)
            .automatic()
            .in(this)
            .change(WeaponState.LOAD_ITERATION)
            .to(WeaponState.LOAD_ITERATION_COMPLETED)
            .when(loadIterationCompleted)
            .withAction(this::completeLoadIteration)
            .automatic()
            .in(this)
            .change(WeaponState.LOAD_ITERATION_COMPLETED)
            .to(WeaponState.LOAD_ITERATION)
            .when(hasNextLoadIteration)
            .withAction(this::startLoadIteration)
            .automatic()
            .in(this)
            .change(WeaponState.LOAD_ITERATION_COMPLETED)
            .to(WeaponState.ALL_LOAD_ITERATIONS_COMPLETED)
            .when(hasNextLoadIteration.negate())
            .automatic()
            .in(this)
            .change(WeaponState.ALL_LOAD_ITERATIONS_COMPLETED)
            .to(WeaponState.READY)
            .when(allLoadIterationsCompleted)
            .withAction(this::completeAllLoadIterations)
            .automatic()
            .in(this)
            .prepare((c, f, t) -> { this.prepareUnload(c); }, unloadAnimationCompleted)
            .change(WeaponState.READY)
            .to(WeaponState.UNLOAD)
            .when(magazineAttached.and(this.inventoryHasFreeSlots))
            .withAction(this::performUnload)
            .manual()
            .in(this)
            .change(WeaponState.UNLOAD)
            .to(WeaponState.READY)
            .automatic()
            .in(this)
            .change(WeaponState.READY)
            .to(WeaponState.ALERT)
            .when(this.inventoryHasFreeSlots.negate())
            .withAction(this::inventoryFullAlert)
            .manual()
            .in(this)
            .change(WeaponState.ALERT)
            .to(WeaponState.READY)
            .when(alertTimeoutExpired)
            .automatic();
    }

    public void reloadMainHeldItem(EntityPlayer player) {
        PlayerWeaponInstance instance = this.modContext.getPlayerItemInstanceRegistry()
            .getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if (instance != null) {
            this.stateManager.changeState(this, instance, WeaponState.LOAD, WeaponState.UNLOAD, WeaponState.ALERT);
        }

    }

    public void updateMainHeldItem(EntityPlayer player) {
        PlayerWeaponInstance instance = this.modContext.getPlayerItemInstanceRegistry()
            .getMainHandItemInstance(player, PlayerWeaponInstance.class);
        if (instance != null) {
            this.stateManager.changeStateFromAnyOf(this, instance, allowedUpdateFromStates);
        }

    }

    private void performLoad(PlayerWeaponInstance weaponInstance) {
        if (!(weaponInstance.getPlayer() instanceof EntityPlayer player)) {
            return;
        }
        ItemWeapon weapon = weaponInstance.getWeapon();

        // Optimistic client-side ammo update (mirrors server logic in WeaponActionMessageHandler)
        weaponInstance.setLoadIterationCount(0);
        List<ItemMagazine> compatibleMagazines = weapon.getCompatibleMagazines();
        List<ItemAttachment> compatibleBullets = weapon.getCompatibleAttachments(ItemBullet.class);

        if (!compatibleMagazines.isEmpty()) {
            ItemAttachment existingMagazine = WeaponAttachmentAspect
                .getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance);
            int ammo = Tags.getAmmo(weaponInstance.getItemStack());
            if (existingMagazine == null) {
                ammo = 0;
                // Find compatible magazine in client inventory (read-only, mirrors server's two-pass search)
                ItemMagazine foundMag = this.findCompatibleMagazine(compatibleMagazines, player);
                if (foundMag != null) {
                    ammo = this.getMagazineAmmoFromInventory(foundMag, player);
                    WeaponAttachmentAspect.addAttachment(foundMag, weaponInstance);
                }
            }
            weaponInstance.setAmmo(ammo);
        } else if (!compatibleBullets.isEmpty()) {
            int maxToLoad = Math.min(
                weapon.getMaxBulletsPerReload(),
                weapon.getAmmoCapacity() - weaponInstance.getAmmo());
            int loaded = this.countAvailableBullets(compatibleBullets, player, maxToLoad);
            if (loaded > 0) {
                weaponInstance.setAmmo(weaponInstance.getAmmo() + loaded);
                if (weapon.hasIteratedLoad()) {
                    weaponInstance.setLoadIterationCount(loaded);
                }
            }
        } else if (weapon.builder.ammo != null && player.inventory.hasItem(weapon.builder.ammo)) {
            weaponInstance.setAmmo(weapon.builder.ammoCapacity);
        }

        // Play sound on client
        if (weapon.getReloadSound() != null) {
            player.playSound(weapon.getReloadSound(), 1.0F, 1.0F);
        }

        // Send action to server for authoritative inventory operations
        this.modContext.getChannel()
            .sendToServer(
                new WeaponActionMessage(WeaponActionMessage.WEAPON_LOAD, weaponInstance.getItemInventoryIndex()));
    }

    /** Finds the first compatible magazine in player inventory (prefers non-empty, falls back to any). */
    private ItemMagazine findCompatibleMagazine(List<ItemMagazine> compatibleMagazines, EntityPlayer player) {
        // First pass: non-empty magazines
        for (ItemMagazine mag : compatibleMagazines) {
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack != null && stack.getItem() == mag && Tags.getAmmo(stack) > 0) {
                    return mag;
                }
            }
        }
        // Second pass: any compatible magazine
        for (ItemMagazine mag : compatibleMagazines) {
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack != null && stack.getItem() == mag) {
                    return mag;
                }
            }
        }
        return null;
    }

    /** Reads the ammo count from the first matching magazine ItemStack in inventory. */
    private int getMagazineAmmoFromInventory(ItemMagazine mag, EntityPlayer player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == mag) {
                return Tags.getAmmo(stack);
            }
        }
        return 0;
    }

    /** Counts how many bullets are available for direct loading (without consuming). */
    private int countAvailableBullets(List<ItemAttachment> compatibleBullets, EntityPlayer player, int maxToLoad) {
        for (ItemAttachment bullet : compatibleBullets) {
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack != null && stack.getItem() == bullet) {
                    return Math.min(maxToLoad, stack.stackSize);
                }
            }
        }
        return 0;
    }

    private void prepareUnload(PlayerWeaponInstance weaponInstance) {
        if (weaponInstance.getWeapon()
            .getUnloadSound() != null) {
            weaponInstance.getPlayer()
                .playSound(
                    weaponInstance.getWeapon()
                        .getUnloadSound(),
                    1.0F,
                    1.0F);
        }
    }

    private void performUnload(PlayerWeaponInstance weaponInstance) {
        // Optimistic: set ammo to 0 on client
        weaponInstance.setAmmo(0);

        // Send action to server for authoritative inventory operations
        this.modContext.getChannel()
            .sendToServer(
                new WeaponActionMessage(WeaponActionMessage.WEAPON_UNLOAD, weaponInstance.getItemInventoryIndex()));
    }

    public void inventoryFullAlert(PlayerWeaponInstance weaponInstance) {
        this.modContext.getStatusMessageCenter()
            .addAlertMessage(StatCollector.translateToLocalFormatted("gui.inventoryFull"), 3, 250L, 200L);
    }

    public void startLoadIteration(PlayerWeaponInstance weaponInstance) {
        if (weaponInstance.getWeapon()
            .getReloadIterationSound() != null) {
            weaponInstance.getPlayer()
                .playSound(
                    weaponInstance.getWeapon()
                        .getReloadIterationSound(),
                    1.0F,
                    1.0F);
        }
    }

    public void completeLoadIteration(PlayerWeaponInstance weaponInstance) {
        weaponInstance.setLoadIterationCount(weaponInstance.getLoadIterationCount() - 1);
    }

    public void completeAllLoadIterations(PlayerWeaponInstance weaponInstance) {
        if (weaponInstance.getWeapon()
            .getAllReloadIterationsCompletedSound() != null) {
            weaponInstance.getPlayer()
                .playSound(
                    weaponInstance.getWeapon()
                        .getAllReloadIterationsCompletedSound(),
                    1.0F,
                    1.0F);
        }
    }

    static {
        allowedUpdateFromStates = new HashSet<>(
            Arrays.asList(
                WeaponState.LOAD,
                WeaponState.LOAD_ITERATION,
                WeaponState.LOAD_ITERATION_COMPLETED,
                WeaponState.ALL_LOAD_ITERATIONS_COMPLETED,
                WeaponState.UNLOAD_PREPARING,
                WeaponState.UNLOAD,
                WeaponState.ALERT));
        sprinting = (instance) -> instance.getPlayer()
            .isSprinting();
        hasNextLoadIteration = (weaponInstance) -> weaponInstance.getWeapon()
            .hasIteratedLoad() && weaponInstance.getLoadIterationCount() > 0;
        supportsDirectBulletLoad = (weaponInstance) -> weaponInstance.getWeapon()
            .getAmmoCapacity() > 0;
        magazineAttached = (
            weaponInstance) -> WeaponAttachmentAspect.getActiveAttachment(AttachmentCategory.MAGAZINE, weaponInstance)
                != null;
        loadIterationCompleted = (weaponInstance) -> System.currentTimeMillis()
            >= weaponInstance.getStateUpdateTimestamp() + Math.max(
                weaponInstance.getWeapon().builder.loadIterationTimeout,
                weaponInstance.getWeapon()
                    .getTotalLoadIterationDuration() + 250L);
        allLoadIterationsCompleted = (weaponInstance) -> System.currentTimeMillis()
            >= weaponInstance.getStateUpdateTimestamp() + weaponInstance.getWeapon()
                .getAllLoadIterationAnimationsCompletedDuration();
        reloadAnimationCompleted = (weaponInstance) -> (double) System.currentTimeMillis()
            >= (double) weaponInstance.getStateUpdateTimestamp() + Math.max(
                (double) weaponInstance.getWeapon().builder.reloadingTimeout,
                (double) weaponInstance.getWeapon()
                    .getTotalReloadingDuration() * 1.1D);
        unloadAnimationCompleted = (weaponInstance) -> (double) System.currentTimeMillis()
            >= (double) weaponInstance.getStateUpdateTimestamp() + (double) weaponInstance.getWeapon()
                .getTotalUnloadingDuration() * 1.1D;
        prepareFirstLoadIterationAnimationCompleted = (weaponInstance) -> (double) System.currentTimeMillis()
            >= (double) weaponInstance.getStateUpdateTimestamp() + (double) weaponInstance.getWeapon()
                .getPrepareFirstLoadIterationAnimationDuration() * 1.1D;
        alertTimeoutExpired = (instance) -> System.currentTimeMillis()
            >= ALERT_TIMEOUT + instance.getStateUpdateTimestamp();
    }
}

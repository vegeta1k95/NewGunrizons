package com.gtnewhorizon.newgunrizons.items.factories.guns.shotgun;

import static com.gtnewhorizon.newgunrizons.client.debug.PositionDebugger.debuggable;

import java.util.Arrays;

import net.minecraft.item.Item;

import com.gtnewhorizon.newgunrizons.NewGunrizonsMod;
import com.gtnewhorizon.newgunrizons.client.debug.PositionDebugger.Mode;
import com.gtnewhorizon.newgunrizons.client.debug.PositionDebugger.TransformState;
import com.gtnewhorizon.newgunrizons.client.render.WeaponRenderer;
import com.gtnewhorizon.newgunrizons.items.ItemWeapon;
import com.gtnewhorizon.newgunrizons.model.JsonModel;
import com.gtnewhorizon.newgunrizons.registry.Bullets;
import com.gtnewhorizon.newgunrizons.state.RenderableState;

public class DoomShotgunFactory {

    private static final TransformState ENTITY_POS = new TransformState(0f, 0f, 0f, 0f, 0f, 0f, 3f);
    private static final TransformState INVENTORY_POS = new TransformState();
    private static final TransformState THIRD_PERSON_POS = new TransformState();
    private static final TransformState FP_POS = new TransformState(-0.330f, -0.330f, -0.780f, 0.0f, 45.0f, 0.0f, 3.0f);
    private static final TransformState FP_ZOOMING_POS = new TransformState(0.700f, -0.600f, -0.800f, 4.0f, 45.0f, 0.0f, 3.0f);
    private static final TransformState FP_RUNNING_POS = new TransformState(-0.600f, -0.250f, -0.670f, 20.0f, 24.0f, 0.0f, 3.0f);
    private static final TransformState LEFT_HAND_POS = new TransformState(-0.330f, -0.730f, 0.780f, -95.0f, 49.0f, 9.0f, 0.5f);
    private static final TransformState RIGHT_HAND_POS = new TransformState(0.410f, -0.210f, -0.090f, -98.0f, -36.0f, -28.0f, 0.5f);
    private static final TransformState LEFT_HAND_ZOOM_POS = new TransformState(-0.290f, -0.990f, 0.590f, -106.0f, 9.0f, 17.0f, 0.5f);
    private static final TransformState RIGHT_HAND_ZOOM_POS = new TransformState(0.330f, -0.250f, 0.000f, -80.0f, -56.0f, 0.0f, 0.5f);
    private static final TransformState FP_MODIFYING_POS = new TransformState();

    public Item createGun() {
        return (new ItemWeapon.Builder()).withName("DoomShotgun")
            .withAmmoCapacity(9)
            .withMaxBulletsPerReload(8)
            .withFireRate(0.5F)
            .withRecoil(5.0F)
            .withCameraRecoilDuration(120)
            .withMaxShots(1)
            .withShootSound("remington870")
            .withReloadSound("loadshell")
            .withReloadIterationSound("loadshell")
            .withAllReloadIterationsCompletedSound("pump")
            .withReloadingTime(15L)
            .withIteratedLoad()
            .withCrosshair("gun")
            .withCrosshairRunning("Running")
            .withCrosshairZoomed("Sight")
            .withFlashIntensity(0.7F)
            .withFlashScale(() -> 0.4F)
            .withFlashOffsetX(() -> 0.0F)
            .withFlashOffsetY(() -> 0.3F)
            .withFlashOffsetZ(() -> 3.0F)
            .withInaccuracy(8.0F)
            .withPellets(8)
            .withCreativeTab(NewGunrizonsMod.ShotgunsTab)
            .withInformationProvider(
                (stack) -> Arrays.asList(
                    "Type: Pump-Action Shotgun",
                    "Damage: 8x8 pellets",
                    "Ammo: 12 Gauge Shotgun Shells",
                    "Fire Rate: Pump Action"))
            .withCompatibleBullet(Bullets.ShotgunShell, (model) -> {})
            .withTextureName("shotgun")
            .withRenderer(
                (new WeaponRenderer.Builder()).withModel(new JsonModel("weapon/shotgun"))
                    .withBedrockAnimation("weapon/shotgun")
                    .withBedrockAnimationForState(RenderableState.RECOILED, "animation.shotgun.shoot")
                    .withBedrockAnimationForState(RenderableState.ZOOMING_RECOILED, "animation.shotgun.shoot")
                    .withBedrockAnimationForState(RenderableState.ALL_LOAD_ITERATIONS_COMPLETED, "animation.shotgun.pump")
                    .withRecoilAnimationDuration(100)
                    .withShootingAnimationDuration(400)
                    .withEntityPositioning(debuggable(Mode.ENTITY, ENTITY_POS))
                    .withInventoryPositioning(debuggable(Mode.INVENTORY, INVENTORY_POS))
                    .withThirdPersonPositioning(debuggable(Mode.THIRD_PERSON, THIRD_PERSON_POS))
                    .withFirstPersonPositioning(debuggable(Mode.FIRST_PERSON, FP_POS))
                    //.withFirstPersonPositioningRecoiled(debuggable(Mode.FIRST_PERSON_RECOILED, FP_POS))
                    .withFirstPersonPositioningZooming(debuggable(Mode.FIRST_PERSON_ZOOMING, FP_ZOOMING_POS))
                    .withFirstPersonPositioningRunning(debuggable(Mode.FIRST_PERSON_RUNNING, FP_RUNNING_POS))
                    //.withFirstPersonPositioningZoomingRecoiled(
                    //    debuggable(Mode.FIRST_PERSON_ZOOMING_RECOILED, FP_ZOOMING_POS))
                    .withFirstPersonPositioningModifying(debuggable(Mode.FIRST_PERSON_MODIFYING, FP_MODIFYING_POS))
                    .withFirstPersonHandPositioning(
                        debuggable(Mode.LEFT_HAND, LEFT_HAND_POS),
                        debuggable(Mode.RIGHT_HAND, RIGHT_HAND_POS))
                    .withFirstPersonHandPositioningZooming(
                        debuggable(Mode.LEFT_HAND_ZOOMING, LEFT_HAND_ZOOM_POS),
                        debuggable(Mode.RIGHT_HAND_ZOOMING, RIGHT_HAND_ZOOM_POS))
                    .build())
            .withSpawnEntityDamage(8.0F)
            .withSpawnEntityGravityVelocity(0.016F)
            .build();
    }
}

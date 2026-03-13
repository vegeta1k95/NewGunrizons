package com.gtnewhorizon.newgunrizons.client.debug;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;

import com.gtnewhorizon.newgunrizons.client.debug.PositionDebugger.Mode;
import com.gtnewhorizon.newgunrizons.client.debug.PositionDebugger.TransformState;

public class PositionDebugScreen extends GuiScreen {

    private static final String[] SLIDER_LABELS = {
        "Translate X", "Translate Y", "Translate Z",
        "Rotate X", "Rotate Y", "Rotate Z",
        "Scale" };

    private static final String[] FLASH_SLIDER_LABELS = {
        "Flash Ofs X", "Flash Ofs Y", "Flash Ofs Z",
        "---", "---", "---",
        "Flash Scale" };

    private static final float[] SLIDER_MINS = { -5, -5, -5, -180, -180, -180, 0f };
    private static final float[] SLIDER_MAXS = { 5, 5, 5, 180, 180, 180, 20 };
    private static final float[] SLIDER_STEPS = { 0.01f, 0.01f, 0.01f, 1.0f, 1.0f, 1.0f, 0.1f };
    private static final int SLIDER_COUNT = 7;

    private static final String RECOIL_LABEL = "Recoil (ms)";
    private static final String SHOOTING_LABEL = "Shooting (ms)";
    private static final String CAM_RECOIL_LABEL = "Cam Recoil (ms)";
    private static final float DURATION_MIN = 10f;
    private static final float DURATION_MAX = 5000f;
    private static final float DURATION_STEP = 5f;

    private static final int PANEL_WIDTH = 220;
    private static final int SLIDER_H = 12;
    private static final int SLIDER_GAP = 18;
    private static final int TRACK_H = 10;

    // Mode button IDs: 100+
    // Action button IDs: 200+
    private static final int BTN_SAVE = 200;
    private static final int BTN_COPY = 201;
    private static final int BTN_DISABLE = 202;
    private static final int BTN_COPY_VALUES = 203;
    private static final int BTN_PASTE_VALUES = 204;

    private static TransformState clipboard;

    private static final int RECOIL_SLIDER_INDEX = 99;
    private static final int SHOOTING_SLIDER_INDEX = 98;
    private static final int CAM_RECOIL_SLIDER_INDEX = 97;

    private int dragSlider = -1;
    private int panelX;
    private int sliderTrackX;
    private int sliderTrackW;
    private int slidersStartY;
    private int recoilSliderY;
    private int shootingSliderY;
    private int camRecoilSliderY;

    @Override
    public void initGui() {
        buttonList.clear();
        PositionDebugger.load();

        panelX = width - PANEL_WIDTH - 5;
        sliderTrackX = panelX + 80;
        sliderTrackW = PANEL_WIDTH - 80 - 50;

        // Mode buttons - two rows
        Mode[] modes = Mode.values();
        int modeX = panelX;
        int modeY = 8;
        for (int i = 0; i < modes.length; i++) {
            int bw = fontRendererObj.getStringWidth(modes[i].displayName) + 8;
            if (modeX + bw > width - 5) {
                modeX = panelX;
                modeY += 16;
            }
            buttonList.add(new GuiButton(100 + i, modeX, modeY, bw, 14, modes[i].displayName));
            modeX += bw + 2;
        }

        slidersStartY = modeY + 22;
        recoilSliderY = slidersStartY + SLIDER_COUNT * SLIDER_GAP + 4;
        shootingSliderY = recoilSliderY + SLIDER_GAP;
        camRecoilSliderY = shootingSliderY + SLIDER_GAP;

        // Action buttons at bottom
        int bottomY = camRecoilSliderY + SLIDER_GAP + 80;
        int btnW = 65;
        buttonList.add(new GuiButton(BTN_SAVE, panelX, bottomY, btnW, 16, "Save"));
        buttonList.add(new GuiButton(BTN_COPY, panelX + btnW + 4, bottomY, btnW + 10, 16, "Copy Code"));
        buttonList.add(new GuiButton(BTN_DISABLE, panelX + btnW * 2 + 18, bottomY, btnW, 16, "Disable"));
        int bottomY2 = bottomY + 20;
        buttonList.add(new GuiButton(BTN_COPY_VALUES, panelX, bottomY2, btnW + 10, 16, "Copy Vals"));
        buttonList.add(new GuiButton(BTN_PASTE_VALUES, panelX + btnW + 14, bottomY2, btnW + 10, 16, "Paste Vals"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!Mouse.isButtonDown(0)) {
            dragSlider = -1;
        }

        // Red target crosshair at screen center
        int cx = width / 2;
        int cy = height / 2;
        drawRect(cx - 1, cy - 5, cx + 1, cy + 5, 0xFFFF0000);
        drawRect(cx - 5, cy - 1, cx + 5, cy + 1, 0xFFFF0000);

        // Panel background
        drawRect(panelX - 4, 2, width - 1, height - 2, 0xC0101010);

        // Highlight active mode button
        Mode current = PositionDebugger.getCurrentMode();
        for (Object obj : buttonList) {
            GuiButton btn = (GuiButton) obj;
            if (btn.id >= 100 && btn.id < 100 + Mode.values().length) {
                Mode mode = Mode.values()[btn.id - 100];
                btn.enabled = mode != current;
            }
        }

        // Draw sliders
        TransformState state = PositionDebugger.getState(current);
        float[] values = getValues(state);
        String[] labels = current == Mode.MUZZLE_FLASH ? FLASH_SLIDER_LABELS : SLIDER_LABELS;

        for (int i = 0; i < SLIDER_COUNT; i++) {
            int y = slidersStartY + i * SLIDER_GAP;
            drawSlider(i, y, labels[i], values[i], SLIDER_MINS[i], SLIDER_MAXS[i], mouseX);
        }

        // Draw recoil duration slider
        int recoilVal = PositionDebugger.getRecoilDurationOverride();
        float recoilDisplay = recoilVal >= 0 ? recoilVal : 100f;
        drawSlider(
            RECOIL_SLIDER_INDEX,
            recoilSliderY,
            RECOIL_LABEL,
            recoilDisplay,
            DURATION_MIN,
            DURATION_MAX,
            mouseX);
        if (recoilVal < 0) {
            fontRendererObj.drawStringWithShadow("(default)", sliderTrackX + sliderTrackW + 38, recoilSliderY + 1, 0x888888);
        }

        // Draw shooting duration slider
        int shootingVal = PositionDebugger.getShootingDurationOverride();
        float shootingDisplay = shootingVal >= 0 ? shootingVal : 100f;
        drawSlider(
            SHOOTING_SLIDER_INDEX,
            shootingSliderY,
            SHOOTING_LABEL,
            shootingDisplay,
            DURATION_MIN,
            DURATION_MAX,
            mouseX);
        if (shootingVal < 0) {
            fontRendererObj.drawStringWithShadow("(default)", sliderTrackX + sliderTrackW + 38, shootingSliderY + 1, 0x888888);
        }

        // Draw camera recoil duration slider
        int camRecoilVal = PositionDebugger.getCameraRecoilDurationOverride();
        float camRecoilDisplay = camRecoilVal >= 0 ? camRecoilVal : 50f;
        drawSlider(
            CAM_RECOIL_SLIDER_INDEX,
            camRecoilSliderY,
            CAM_RECOIL_LABEL,
            camRecoilDisplay,
            DURATION_MIN,
            DURATION_MAX,
            mouseX);
        if (camRecoilVal < 0) {
            fontRendererObj.drawStringWithShadow("(default)", sliderTrackX + sliderTrackW + 38, camRecoilSliderY + 1, 0x888888);
        }

        // Draw generated code
        String code = PositionDebugger.generateCode(current);
        String[] lines = code.split("\n");
        int codeY = camRecoilSliderY + SLIDER_GAP + 6;
        fontRendererObj.drawString("Generated Code:", panelX, codeY, 0xFFFF00);
        codeY += 11;
        for (String line : lines) {
            fontRendererObj.drawStringWithShadow(line, panelX + 2, codeY, 0x88FF88);
            codeY += 9;
        }

        // Status
        fontRendererObj.drawStringWithShadow(
            "Debug: ON  |  Mode: " + current.displayName,
            panelX,
            height - 12,
            0x55FF55);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawSlider(int index, int y, String label, float value, float min, float max, int mouseX) {
        // Label
        fontRendererObj.drawStringWithShadow(label, panelX, y + 1, 0xCCCCCC);

        // Track background
        drawRect(sliderTrackX, y, sliderTrackX + sliderTrackW, y + TRACK_H, 0xFF333333);
        drawRect(sliderTrackX, y, sliderTrackX + sliderTrackW, y + 1, 0xFF555555);
        drawRect(sliderTrackX, y, sliderTrackX + 1, y + TRACK_H, 0xFF555555);

        // Zero line marker
        if (min < 0 && max > 0) {
            float zeroRatio = -min / (max - min);
            int zeroX = sliderTrackX + (int) (zeroRatio * sliderTrackW);
            drawRect(zeroX, y, zeroX + 1, y + TRACK_H, 0xFF666666);
        }

        // Handle
        float ratio = (value - min) / (max - min);
        ratio = Math.max(0, Math.min(1, ratio));
        int handleX = sliderTrackX + (int) (ratio * (sliderTrackW - 4));
        int handleColor = dragSlider == index ? 0xFFFFFF55 : 0xFFFFFFFF;
        drawRect(handleX, y, handleX + 4, y + TRACK_H, handleColor);

        // Value text
        boolean isDurationSlider = index == RECOIL_SLIDER_INDEX || index == SHOOTING_SLIDER_INDEX
            || index == CAM_RECOIL_SLIDER_INDEX;
        String fmt = isDurationSlider ? "%.0f" : (index < 3 ? "%.3f" : "%.1f");
        String valueStr = String.format(fmt, value);
        fontRendererObj.drawStringWithShadow(valueStr, sliderTrackX + sliderTrackW + 4, y + 1, 0xAAAAFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id >= 100 && button.id < 100 + Mode.values().length) {
            PositionDebugger.setCurrentMode(Mode.values()[button.id - 100]);
        } else if (button.id == BTN_SAVE) {
            PositionDebugger.save();
        } else if (button.id == BTN_COPY) {
            String code = PositionDebugger.generateCode(PositionDebugger.getCurrentMode());
            try {
                Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(code), null);
            } catch (Exception ignored) {}
        } else if (button.id == BTN_DISABLE) {
            PositionDebugger.setActive(false);
            mc.displayGuiScreen(null);
        } else if (button.id == BTN_COPY_VALUES) {
            TransformState src = PositionDebugger.getState(PositionDebugger.getCurrentMode());
            clipboard = new TransformState();
            clipboard.copyFrom(src);
        } else if (button.id == BTN_PASTE_VALUES) {
            if (clipboard != null) {
                PositionDebugger.getState(PositionDebugger.getCurrentMode())
                    .copyFrom(clipboard);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = 0; i < SLIDER_COUNT; i++) {
            int y = slidersStartY + i * SLIDER_GAP;
            if (mouseX >= sliderTrackX && mouseX <= sliderTrackX + sliderTrackW
                && mouseY >= y
                && mouseY <= y + TRACK_H) {
                if (mouseButton == 0) {
                    dragSlider = i;
                    updateSliderFromMouse(i, mouseX);
                } else if (mouseButton == 1) {
                    // Right-click resets to 0 (or 1 for scale)
                    setSliderValue(i, i == 6 ? 1.0f : 0.0f);
                }
                return;
            }
        }

        // Recoil duration slider
        if (mouseX >= sliderTrackX && mouseX <= sliderTrackX + sliderTrackW
            && mouseY >= recoilSliderY
            && mouseY <= recoilSliderY + TRACK_H) {
            if (mouseButton == 0) {
                dragSlider = RECOIL_SLIDER_INDEX;
                updateDurationFromMouse(RECOIL_SLIDER_INDEX, mouseX);
            } else if (mouseButton == 1) {
                PositionDebugger.setRecoilDurationOverride(-1);
            }
            return;
        }

        // Shooting duration slider
        if (mouseX >= sliderTrackX && mouseX <= sliderTrackX + sliderTrackW
            && mouseY >= shootingSliderY
            && mouseY <= shootingSliderY + TRACK_H) {
            if (mouseButton == 0) {
                dragSlider = SHOOTING_SLIDER_INDEX;
                updateDurationFromMouse(SHOOTING_SLIDER_INDEX, mouseX);
            } else if (mouseButton == 1) {
                PositionDebugger.setShootingDurationOverride(-1);
            }
            return;
        }

        // Camera recoil duration slider
        if (mouseX >= sliderTrackX && mouseX <= sliderTrackX + sliderTrackW
            && mouseY >= camRecoilSliderY
            && mouseY <= camRecoilSliderY + TRACK_H) {
            if (mouseButton == 0) {
                dragSlider = CAM_RECOIL_SLIDER_INDEX;
                updateDurationFromMouse(CAM_RECOIL_SLIDER_INDEX, mouseX);
            } else if (mouseButton == 1) {
                PositionDebugger.setCameraRecoilDurationOverride(-1);
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (dragSlider >= 0 && clickedMouseButton == 0) {
            if (dragSlider == RECOIL_SLIDER_INDEX || dragSlider == SHOOTING_SLIDER_INDEX
                || dragSlider == CAM_RECOIL_SLIDER_INDEX) {
                updateDurationFromMouse(dragSlider, mouseX);
            } else {
                updateSliderFromMouse(dragSlider, mouseX);
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            for (int i = 0; i < SLIDER_COUNT; i++) {
                int y = slidersStartY + i * SLIDER_GAP;
                if (mouseX >= panelX && mouseX <= width && mouseY >= y && mouseY <= y + TRACK_H) {
                    float step = SLIDER_STEPS[i];
                    if (isShiftKeyDown()) step *= 0.1f;
                    float current = getSliderValue(i);
                    float newVal = current + (scroll > 0 ? step : -step);
                    newVal = Math.max(SLIDER_MINS[i], Math.min(SLIDER_MAXS[i], newVal));
                    setSliderValue(i, newVal);
                    return;
                }
            }
            // Recoil duration slider scroll
            if (mouseX >= panelX && mouseX <= width && mouseY >= recoilSliderY
                && mouseY <= recoilSliderY + TRACK_H) {
                scrollDurationSlider(scroll, RECOIL_SLIDER_INDEX);
                return;
            }
            // Shooting duration slider scroll
            if (mouseX >= panelX && mouseX <= width && mouseY >= shootingSliderY
                && mouseY <= shootingSliderY + TRACK_H) {
                scrollDurationSlider(scroll, SHOOTING_SLIDER_INDEX);
                return;
            }
            // Camera recoil duration slider scroll
            if (mouseX >= panelX && mouseX <= width && mouseY >= camRecoilSliderY
                && mouseY <= camRecoilSliderY + TRACK_H) {
                scrollDurationSlider(scroll, CAM_RECOIL_SLIDER_INDEX);
            }
        }
    }

    private void updateSliderFromMouse(int index, int mouseX) {
        float ratio = (float) (mouseX - sliderTrackX) / (float) sliderTrackW;
        ratio = Math.max(0, Math.min(1, ratio));
        float value = SLIDER_MINS[index] + ratio * (SLIDER_MAXS[index] - SLIDER_MINS[index]);
        // Snap to step
        float step = SLIDER_STEPS[index];
        if (isShiftKeyDown()) step *= 0.1f;
        value = Math.round(value / step) * step;
        setSliderValue(index, value);
    }

    private void updateDurationFromMouse(int sliderIndex, int mouseX) {
        float ratio = (float) (mouseX - sliderTrackX) / (float) sliderTrackW;
        ratio = Math.max(0, Math.min(1, ratio));
        float value = DURATION_MIN + ratio * (DURATION_MAX - DURATION_MIN);
        float step = isShiftKeyDown() ? 1f : DURATION_STEP;
        value = Math.round(value / step) * step;
        setDurationOverride(sliderIndex, Math.round(value));
    }

    private void scrollDurationSlider(int scroll, int sliderIndex) {
        float step = DURATION_STEP;
        if (isShiftKeyDown()) step = 1f;
        int current = getDurationOverride(sliderIndex);
        if (current < 0) current = 50;
        float newVal = current + (scroll > 0 ? step : -step);
        newVal = Math.max(DURATION_MIN, Math.min(DURATION_MAX, newVal));
        setDurationOverride(sliderIndex, Math.round(newVal));
    }

    private int getDurationOverride(int sliderIndex) {
        if (sliderIndex == RECOIL_SLIDER_INDEX) return PositionDebugger.getRecoilDurationOverride();
        if (sliderIndex == SHOOTING_SLIDER_INDEX) return PositionDebugger.getShootingDurationOverride();
        return PositionDebugger.getCameraRecoilDurationOverride();
    }

    private void setDurationOverride(int sliderIndex, int value) {
        if (sliderIndex == RECOIL_SLIDER_INDEX) {
            PositionDebugger.setRecoilDurationOverride(value);
        } else if (sliderIndex == SHOOTING_SLIDER_INDEX) {
            PositionDebugger.setShootingDurationOverride(value);
        } else {
            PositionDebugger.setCameraRecoilDurationOverride(value);
        }
    }

    private float getSliderValue(int index) {
        TransformState s = PositionDebugger.getState(PositionDebugger.getCurrentMode());
        switch (index) {
            case 0: return s.translateX;
            case 1: return s.translateY;
            case 2: return s.translateZ;
            case 3: return s.rotateX;
            case 4: return s.rotateY;
            case 5: return s.rotateZ;
            case 6: return s.scale;
            default: return 0;
        }
    }

    private void setSliderValue(int index, float value) {
        TransformState s = PositionDebugger.getState(PositionDebugger.getCurrentMode());
        switch (index) {
            case 0: s.translateX = value; break;
            case 1: s.translateY = value; break;
            case 2: s.translateZ = value; break;
            case 3: s.rotateX = value; break;
            case 4: s.rotateY = value; break;
            case 5: s.rotateZ = value; break;
            case 6: s.scale = value; break;
        }
    }

    private float[] getValues(TransformState s) {
        return new float[] { s.translateX, s.translateY, s.translateZ, s.rotateX, s.rotateY, s.rotateZ, s.scale };
    }

    @Override
    public void onGuiClosed() {
        PositionDebugger.setScreenOpen(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

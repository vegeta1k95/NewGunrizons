package com.gtnewhorizon.newgunrizons.client.animation;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Utility for capturing, loading, and interpolating OpenGL modelview matrices.
 * Used by the animation system to blend between transform states during transitions.
 */
public class MatrixHelper {

    /**
     * Multiplies the current modelview matrix by the given matrix.
     */
    public static void applyMatrix(Matrix4f m) {
        if (m != null) {
            FloatBuffer buf = BufferUtils.createFloatBuffer(16);
            m.store(buf);
            buf.rewind();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glMultMatrix(buf);
        }
    }

    /**
     * Replaces the current modelview matrix with the given matrix.
     */
    public static void loadMatrix(Matrix4f m) {
        if (m != null) {
            FloatBuffer buf = BufferUtils.createFloatBuffer(16);
            m.store(buf);
            buf.rewind();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadMatrix(buf);
        }
    }

    /**
     * Captures the current modelview matrix and returns it as a Matrix4f.
     */
    public static Matrix4f captureMatrix() {
        FloatBuffer buf = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, buf);
        buf.rewind();
        Matrix4f matrix = new Matrix4f();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        matrix.load(buf);
        return matrix;
    }

    /**
     * Scales every element of the matrix by the given factor.
     * Used for linear interpolation between two matrices: result = A*(1-t) + B*t.
     */
    public static Matrix4f interpolateMatrix(Matrix4f m, float factor) {
        Matrix4f result = new Matrix4f();
        result.m00 = m.m00 * factor;
        result.m01 = m.m01 * factor;
        result.m02 = m.m02 * factor;
        result.m03 = m.m03 * factor;
        result.m10 = m.m10 * factor;
        result.m11 = m.m11 * factor;
        result.m12 = m.m12 * factor;
        result.m13 = m.m13 * factor;
        result.m20 = m.m20 * factor;
        result.m21 = m.m21 * factor;
        result.m22 = m.m22 * factor;
        result.m23 = m.m23 * factor;
        result.m30 = m.m30 * factor;
        result.m31 = m.m31 * factor;
        result.m32 = m.m32 * factor;
        result.m33 = m.m33 * factor;
        return result;
    }
}

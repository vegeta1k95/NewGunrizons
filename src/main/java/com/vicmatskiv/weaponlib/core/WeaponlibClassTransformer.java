package com.vicmatskiv.weaponlib.core;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import com.vicmatskiv.weaponlib.ClassInfo;

public class WeaponlibClassTransformer implements IClassTransformer {

    private static final ClassInfo entityRendererClassInfo = new ClassInfo(
        "net/minecraft/client/renderer/EntityRenderer", "blt")
        .addMethodInfo("hurtCameraEffect", "(F)V", "f")
        .addMethodInfo("setupCameraTransform", "(FI)V", "a")
        .addMethodInfo("setupViewBobbing", "(F)V", "g");
    private static final ClassInfo renderBipedClassInfo = new ClassInfo(
        "net/minecraft/client/renderer/entity/RenderBiped", "bnw")
        .addMethodInfo2("renderEquippedItems", "(Lnet/minecraft/entity/EntityLiving;F)V", "a", "(Lsw;F)V");
    private static final ClassInfo itemBlockClassInfo = new ClassInfo("net/minecraft/item/ItemBlock", "abh");

    public byte[] transform(String par1, String className, byte[] bytecode) {
        if (!entityRendererClassInfo.classMatches(className)
            && (renderBipedClassInfo == null || !renderBipedClassInfo.classMatches(className))) {
            return bytecode;
        } else {
            ClassReader cr = new ClassReader(bytecode);
            ClassWriter cw = new ClassWriter(cr, 1);
            WeaponlibClassTransformer.CVTransform cv = new WeaponlibClassTransformer.CVTransform(cw);
            cr.accept(cv, 0);
            return cw.toByteArray();
        }
    }

    private static class CVTransform extends ClassVisitor {

        String classname;

        public CVTransform(ClassVisitor cv) {
            super(262144, cv);
        }

        public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) {
            this.classname = name;
            this.cv.visit(version, access, name, signature, superName, interfaces);
        }

        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (WeaponlibClassTransformer.entityRendererClassInfo
                .methodMatches("setupCameraTransform", "(FI)V", this.classname, name, desc)) {
                return new WeaponlibClassTransformer.SetupCameraTransformMethodVisitor(
                    this.cv.visitMethod(access, name, desc, signature, exceptions));
            } else {
                return (WeaponlibClassTransformer.renderBipedClassInfo != null
                    && WeaponlibClassTransformer.renderBipedClassInfo.methodMatches(
                        "renderEquippedItems",
                        "(Lnet/minecraft/entity/EntityLiving;F)V",
                        this.classname,
                        name,
                        desc)
                            ? new WeaponlibClassTransformer.RenderEquippedItemsMethodVisitor(
                                this.cv.visitMethod(access, name, desc, signature, exceptions))
                            : this.cv.visitMethod(access, name, desc, signature, exceptions));
            }
        }
    }

    private static class RenderEquippedItemsMethodVisitor extends MethodVisitor {

        public RenderEquippedItemsMethodVisitor(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitTypeInsn(int opcode, String type) {
            if (opcode != 193 || !type.equals(WeaponlibClassTransformer.itemBlockClassInfo.getMcpClassName())
                && !type.equals(WeaponlibClassTransformer.itemBlockClassInfo.getNotchClassName())) {
                super.visitTypeInsn(opcode, type);
            } else {
                this.mv.visitMethodInsn(
                    184,
                    "com/vicmatskiv/weaponlib/Interceptors",
                    "is3dRenderableItem",
                    "(Lnet/minecraft/item/Item;)Z",
                    false);
            }

        }
    }

    private static class SetupCameraTransformMethodVisitor extends MethodVisitor {

        public SetupCameraTransformMethodVisitor(MethodVisitor mv) {
            super(262144, mv);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
            if (WeaponlibClassTransformer.entityRendererClassInfo
                .methodMatches("hurtCameraEffect", "(F)V", owner, name, desc)) {
                super.visitMethodInsn(opcode, owner, name, desc);
                this.mv.visitVarInsn(23, 1);
                this.mv.visitMethodInsn(
                    184,
                    "com/vicmatskiv/weaponlib/Interceptors",
                    "setupCameraTransformAfterHurtCameraEffect",
                    "(F)V");
            } else {
                this.mv.visitMethodInsn(opcode, owner, name, desc);
            }
        }

        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
        }
    }

}

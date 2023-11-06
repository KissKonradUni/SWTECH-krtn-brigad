package hu.krtn.brigad.test.shadingtest;

import hu.krtn.brigad.engine.rendering.Material;
import hu.krtn.brigad.engine.rendering.Shader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;

import java.util.function.Supplier;

public class ShellShader extends Shader {
    int layer = 0;

    public ShellShader(String vertexShaderPath, String fragmentShaderPath, int layer) {
        super(vertexShaderPath, fragmentShaderPath);
        this.layer = layer;
    }

    @Override
    public void bind(Supplier<Matrix4f> modelMatrixSupplier, Material material) {
        super.bind(modelMatrixSupplier, material);

        GL33.glUniform1i(this.getUniformLocation("f_layer"), layer);
    }
}

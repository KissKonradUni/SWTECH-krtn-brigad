package hu.krtn.brigad.engine.rendering;

import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.window.Logger;
import org.joml.Matrix4f;

import java.util.function.Supplier;

import static org.lwjgl.opengl.GL32.*;

public class Shader {

    public enum ShaderTypes {
        VERTEX_SHADER(GL_VERTEX_SHADER),
        FRAGMENT_SHADER(GL_FRAGMENT_SHADER),
        GEOMETRY_SHADER(GL_GEOMETRY_SHADER);

        private final int glType;

        ShaderTypes(int glType) {
            this.glType = glType;
        }

        public int getGlType() {
            return glType;
        }
    }

    private final int handle;

    private Supplier<Matrix4f> modelMatrixSupplier;

    public Shader(String vertexShader, String fragmentShader) {
        handle = glCreateProgram();
        int vertexShaderHandle   = createShader(vertexShader  , ShaderTypes.VERTEX_SHADER  );
        int fragmentShaderHandle = createShader(fragmentShader, ShaderTypes.FRAGMENT_SHADER);
        glAttachShader(handle, vertexShaderHandle);
        glAttachShader(handle, fragmentShaderHandle);
        glLinkProgram(handle);

        if (glGetProgrami(handle, GL_LINK_STATUS) == GL_FALSE) {
            Logger.error("Program linking failed: " + glGetProgramInfoLog(handle));
            return;
        }

        glValidateProgram(handle);
        glUseProgram(handle);

        glDeleteShader(vertexShaderHandle  );
        glDeleteShader(fragmentShaderHandle);
    }

    private int createShader(String shaderSource, ShaderTypes shaderType) {
        int shader = glCreateShader(shaderType.getGlType());
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            Logger.error("Shader compilation failed: " + glGetShaderInfoLog(shader));
            return -1;
        }

        return shader;
    }

    public void bind() {
        glUseProgram(handle);
        if (modelMatrixSupplier != null)
            glUniformMatrix4fv(glGetUniformLocation(handle, "model"), false, modelMatrixSupplier.get().get(new float[16]));
        if (CameraComponent.getActiveCamera() != null) {
            glUniformMatrix4fv(glGetUniformLocation(handle, "proj"), false, CameraComponent.getActiveCamera().getProjectionMatrix().get(new float[16]));
            glUniformMatrix4fv(glGetUniformLocation(handle, "view"), false, CameraComponent.getActiveCamera().getViewMatrix().get(new float[16]));
        }
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        glDeleteProgram(handle);
    }

    public void setModelMatrixSupplier(Supplier<Matrix4f> modelMatrixSupplier) {
        this.modelMatrixSupplier = modelMatrixSupplier;
    }
}

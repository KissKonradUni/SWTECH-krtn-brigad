package hu.krtn.brigad.engine.rendering.shading;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.rendering.Material;
import hu.krtn.brigad.engine.rendering.Mesh;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.window.Logger;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL32.*;

/**
 * The Shader class is responsible for loading and managing OpenGL shaders.
 * @see Mesh
 */
public class Shader {

    /**
     * The currently active shader.
     * It is used to prevent unnecessary shader switches.
     */
    private static int activeShader = -1;

    private List<Entity> lightsCache = new ArrayList<>();
    private final List<LightComponent> compCache = new ArrayList<>();

    private String vertexShaderPath;
    private String fragmentShaderPath;

    public void setLights(List<Entity> lightsCache) {
        if (this.lightsCache.equals(lightsCache)) return;

        this.lightsCache = lightsCache;

        compCache.clear();
        for (Entity entity : lightsCache) {
            LightComponent light = (LightComponent) entity.getComponent(LightComponent.class);
            if (light == null) continue;

            compCache.add(light);
        }
    }

    /**
     * The ShaderTypes enum is used to specify the type of the shader.
     * Vertex - A shader that is executed for every vertex.
     * Fragment - A shader that is executed for every fragment.
     * Geometry - A shader that is executed for every primitive.
     */
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

    /**
     * A map that stores the locations of the uniforms.
     * This is used to prevent unnecessary calls to glGetUniformLocation.
     */
    private final HashMap<String, Integer> uniforms = new HashMap<>();

    /**
     * Creates a shader program from the given vertex and fragment shaders.
     * @param vertexShader The vertex shader.
     * @param fragmentShader The fragment shader.
     * @param raw Redundant parameter, used to differentiate between the constructors.
     */
    private Shader(String vertexShader, String fragmentShader, boolean raw) {
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

    /**
     * Creates a shader program from the given vertex and fragment shader files from the given paths.
     * @param vertexShaderPath The path of the vertex shader.
     * @param fragmentShaderPath The path of the fragment shader.
     */
    public Shader(String vertexShaderPath, String fragmentShaderPath) {
        this(
            ResourceManager.getInstance().readTextFile(vertexShaderPath),
            ResourceManager.getInstance().readTextFile(fragmentShaderPath),
            false
        );

        this.vertexShaderPath = vertexShaderPath;
        this.fragmentShaderPath = fragmentShaderPath;
    }

    /**
     * Creates a shader.
     * @param shaderSource The source code of the shader.
     * @param shaderType The type of the shader.
     * @return The handle of the shader.
     */
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

    /**
     * Binds the shader.
     *
     * @param modelMatrixSupplier A supplier that returns the model matrix of the entity.
     * @param material
     */
    public void bind(Supplier<Matrix4f> modelMatrixSupplier, Material material) {
        if (activeShader != handle) {
            glUseProgram(handle);
            activeShader = handle;
        }
        if (modelMatrixSupplier != null)
            glUniformMatrix4fv(getUniformLocation("model"), false, modelMatrixSupplier.get().get(new float[16]));
        CameraComponent camera = CameraComponent.getActiveCamera();
        if (camera != null) {
            glUniformMatrix4fv(getUniformLocation("projection"), false, camera.getProjectionMatrix().get(new float[16]));
            glUniformMatrix4fv(getUniformLocation("view"), false, camera.getViewMatrix().get(new float[16]));

            int viewPosLoc = getUniformLocation("viewPos");
            if (viewPosLoc != -1)
                glUniform3f(viewPosLoc, camera.getTransform().getPosition().x, camera.getTransform().getPosition().y, camera.getTransform().getPosition().z);
        }

        // TODO: this is dogsh*t
        //glUniform3f(getUniformLocation("PBRData.albedo"),    material.getBaseColor().x, material.getBaseColor().y, material.getBaseColor().z);
        glUniform1i(getUniformLocation("albedo"), 0);
        glUniform1f(getUniformLocation("f_PBRData.metallic"),         material.getMetallic());
        glUniform1f(getUniformLocation("f_PBRData.roughness"),        material.getRoughness());
        glUniform1f(getUniformLocation("f_PBRData.ambientOcclusion"), material.getAmbientOcclusion());
        glUniform1f(getUniformLocation("f_PBRData.emission"),         material.getEmission());

        int lightCount = lightsCache.size();
        glUniform1i(getUniformLocation("f_numLights"), lightCount);

        for (int i = 0; i < lightCount; i++) {
            LightComponent light = compCache.get(i);
            glUniform3f(getUniformLocation("f_lights[" + i + "].position"), light.getPosition().x, light.getPosition().y, light.getPosition().z);
            glUniform3f(getUniformLocation("f_lights[" + i + "].color"),    light.getColor().x, light.getColor().y, light.getColor().z);
            glUniform1f(getUniformLocation("f_lights[" + i + "].intensity"), light.getIntensity());
        }
    }

    /**
     * Unbinds the shader.
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * Destroys the shader.
     */
    public void destroy() {
        glDeleteProgram(handle);
    }

    /**
     * Returns the location of the uniform with the given name.
     * @param name The name of the uniform.
     * @return The location of the uniform.
     */
    protected int getUniformLocation(String name) {
        if (uniforms.containsKey(name)) {
            return uniforms.get(name);
        }
        int loc = glGetUniformLocation(handle, name);
        uniforms.put(name, loc);
        return loc;
    }

    public String getVertexShaderPath() {
        return vertexShaderPath;
    }

    public String getFragmentShaderPath() {
        return fragmentShaderPath;
    }
}

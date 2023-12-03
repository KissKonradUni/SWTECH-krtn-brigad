package hu.krtn.brigad.engine.ecs.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.ComponentDependencyException;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.rendering.*;
import hu.krtn.brigad.engine.resources.ResourceManager;
import org.joml.Vector4f;

import java.util.List;

import static hu.krtn.brigad.engine.resources.ResourceManager.*;

/**
 * The renderer component is used to render meshes.
 * It contains a mesh and a shader.
 */
public class StaticModelRendererComponent extends Component {

    private Mesh mesh;
    private Material material;
    private Texture diffuseTexture;
    private Shader shader;

    private TransformComponent transformComponent;

    private String meshPath;
    private String diffuseTexturePath;
    private String vertexShaderPath;
    private String fragmentShaderPath;

    private boolean alphaBlending = false;

    /**
     * The constructor of the renderer component.
     * @param mesh The mesh to be rendered.
     * @param shader The shader to be used to render the mesh.
     */
    private StaticModelRendererComponent(Mesh mesh, Material material, Texture diffuseTexture, Shader shader) {
        super();
        this.mesh = mesh;
        this.material = material;
        this.diffuseTexture = diffuseTexture;
        this.shader = shader;

        if (!LogicManager.getInstance().isLogicPresent(RendererLogic.class))
            LogicManager.getInstance().registerLogic(new RendererLogic());

        this.meshPath = null;
        this.diffuseTexturePath = null;
        this.vertexShaderPath = null;
        this.fragmentShaderPath = null;
    }

    public StaticModelRendererComponent(StaticModelData data, Texture diffuseTexture, Shader shader) {
        this(data.mesh, data.material, diffuseTexture, shader);

        this.meshPath = data.path;
        this.diffuseTexturePath = diffuseTexture.getPath();
        this.vertexShaderPath = shader.getVertexShaderPath();
        this.fragmentShaderPath = shader.getFragmentShaderPath();
    }

    public StaticModelRendererComponent() {
        this(null, null, null, null);
    }

    public StaticModelRendererComponent setAlphaBlending(boolean alphaBlending) {
        this.alphaBlending = alphaBlending;
        return this;
    }

    public boolean isAlphaBlending() {
        return alphaBlending;
    }

    @Override
    public String serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("mesh", meshPath);
        object.addProperty("diffuseTexture", diffuseTexturePath);
        object.addProperty("vertexShader", vertexShaderPath);
        object.addProperty("fragmentShader", fragmentShaderPath);

        object.addProperty("type", this.getType());

        return object.toString();
    }

    @Override
    public void deserialize(String data) {
        JsonObject object = JsonParser.parseString(data).getAsJsonObject();

        meshPath = object.get("mesh").getAsString();
        JsonElement diffuseTextureObject = object.get("diffuseTexture");
        if (diffuseTextureObject != null)
            diffuseTexturePath = diffuseTextureObject.getAsString();
        vertexShaderPath = object.get("vertexShader").getAsString();
        fragmentShaderPath = object.get("fragmentShader").getAsString();

        StaticModelData[] modelData = ResourceManager.getInstance().loadStaticModel(meshPath);
        mesh = modelData[0].mesh;
        material = modelData[0].material;
        diffuseTexture = (diffuseTextureObject != null) ? ResourceManager.getInstance().loadTexture(diffuseTexturePath) : new Texture(new Texture.ByteColor(new Vector4f(1.0f)));
        shader = ResourceManager.getInstance().loadShader(vertexShaderPath, fragmentShaderPath);
    }

    public void bind() {
        mesh.bind();
        diffuseTexture.bind(0);
        shader.bind(transformComponent::getModelMatrix, material);
    }

    public Mesh getMesh() {
        return mesh;
    }

    /**
     * The renderer component depends on the transform component.
     * It requires the model matrix to be calculated, which is done by the transform component.
     * @see TransformComponent
     * @return The dependencies of the component.
     */
    @Override
    public Class<? extends Component>[] getDependencies() {
        //noinspection unchecked
        return new Class[] { TransformComponent.class };
    }

    /**
     * This method is used to fulfill the dependencies of the component.
     * @param entity The entity that the component is attached to.
     * @throws ComponentDependencyException If a dependency is missing.
     */
    @Override
    public void fulfillDependencies(Entity entity) throws ComponentDependencyException {
        transformComponent = (TransformComponent) entity.getComponent(TransformComponent.class);
        if (transformComponent == null) {
            throw new ComponentDependencyException(TransformComponent.class);
        }
    }

    /**
     * Sets the lights for the shader.
     * @param lightsCache The lights to be set.
     */
    public void setLights(List<Entity> lightsCache) {
        shader.setLights(lightsCache);
    }
}

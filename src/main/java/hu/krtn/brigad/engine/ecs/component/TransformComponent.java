package hu.krtn.brigad.engine.ecs.component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityManager;
import hu.krtn.brigad.engine.window.Logger;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A component that stores the position, rotation and scale of an entity.
 */
public class TransformComponent extends Component {

    private Entity parentEntity = null;
    private TransformComponent parent = null;

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public TransformComponent(Vector3f position, Vector3f rotation, Vector3f scale, Entity parent) {
        this.position = position;
        this.rotation = rotation;
        this.scale    = scale;

        if (parent != null)
            this.setParent(parent);
    }

    public TransformComponent(Vector3f position, Vector3f rotation, Vector3f scale) {
        this(position, rotation, scale, null);
    }

    /**
     * Creates a new TransformComponent with default values.
     */
    public TransformComponent() {
        this(
            new Vector3f(),
            new Vector3f(),
            new Vector3f(1, 1, 1)
        );
    }

    /**
     * Serializes the component to a JSON string.
     * @return The JSON string.
     */
    @Override
    public String serialize() {
        JsonObject object = new JsonObject();

        if (parentEntity != null)
            object.addProperty("parent", parentEntity.getHashId());
        object.add("position", new Gson().toJsonTree(position));
        object.add("rotation", new Gson().toJsonTree(rotation));
        object.add("scale", new Gson().toJsonTree(scale));
        object.addProperty("type", this.getType());

        return new Gson().toJson(object);
    }

    /**
     * Deserializes the component from a JSON string.
     * @param data The JSON string.
     */
    @Override
    public void deserialize(String data) {
        JsonObject object = JsonParser.parseString(data).getAsJsonObject();
        this.position = new Gson().fromJson(object.get("position"), Vector3f.class);
        this.rotation = new Gson().fromJson(object.get("rotation"), Vector3f.class);
        this.scale    = new Gson().fromJson(object.get("scale")   , Vector3f.class);

        JsonElement parentHashId = object.get("parent");
        if (parentHashId != null) {
            String parentId = Entity.resolveDeserializedEntityHashId(parentHashId.getAsString());
            Entity parent = EntityManager.getInstance().getEntityByHashId(parentId);
            if (parent == null) {
                Logger.error("Parent entity with hash id \"" + parentId + "\" not found!");
                return;
            }
            this.setParent(parent);
        }
    }

    /**
     * Returns the parent of the entity.
     * @return The parent.
     */
    public TransformComponent getParent() {
        return parent;
    }

    /**
     * Sets the parent of the entity.
     * The parent is used to create a hierarchy of entities.
     * The position, rotation and scale of the entity will be relative to its parent.
     * @param parent The parent.
     * @return The component itself.
     */
    public TransformComponent setParent(Entity parent) {
        this.parent = (TransformComponent) parent.getComponent(TransformComponent.class);
        if (this.parent == null) {
            throw new RuntimeException("The parent entity does not have a TransformComponent!");
        }
        this.parentEntity = parent;

        return this;
    }

    /**
     * Returns the parent entity.
     * @return The parent entity.
     */
    public Entity getParentEntity() {
        return parentEntity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    /**
     * Returns the world position of the entity.
     * @return The world position.
     */
    public Vector3f getWorldPosition() {
        return getModelMatrix().transformPosition(new Vector3f());
    }

    /**
     * Returns the model matrix of the entity.
     * The model matrix is used to transform the vertices of the entity regarding its position, rotation and scale.
     * @return The model matrix.
     */
    public Matrix4f getModelMatrix() {
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix = modelMatrix.identity();
        modelMatrix = modelMatrix.translate(position);
        modelMatrix = modelMatrix.rotateX((float) Math.toRadians(rotation.x));
        modelMatrix = modelMatrix.rotateY((float) Math.toRadians(rotation.y));
        modelMatrix = modelMatrix.rotateZ((float) Math.toRadians(rotation.z));
        modelMatrix = modelMatrix.scale(scale);
        return parent != null ? parent.getModelMatrix().mul(modelMatrix) : modelMatrix;
    }

}

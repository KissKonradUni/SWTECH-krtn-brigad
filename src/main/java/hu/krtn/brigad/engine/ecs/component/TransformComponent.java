package hu.krtn.brigad.engine.ecs.component;

import com.google.gson.Gson;
import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A component that stores the position, rotation and scale of an entity.
 */
public class TransformComponent extends Component {

    private TransformComponent parent = null;

    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public TransformComponent(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale    = scale;
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
        return new Gson().toJson(this);
    }

    /**
     * Deserializes the component from a JSON string.
     * @param data The JSON string.
     */
    @Override
    public void deserialize(String data) {
        TransformComponent tc = new Gson().fromJson(data, TransformComponent.class);
        this.position = tc.position;
        this.rotation = tc.rotation;
        this.scale    = tc.scale;
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

        return this;
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
     * Returns the model matrix of the entity.
     * The model matrix is used to transform the vertices of the entity regarding to it's position, rotation and scale.
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

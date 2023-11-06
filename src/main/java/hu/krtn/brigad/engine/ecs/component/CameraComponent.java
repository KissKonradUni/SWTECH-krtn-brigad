package hu.krtn.brigad.engine.ecs.component;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.ComponentDependencyException;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.window.Window;
import org.joml.Matrix4f;

/**
 * The CameraComponent class represents a camera in the scene.
 * It is used to calculate the view and projection matrices.
 */
public class CameraComponent extends Component {

    /**
     * The field of view of the camera in degrees.
     */
    private float fieldOfView = 70.0f;
    /**
     * The near plane of the camera.
     */
    private float nearPlane = 0.1f;
    /**
     * The far plane of the camera.
     */
    private float farPlane = 1000.0f;

    /**
     * The projection matrix of the camera.
     */
    private Matrix4f projectionMatrix;

    /**
     * The transform component of the camera.
     */
    private TransformComponent transformComponent;

    /**
     * The active camera in the scene.
     */
    private static CameraComponent activeCamera = null;

    public CameraComponent(float fieldOfView, float nearPlane, float farPlane, boolean active) {
        super();
        float aspectRatio = Window.getInstance().getAspectRatio();

        this.fieldOfView = fieldOfView;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;

        recalculateProjectionMatrix(aspectRatio);

        if (activeCamera == null || active) {
            activeCamera = this;
        }
    }

    public CameraComponent(float fieldOfView, float nearPlane, float farPlane) {
        this(fieldOfView, nearPlane, farPlane, false);
    }

    public CameraComponent() {
        this(70.0f, 0.1f, 1000.0f, false);
    }

    /**
     * Recalculates the projection matrix of the camera.
     * @param aspectRatio The aspect ratio of the window.
     */
    public void recalculateProjectionMatrix(float aspectRatio) {
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(this.fieldOfView), aspectRatio, nearPlane, farPlane);
    }

    /**
     * Gets the active camera in the scene.
     * @return The active camera in the scene.
     */
    public static CameraComponent getActiveCamera() {
        return activeCamera;
    }

    /**
     * Sets the active camera in the scene.
     */
    public void setActive() {
        activeCamera = this;
    }

    @Override
    public String serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("fieldOfView", fieldOfView);
        object.addProperty("nearPlane", nearPlane);
        object.addProperty("farPlane", farPlane);
        object.addProperty("active", activeCamera == this);
        object.addProperty("type", this.getType());

        return object.toString();
    }

    @Override
    public void deserialize(String data) {
        JsonObject object = JsonParser.parseString(data).getAsJsonObject();

        fieldOfView = object.get("fieldOfView").getAsFloat();
        nearPlane = object.get("nearPlane").getAsFloat();
        farPlane = object.get("farPlane").getAsFloat();

        recalculateProjectionMatrix(Window.getInstance().getAspectRatio());

        if (activeCamera == null || object.get("active").getAsBoolean()) {
            activeCamera = this;
        }
    }

    /**
     * Gets the projection matrix of the camera.
     * The projection matrix contains the camera's field of view, aspect ratio, near and far planes.
     * @return The projection matrix of the camera.
     */
    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Gets the view matrix of the camera.
     * The view matrix contains the inverse of the camera's transform, rotation and scale.
     * @return The view matrix of the camera.
     */
    public Matrix4f getViewMatrix() {
        return transformComponent.getModelMatrix().invert();
    }

    /**
     * The CameraComponent depends on the TransformComponent.
     * @return The dependencies of the component.
     */
    @Override
    public Class<? extends Component>[] getDependencies() {
        //noinspection unchecked
        return new Class[]{TransformComponent.class};
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
     * Gets the transform component of the camera.
     * @return The transform component of the camera.
     */
    public TransformComponent getTransform() {
        return transformComponent;
    }

    public float getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
        recalculateProjectionMatrix(Window.getInstance().getAspectRatio());
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
        recalculateProjectionMatrix(Window.getInstance().getAspectRatio());
    }

    public float getFarPlane() {
        return farPlane;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
        recalculateProjectionMatrix(Window.getInstance().getAspectRatio());
    }
}

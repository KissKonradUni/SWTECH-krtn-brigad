package hu.krtn.brigad.engine.ecs.component;

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
    private float FOV = 70.0f;
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

    public CameraComponent() {
        super();
        float aspectRatio = Window.getInstance().getAspectRatio();

        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(FOV), aspectRatio, nearPlane, farPlane);

        if (activeCamera == null) {
            activeCamera = this;
        }
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
        return "{}";
    }

    @Override
    public void deserialize(String data) {

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
}

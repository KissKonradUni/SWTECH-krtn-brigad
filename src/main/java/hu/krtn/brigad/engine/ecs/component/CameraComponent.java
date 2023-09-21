package hu.krtn.brigad.engine.ecs.component;

import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.ComponentDependencyException;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.window.Window;
import org.joml.Matrix4f;

public class CameraComponent extends Component {

    private float FOV = 70.0f;
    private float nearPlane = 0.1f;
    private float farPlane = 1000.0f;

    private Matrix4f projectionMatrix;

    private TransformComponent transformComponent;

    private static CameraComponent activeCamera = null;

    public CameraComponent() {
        super();
        float aspectRatio = Window.getInstance().getAspectRatio();

        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(FOV), aspectRatio, nearPlane, farPlane);

        if (activeCamera == null) {
            activeCamera = this;
        }
    }

    public static CameraComponent getActiveCamera() {
        return activeCamera;
    }

    @Override
    public String serialize() {
        return "{}";
    }

    @Override
    public void deserialize(String data) {

    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getViewMatrix() {
        return transformComponent.getModelMatrix().invert();
    }

    @Override
    public Class<? extends Component>[] getDependencies() {
        //noinspection unchecked
        return new Class[]{TransformComponent.class};
    }

    @Override
    public void fulfillDependencies(Entity entity) throws ComponentDependencyException {
        transformComponent = (TransformComponent) entity.getComponent(TransformComponent.class);
        if (transformComponent == null) {
            throw new ComponentDependencyException(TransformComponent.class);
        }
    }
}

package hu.krtn.brigad.engine.ecs.component;

import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.ComponentDependencyException;
import hu.krtn.brigad.engine.ecs.Entity;
import org.joml.Vector3f;

public class LightComponent extends Component {

    public enum LightType {
        DIRECTIONAL,
        SPOT,
        POINT
    }

    private LightType type;
    private float intensity;
    private Vector3f color;

    private TransformComponent transformComponent;

    public LightComponent(LightType type, float intensity, Vector3f color) {
        this.type = type;
        this.intensity = intensity;
        this.color = color;
    }

    public LightType getLightType() {
        return type;
    }

    public void setLightType(LightType type) {
        this.type = type;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return transformComponent.getWorldPosition();
    }

    @Override
    public String serialize() {
        return "{}";
    }

    @Override
    public void deserialize(String data) {

    }

    @Override
    public Class<? extends Component>[] getDependencies() {
        //noinspection unchecked
        return new Class[]{ TransformComponent.class };
    }

    @Override
    public void fulfillDependencies(Entity entity) throws ComponentDependencyException {
        transformComponent = (TransformComponent) entity.getComponent(TransformComponent.class);
    }

}

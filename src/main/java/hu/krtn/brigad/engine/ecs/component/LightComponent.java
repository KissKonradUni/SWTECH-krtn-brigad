package hu.krtn.brigad.engine.ecs.component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.ComponentDependencyException;
import hu.krtn.brigad.engine.ecs.Entity;
import org.joml.Vector3f;

public class LightComponent extends Component {

    public enum LightType {
        DIRECTIONAL,
        SPOT,
        POINT;

        @Override
        public String toString() {
            return switch (this) {
                case DIRECTIONAL -> "directional";
                case SPOT -> "spot";
                case POINT -> "point";
            };
        }

        public static LightType fromString(String string) {
            return switch (string) {
                case "directional" -> DIRECTIONAL;
                case "spot" -> SPOT;
                case "point" -> POINT;
                default -> throw new IllegalArgumentException("Invalid light type: " + string);
            };
        }
    }

    private LightType lightType;
    private float intensity;
    private Vector3f color;

    private TransformComponent transformComponent;

    public LightComponent(LightType lightType, float intensity, Vector3f color) {
        this.lightType = lightType;
        this.intensity = intensity;
        this.color = color;
    }

    public LightComponent() {
        this(LightType.POINT, 1.0f, new Vector3f(1.0f, 1.0f, 1.0f));
    }

    public LightType getLightType() {
        return lightType;
    }

    public void setLightType(LightType type) {
        this.lightType = type;
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
        JsonObject object = new JsonObject();

        object.addProperty("lightType", lightType.toString());
        object.addProperty("intensity", intensity);
        object.add("color", new Gson().toJsonTree(color));
        object.addProperty("type", this.getType());

        return object.toString();
    }

    @Override
    public void deserialize(String data) {
        JsonObject object = JsonParser.parseString(data).getAsJsonObject();

        lightType = LightType.fromString(object.get("lightType").getAsString());
        intensity = object.get("intensity").getAsFloat();
        color     = new Gson().fromJson(object.get("color"), Vector3f.class);
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

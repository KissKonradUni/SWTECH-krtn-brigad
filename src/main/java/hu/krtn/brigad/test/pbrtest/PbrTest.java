package hu.krtn.brigad.test.pbrtest;

import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.ecs.component.StaticModelRendererComponent;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.rendering.Material;
import hu.krtn.brigad.engine.rendering.Texture;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.resources.ResourceManager.StaticModelData;
import hu.krtn.brigad.engine.window.Window;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PbrTest {

    public static void main(String[] args) {
        Window window = Window.initInstance(1920, 1080, "Test Game", 24.0f, true, false, 16);
        window.init();

        EntityFactory
            .create("Camera")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 5.0f), new Vector3f(), new Vector3f(1.0f)))
            .addComponent(new CameraComponent(50.0f, 0.1f, 1000.0f))
            .buildAndRegister();

        StaticModelData[] modelData = ResourceManager.getInstance().loadStaticModel("./resources/models/shells/Sphere.gltf");

        for (int y = 0; y <= 10; y++) {
            for (int x = 0; x <= 10; x++) {
                Vector4f diffuse = new Vector4f(1.0f, 0.5f, 0.5f, 1.0f);
                StaticModelData data = new StaticModelData(modelData[0].mesh, new Material(diffuse, 0.1f * x, 0.1f * y, 0.03f, 0.0f), null);
                EntityFactory
                    .create("Sphere")
                    .addComponent(new TransformComponent(new Vector3f(x * 1.2f - 6.0f, y * 1.2f - 6.0f, -10.0f), new Vector3f(), new Vector3f(0.5f, 0.5f, 0.5f)))
                    .addComponent(new StaticModelRendererComponent(data, new Texture(new Texture.ByteColor(diffuse)),
                        ResourceManager.getInstance().loadShader(
                            "./resources/shaders/vertex/pbr.glsl",
                            "./resources/shaders/fragment/pbr.glsl"
                        )
                    ))
                    .buildAndRegister();
            }
        }

        EntityFactory
            .create("Light")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(), new Vector3f()))
            .addComponent(new LightComponent(LightComponent.LightType.POINT, 40.0f, new Vector3f(1.0f, 1.0f, 1.0f)))
            .buildAndRegister();

        window.run();
    }

}

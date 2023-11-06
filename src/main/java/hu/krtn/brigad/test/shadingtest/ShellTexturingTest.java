package hu.krtn.brigad.test.shadingtest;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.ecs.component.RendererComponent;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.logic.Query;
import hu.krtn.brigad.engine.rendering.Texture;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.window.Window;
import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ShellTexturingTest {

    public static void main(String[] args) {
        Window window = Window.initInstance(2560, 1440, "Shell Texturing Test", 60.0f, true, false, 16);
        window.init();

        EntityFactory
            .create("Camera")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 5.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
            .addComponent(new CameraComponent(70.0f, 0.1f, 1000.0f))
            .buildAndRegister();

        ResourceManager.StaticModelData model = ResourceManager.getInstance().loadStaticModel("./resources/models/shells/Sphere.gltf")[0];

        EntityFactory
            .create("BaseSphere")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f), new Vector3f(3.0f)))
            .addComponent(new RendererComponent(
                model,
                ResourceManager.getInstance().loadTexture("./resources/textures/shell_test.png"),
                ResourceManager.getInstance().loadShader("./resources/shaders/vertex/pbr.glsl", "./resources/shaders/fragment/pbr.glsl")
            ))
            .buildAndRegister();

        for (int i = 1; i <= 256; i++) {
            EntityFactory
                .create("Sphere0" + i)
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f - (i * i * (0.0005f / 64.0f)), -5.0f), new Vector3f(0.0f), new Vector3f(3.0f + i * (0.025f / 8.0f))))
                .addComponent(new RendererComponent(
                    model,
                    ResourceManager.getInstance().loadTexture("./resources/textures/shell_test.png"),
                    new ShellShader("./resources/shaders/vertex/shell.glsl", "./resources/shaders/fragment/shell.glsl", i)
                ))
                .buildAndRegister();
        }

        EntityFactory
            .create("Backdrop")
            .addComponent(new TransformComponent(new Vector3f(0.0f, -5.0f, -3.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(50.0f, 20.0f, 20.0f)))
            .addComponent(new RendererComponent(
                ResourceManager.getInstance().loadStaticModel("./resources/models/backdrop.gltf")[0],
                new Texture(new Texture.ByteColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f))),
                ResourceManager.getInstance().loadShader("./resources/shaders/vertex/pbr.glsl", "./resources/shaders/fragment/pbr.glsl")
            ))
            .buildAndRegister();

        EntityFactory
            .create("Light01")
            .addComponent(new TransformComponent(new Vector3f(3.0f, 10.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
            .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(1.0f, 1.0f, 1.0f)))
            .buildAndRegister();

        EntityFactory
            .create("Light02")
            .addComponent(new TransformComponent(new Vector3f(-1.0f, -3.0f, 5.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
            .addComponent(new LightComponent(LightComponent.LightType.POINT, 50.0f, new Vector3f(1.0f, 1.0f, 0.8f)))
            .buildAndRegister();

        LogicManager.getInstance().registerLogic(
            new Logic(new Query("Sphere", true)) {
                @Override
                protected void update(Entity[] queryTargets, float fixedDeltaTime) {

                }

                float time = 0.0f;
                Vector3f lastPos = new Vector3f(0.0f, 0.0f, 0.0f);
                Vector3f velocity = new Vector3f(0.0f, 0.0f, 0.0f);

                @Override
                protected void render(Entity[] queryTargets, float deltaTime) {
                    int index = -1;

                    TransformComponent baseTc = (TransformComponent) queryTargets[0].getComponent(TransformComponent.class);

                    time += deltaTime;
                    velocity = new Vector3f(lastPos).sub(baseTc.getPosition()).mul(40.0f);
                    lastPos = new Vector3f(baseTc.getPosition());

                    for (Entity queryTarget : queryTargets) {
                        TransformComponent tc = (TransformComponent) queryTarget.getComponent(TransformComponent.class);

                        Vector3f offsetVector = new Vector3f(velocity).mul((index * index * (0.0005f / 64.0f)));

                        tc.setRotation(tc.getRotation().add(new Vector3f(0.0f, 10.0f * deltaTime, 0.0f)));
                        tc.setPosition(new Vector3f(0.0f + Math.sin(time) * 2.0f, Math.cos(time) * 2.0f, -5.0f).add(offsetVector));

                        index++;
                    }
                }
            }
        );

        window.run();
    }

}

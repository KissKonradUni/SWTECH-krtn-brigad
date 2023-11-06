package hu.krtn.brigad.test.gametest;

import com.google.gson.*;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.EntityManager;
import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.ecs.component.RendererComponent;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.rendering.Texture;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.resources.ResourceManager.StaticModelData;
import hu.krtn.brigad.engine.serialization.ExtraDataManager;
import hu.krtn.brigad.engine.serialization.SaveManager;
import hu.krtn.brigad.engine.serialization.data.IntData;
import hu.krtn.brigad.engine.window.Window;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestGame {

    public static void main(String[] args) {
        Window window = Window.initInstance(1920, 1080, "Test Game", 60.0f, true, false, 4);
        window.init();

        StaticModelData[] monke = ResourceManager.getInstance().loadStaticModel("./resources/models/monke.gltf");
        StaticModelData[] cage = ResourceManager.getInstance().loadStaticModel("./resources/models/cage.gltf");

        EntityFactory
            .create("Camera")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 5.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
            .addComponent(new CameraComponent(70.0f, 0.1f, 1000.0f))
            .buildAndRegister();

        EntityFactory
                .create("LocalPlayer")
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
                .addComponent(
                        new RendererComponent(
                                monke[0],
                                ResourceManager.getInstance().loadTexture(
                                        "./resources/textures/test.jpg"
                                ),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ))
                .buildAndRegister();

        EntityFactory
                .create("Cage")
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f), new Vector3f(5.0f)))
                .addComponent(
                        new RendererComponent(
                                cage[0],
                                ResourceManager.getInstance().loadTexture(
                                        "./resources/textures/test.png"
                                ),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ))
                .buildAndRegister();

        StaticModelData[] backdrop = ResourceManager.getInstance().loadStaticModel("./resources/models/backdrop.gltf");

        EntityFactory
                .create("Backdrop")
                .addComponent(new TransformComponent(new Vector3f(0.0f, -5.0f, -3.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(50.0f, 20.0f, 20.0f)))
                .addComponent(
                        new RendererComponent(
                                backdrop[0],
                                new Texture(new Texture.ByteColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f))),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ))
                .buildAndRegister();

        StaticModelData[] light = ResourceManager.getInstance().loadStaticModel("./resources/models/light.gltf");

        EntityFactory
                .create("Light01")
                .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 12.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("LocalPlayer")[0]))
                .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(0.0f, 1.0f, 0.0f)))
                .addComponent(
                        new RendererComponent(
                                light[0],
                                new Texture(new Texture.ByteColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f))),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ))
                .buildAndRegister();

        EntityFactory
                .create("Light02")
                .addComponent(new TransformComponent(new Vector3f(-12.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("LocalPlayer")[0]))
                .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(1.0f, 0.5f, 0.0f)))
                .addComponent(
                        new RendererComponent(
                                light[0],
                                new Texture(new Texture.ByteColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f))),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ))
                .buildAndRegister();

        EntityFactory
                .create("Light03")
                .addComponent(new TransformComponent(new Vector3f(12.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("LocalPlayer")[0]))
                .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(0.0f, 0.5f, 1.0f)))
                .addComponent(
                        new RendererComponent(
                                light[0],
                                new Texture(new Texture.ByteColor(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f))),
                                ResourceManager.getInstance().loadShader(
                                        "./resources/shaders/vertex/pbr.glsl",
                                        "./resources/shaders/fragment/pbr.glsl"
                                )
                        ))
                .buildAndRegister();


        LogicManager.getInstance().registerLogic(new TestLogic());
        LogicManager.getInstance().registerLogic(new CageLogic());

        ExtraDataManager.getInstance().registerData("High-score", new IntData(54212));

        window.run();

        File file = new File("save.json");
        String data = SaveManager.getInstance().serializeFile();
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
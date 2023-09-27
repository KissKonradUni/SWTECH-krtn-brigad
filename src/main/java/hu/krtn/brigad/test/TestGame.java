package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.EntityManager;
import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.ecs.component.RendererComponent;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.resources.ResourceManager.StaticModelData;
import hu.krtn.brigad.engine.serialization.ExtraDataManager;
import hu.krtn.brigad.engine.serialization.SaveManager;
import hu.krtn.brigad.engine.serialization.data.IntData;
import hu.krtn.brigad.engine.window.Window;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestGame {

    public static void main(String[] args) {
        Window window = Window.initInstance(1920, 1080, "Test Game", 60.0f, true, false, 4);
        window.init();

        StaticModelData[] monke = ResourceManager.getInstance().loadStaticModel("./resources/models/monke.gltf");
        StaticModelData[] cage  = ResourceManager.getInstance().loadStaticModel("./resources/models/cage.gltf");

        EntityFactory
            .create("Camera")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 5.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
            .addComponent(new CameraComponent())
            .buildAndRegister();

        EntityFactory
            .create("LocalPlayer")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f), new Vector3f(1.0f)))
            .addComponent(
                new RendererComponent(
                    monke[0].mesh,
                    monke[0].material,
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
                    cage[0].mesh,
                    cage[0].material,
                    ResourceManager.getInstance().loadShader(
                    "./resources/shaders/vertex/pbr.glsl",
                    "./resources/shaders/fragment/pbr.glsl"
                    )
                ))
            .buildAndRegister();

        EntityFactory
            .create("Light01")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, 10.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("LocalPlayer")[0]))
            .addComponent(new LightComponent(LightComponent.LightType.POINT, 10.0f, new Vector3f(1.0f, 1.0f, 1.0f)))
            .buildAndRegister();

        EntityFactory
            .create("Light02")
            .addComponent(new TransformComponent(new Vector3f(-10.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("LocalPlayer")[0]))
            .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(1.0f, 0.5f, 0.0f)))
            .buildAndRegister();

        EntityFactory
            .create("Light03")
            .addComponent(new TransformComponent(new Vector3f(10.0f, 0.0f, 0.0f), new Vector3f(0.0f), new Vector3f(1.0f), EntityManager.getInstance().getEntitiesByName("LocalPlayer")[0]))
            .addComponent(new LightComponent(LightComponent.LightType.POINT, 100.0f, new Vector3f(0.0f, 0.5f, 1.0f)))
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
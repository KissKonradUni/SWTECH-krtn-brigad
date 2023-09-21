package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.component.CameraComponent;
import hu.krtn.brigad.engine.ecs.component.RendererComponent;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.rendering.Mesh;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.logic.LogicManager;
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

        Mesh[] monke = ResourceManager.getInstance().loadStaticModel("./resources/models/monke.gltf");
        Mesh[] cage  = ResourceManager.getInstance().loadStaticModel("./resources/models/cage.gltf");

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
                    monke[0],
                    ResourceManager.getInstance().loadShader(
                    "./resources/shaders/vertex/basic.glsl",
                    "./resources/shaders/fragment/diffuse.glsl"
                    )
                ))
            .buildAndRegister();

        EntityFactory
            .create("Cage")
            .addComponent(new TransformComponent(new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f), new Vector3f(5.0f)))
            .addComponent(
                new RendererComponent(
                    cage[0],
                    ResourceManager.getInstance().loadShader(
                    "./resources/shaders/vertex/basic.glsl",
                    "./resources/shaders/fragment/diffuse.glsl"
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
package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.component.RendererComponent;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.io.ResourceManager;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.serialization.ExtraDataManager;
import hu.krtn.brigad.engine.serialization.SaveManager;
import hu.krtn.brigad.engine.serialization.data.IntData;
import hu.krtn.brigad.engine.window.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestGame {

    public static void main(String[] args) {
        Window window = new Window(1920, 1080, "Test Game", 60.0f, true, false);
        window.init();

        EntityFactory
            .create("LocalPlayer")
            .addComponent(new TransformComponent())
            .addComponent(
                new RendererComponent(
                    new QuadMesh(),
                    ResourceManager.getInstance().loadShader(
                    "./resources/shaders/vertex/basic.glsl",
                    "./resources/shaders/fragment/unlit.glsl"
                    )
                ))
            .buildAndRegister();

        for (int i = 0; i < 20; i++) {
            EntityFactory
                .create("RemotePlayer" + String.format("%02d", i))
                .addComponent(new TransformComponent())
                .buildAndRegister();
        }

        EntityFactory
            .create("Bullet", false)
            .addComponent(new TransformComponent())
            .buildAndRegister();

        LogicManager.getInstance().registerLogic(new TestLogic());
        LogicManager.getInstance().registerLogic(new RendererLogic());

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
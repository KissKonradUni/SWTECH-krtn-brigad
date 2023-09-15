package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.serialization.SaveManager;
import hu.krtn.brigad.engine.window.Window;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestGame {

    public static void main(String[] args) {
        new EntityFactory("LocalPlayer").addComponent(new TransformComponent()).BuildAndRegister();

        LogicManager.getInstance().registerLogic(new TestLogic());
        LogicManager.getInstance().registerLogic(new BusyLogic());

        new EntityFactory("Bullet", false).addComponent(new TransformComponent()).BuildAndRegister();

        Window window = new Window(1280, 720, "Test Game", 32.0f, true, false);
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
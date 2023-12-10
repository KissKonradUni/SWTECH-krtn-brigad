package hu.krtn.brigad.test.gametest;

import com.google.gson.*;
import hu.krtn.brigad.editor.EditorLogic;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityFactory;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.resources.ResourceManager;
import hu.krtn.brigad.engine.window.ImGuiLayer;
import hu.krtn.brigad.engine.window.Window;

import java.util.Objects;

public class TestGameFileReader {

    public static void main(String[] args) {
        Window window = Window.initInstance(1920, 1080, "Test Game", 24.0f, true, false, 4);
        window.init();

        String saveFile = ResourceManager.getInstance().readTextFile("./save.json");
        JsonArray saveData = JsonParser.parseString(saveFile).getAsJsonArray();

        for (int i = 0; i < saveData.size(); i++) {
            JsonObject entity = saveData.get(i).getAsJsonObject();
            String type = entity.get("type").getAsString();

            if (!Objects.equals(type, Entity.class.getCanonicalName()))
                continue;

            String entityName = entity.get("name").getAsString();

            EntityFactory
                    .create(entityName)
                    .deserialize(new Gson().toJson(entity))
                    .buildAndRegister();
        }

        LogicManager.getInstance().registerLogic(new TestLogic());
        LogicManager.getInstance().registerLogic(new CageLogic());

        EntityFactory
                .create("EditorDataHolder")
                .buildAndRegister();

        LogicManager.getInstance().registerLogic(new EditorLogic());

        window.run();

        window.destroy();
    }

}

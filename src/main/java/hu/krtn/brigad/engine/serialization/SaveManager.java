package hu.krtn.brigad.engine.serialization;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * The SaveManager class is responsible for managing the serializable objects.
 */
public class SaveManager {

    private final ArrayList<Serializable> saveData;

    private static SaveManager INSTANCE;

    private SaveManager() {
        saveData = new ArrayList<>();
    }

    public static SaveManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SaveManager();
        }
        return INSTANCE;
    }

    /**
     * Registers a serializable object to the save manager.
     * @param object The object to be registered.
     */
    public void registerSaveObject(Serializable object) {
        saveData.add(object);
    }

    /**
     * Unregisters a serializable object from the save manager.
     * @param object The object to be unregistered.
     */
    public void unregisterSaveObject(Serializable object) {
        saveData.remove(object);
    }

    /**
     * Serializes all registered objects.
     * @return The serialized objects.
     */
    public String serializeFile() {
        JsonArray json = new JsonArray();
        saveData.forEach(object -> json.add(JsonParser.parseString(object.serialize())));
        return new GsonBuilder().setPrettyPrinting().create().toJson(json);
    }

    /**
     * Deserializes the given json.
     * @param json The json to be deserialized.
     */
    public void deserializeFile(String json) {
        // TODO: implement, harder than it seems, because i can't just use gson to deserialize the objects i have to find the correct object and call its deserialize method
    }

    /**
     * Resets the save manager.
     */
    public static void reset() {
        INSTANCE = new SaveManager();
    }

}

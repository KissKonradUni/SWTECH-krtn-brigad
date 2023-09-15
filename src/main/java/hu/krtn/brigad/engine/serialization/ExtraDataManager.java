package hu.krtn.brigad.engine.serialization;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.serialization.data.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * The ExtraDataManager is a singleton class that stores all the extra data that is not part of the
 * {@link Component} class.
 * <p>This data is stored in a HashMap with a String
 * key and a {@link Data} value. The key is used to identify the data and the
 * value is the data itself. The data can be retrieved by the key with the corresponding {@link #getData(String)} method.<p/>
 */
public class ExtraDataManager extends Serializable {

    private final HashMap<String, Data<?>> dataCollection;

    private static ExtraDataManager INSTANCE;

    private ExtraDataManager() {
        dataCollection = new HashMap<>();
    }

    public static ExtraDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ExtraDataManager();
            SaveManager.getInstance().registerSaveObject(INSTANCE);
        }
        return INSTANCE;
    }

    /**
     * Resets the data collection.
     */
    public void reset() {
        dataCollection.clear();
    }

    /**
     * Registers a new data to the data collection.
     * @param key The key of the data.
     * @param data The data itself.
     */
    public void registerData(String key, Data<?> data) {
        dataCollection.put(key, data);
    }

    /**
     * Unregisters a data from the data collection.
     * @param key The key of the data.
     */
    public void unregisterData(String key) {
        dataCollection.remove(key);
    }

    /**
     * Returns the data with the given key if it is a {@link StringData}.
     * @param key The key of the data.
     * @return The data with the given key.
     */
    public StringData getStringData(String key) {
        StringData result = null;
        try {
            result = (StringData) dataCollection.get(key);
        } catch (ClassCastException e) {
            System.err.println("Data is not a StringData: " + key);
        }
        return result;
    }

    /**
     * Returns the data with the given key if it is a {@link FloatData}.
     * @param key The key of the data.
     * @return The data with the given key.
     */
    public FloatData getFloatData(String key) {
        FloatData result = null;
        try {
            result = (FloatData) dataCollection.get(key);
        } catch (ClassCastException e) {
            System.err.println("Data is not a FloatData: " + key);
        }
        return result;
    }

    /**
     * Returns the data with the given key if it is a {@link IntData}.
     * @param key The key of the data.
     * @return The data with the given key.
     */
    public IntData getIntData(String key) {
        IntData result = null;
        try {
            result = (IntData) dataCollection.get(key);
        } catch (ClassCastException e) {
            System.err.println("Data is not a IntData: " + key);
        }
        return result;
    }

    /**
     * Returns the raw {@link Data} object with the given key.
     * It should be used if the type of the data conversion is not implemented here,
     * and the user wants to implement it.
     * <p>Example:<br>
     * <pre>{@code
     *    Data<?> data = ExtraDataManager.getInstance().getData("key");
     *    if (data instanceof MyData) {
     *      MyData myData = (MyData) data;
     *      // Do something with myData
     *    }
     * }<pre/>
     * @param key The key of the data.
     * @return The data with the given key.
     */
    public Data<?> getData(String key) {
        return dataCollection.get(key);
    }

    /**
     * Returns the whole data collection serialized.
     * @return The serialized JSON string.
     */
    @Override
    public String serialize() {
        JsonObject json = new JsonObject();
        dataCollection.forEach((key, value) -> json.addProperty(value.getType() + ":" + key, value.serialize()));
        json.addProperty("type", getType());
        return json.toString();
    }

    /**
     * Deserializes the given JSON string and fills the data collection with the data.
     * <p>The JSON string should be in the following format:<br>
     * <pre>{@code
     *   {
     *      "type": "ExtraDataManager",
     *      "data_type1:key1": "serialized_data1",
     *      "data_type2:key2": "serialized_data2",
     *      ...
     *   }
     *}</pre>
     * Also, there are possible exceptions:
     * <li>If the data type is not found, it will print an error message to the console and not register the data.</li>
     * <li>If the data type does not have an empty constructor, it will print an error message to the console and not register the data.</li>
     * @see Data
     * @param data The serialized JSON string.
     */
    @Override
    public void deserialize(String data) {
        // Put the string into a JSON object
        JsonObject json = JsonParser.parseString(data).getAsJsonObject();
        // Iterate through the JSON object
        json.entrySet().forEach(entry -> {
            // Get the data type, key and value
            String dataType = entry.getKey().split(":")[0];
            String key = entry.getKey().split(":")[1];
            String value = entry.getValue().getAsString();

            try {
                // Get the class type and constructor
                Class<?> classType = Class.forName(dataType).asSubclass(Data.class);
                Constructor<?> constructor = classType.getConstructor();
                // Create a new instance of the data type and deserialize it
                Data<?> data_ = (Data<?>) constructor.newInstance();
                data_.deserialize(value);
                dataCollection.put(key, data_);
            } catch (ClassNotFoundException e) {
                System.err.println("Data class not found: " + dataType);
            } catch (NoSuchMethodException e) {
                System.err.println("Data class constructor was not found: " + dataType);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                System.err.println("Data class does not have an empty constructor: " + dataType);
            }
        });
    }

}

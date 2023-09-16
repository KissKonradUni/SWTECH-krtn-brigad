package hu.krtn.brigad.engine.ecs;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.krtn.brigad.engine.serialization.SaveManager;
import hu.krtn.brigad.engine.serialization.Serializable;
import hu.krtn.brigad.engine.window.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * The Entity class represents an entity in the game world.
 * An entity is a collection of components.
 */
public class Entity extends Serializable {

    private String name;
    private ArrayList<Component> components;

    /**
     * Creates a new entity with the given name.
     * @param name The name of the entity.
     * @param persistent Whether the entity should be saved or not.
     */
    protected Entity(String name, boolean persistent) {
        this.name       = name;
        this.components = new ArrayList<>();

        EntityManager.getInstance().registerEntity(this);
        if (persistent)
            SaveManager.getInstance().registerSaveObject(this);
    }

    /**
     * Creates a new entity with the given name. This entity will be saved.
     * @param name The name of the entity.
     */
    public Entity(String name) {
        this(name, true);
    }

    /**
     * Serializes the entity to a JSON string.
     * @return The JSON string.
     */
    @Override
    public String serialize() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("type", getClass().getCanonicalName());

        JsonArray components = new JsonArray();
        this.components.forEach(component -> components.add(JsonParser.parseString(component.serialize())));

        object.add("components", components);
        return new Gson().toJson(object);
    }

    /**
     * Deserializes the entity from a JSON string.<br><br>
     * It relies on the fact that the components should have a specific structure:
     * <li>The component should have a type field, which is the canonical name of the class. (Done automatically by the Component class)
     * <li>The component should have a deserialize method, which takes a JSON string as a parameter.
     * <li>The component should have an empty constructor.
     * @see SaveManager
     * @param data The JSON string.
     */
    @Override
    public void deserialize(String data) {
        // Parse the JSON string to a JSON object.
        JsonObject object = JsonParser.parseString(data).getAsJsonObject();

        // If the name is set in the JSON, use that.
        if (this.name == null)
            this.name = object.get("name").getAsString();

        // Deserialize the components.
        this.components = new ArrayList<>();
        JsonArray components = object.get("components").getAsJsonArray();

        for (int i = 0; i < components.size(); i++) {
            // Get the component as a JSON object and get its type.
            JsonObject jsonComponent = components.get(i).getAsJsonObject();
            String componentType = jsonComponent.get("type").getAsString();

            // Try to create an instance of the component.
            try {
                // Get the class type of the component.
                Class<? extends Component> classType = Class.forName(componentType).asSubclass(Component.class);
                // Get the constructor of the component and create an instance of it.
                Constructor<?> constructor = classType.getConstructor();
                Component component = (Component) constructor.newInstance();
                // Deserialize the component.
                component.deserialize(jsonComponent.toString());
                this.components.add(component);
            } catch (ClassNotFoundException e) {
                Logger.error("Component class not found: " + componentType);
            } catch (NoSuchMethodException e) {
                Logger.error("Component class constructor was not found: " + componentType);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                Logger.error("Component class does not have an empty constructor: " + componentType);
            }
        }

    }

    /**
     * Adds a component to the entity.
     * @param component The component to add.
     */
    public void addComponent(Component component) {
        components.add(component);
        EntityManager.getInstance().registerEntityByComponent(this, component.getType());
    }

    public String getName() {
        return name;
    }

    /**
     * Removes the entity from the game world.<br>
     * <b>THIS IS THE ONLY WAY TO REMOVE AN ENTITY FROM THE GAME WORLD!</b>
     * @param entity The entity to remove.
     */
    public static void removeEntity(Entity entity) {
        EntityManager.getInstance().unregisterEntity(entity);
        SaveManager  .getInstance().unregisterSaveObject(entity);
    }

    /**
     * Gets all the components of the entity.
     * @return The components of the entity.
     */
    public ArrayList<Component> getComponents() {
        return components;
    }

    /**
     * Gets a component from the entity by its class.
     * @param componentClass The class of the component to get.
     * @return The component with the given class, or null if it does not exist.
     */
    public Component getComponent(Class<? extends Component> componentClass) {
        for (Component component : components) {
            if (component.getType().equals(componentClass.getCanonicalName()))
                return component;
        }
        return null;
    }
}

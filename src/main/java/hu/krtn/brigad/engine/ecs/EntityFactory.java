package hu.krtn.brigad.engine.ecs;

import hu.krtn.brigad.engine.window.Logger;

/**
 * The EntityFactory class is used to create entities and register them to the EntityManager.
 * The purpose of this class is to don't leave references to the created entities.
 * This way the entities will be garbage collected when they are removed.
 */
public class EntityFactory {

    private final Entity entity;

    private EntityFactory(String name, boolean persistent) {
        entity = new Entity(name, persistent);
    }

    /**
     * Adds a component to the entity.
     * @param component The component to add.
     * @return The EntityFactory instance.
     */
    public EntityFactory addComponent(Component component) {
        entity.addComponent(component);
        Class<? extends Component>[] dependencies = component.getDependencies();
        try {
            if (dependencies != null)
                component.fulfillDependencies(entity);
        } catch (ComponentDependencyException e) {
            Logger.error(e.getMessage());
        }
        return this;
    }

    /**
     * Deserializes the entity from the given data.
     * @param data The data to deserialize from.
     * @return The EntityFactory instance.
     */
    public EntityFactory deserialize(String data) {
        entity.deserialize(data);
        return this;
    }

    /**
     * Builds and registers the entity.
     */
    public void buildAndRegister() {
        // No need to register the entity, because it is registered in the constructor.
    }

    /**
     * Creates a new Entity with the given name.
     * @param name The name of the entity.
     * @return The EntityFactory instance.
     */
    public static EntityFactory create(String name) {
        return new EntityFactory(name, true);
    }

    /**
     * Creates a new Entity with the given name and persistence.
     * @param name The name of the entity.
     * @param persistent Whether the entity should be persistent or not.
     * @return The EntityFactory instance.
     */
    public static EntityFactory create(String name, boolean persistent) {
        return new EntityFactory(name, persistent);
    }
}

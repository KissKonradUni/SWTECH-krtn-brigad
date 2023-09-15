package hu.krtn.brigad.engine.ecs;

/**
 * The EntityFactory class is used to create entities and register them to the EntityManager.
 * The purpose of this class is to don't leave references to the created entities.
 * This way the entities will be garbage collected when they are removed.
 */
public class EntityFactory {

    private final Entity entity;

    public EntityFactory(String name, boolean persistent) {
        entity = new Entity(name, persistent);
    }

    public EntityFactory(String name) {
        this(name, true);
    }

    /**
     * Adds a component to the entity.
     * @param component The component to add.
     * @return The EntityFactory instance.
     */
    public EntityFactory addComponent(Component component) {
        entity.addComponent(component);
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
    public void BuildAndRegister() {
        EntityManager.getInstance().registerEntity(entity);
    }

}

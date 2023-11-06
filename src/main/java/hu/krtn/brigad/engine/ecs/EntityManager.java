package hu.krtn.brigad.engine.ecs;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The EntityManager is responsible for managing the entities in the game.
 */
public class EntityManager {

    private final ArrayList<Entity> entities;
    /**
     * This HashMap stores the entities by their component type.
     * Its purpose is to make querying entities by component type faster.
     */
    private final HashMap<String, ArrayList<Entity>> entitiesByComponent;

    private static EntityManager INSTANCE;

    /**
     * This flag indicates whether the EntityManager has been modified since the last query.
     */
    private boolean dirty = false;

    private EntityManager() {
        entities = new ArrayList<>();
        entitiesByComponent = new HashMap<>();
    }

    public static EntityManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EntityManager();
        }
        return INSTANCE;
    }

    /**
     * This method is called when an entity is created.
     * @param entity The entity to be registered.
     */
    public void registerEntity(Entity entity) {
        entities.add(entity);
        dirty = true;
    }

    /**
     * This method is called when an entity is destroyed.
     * @param entity The entity to be unregistered.
     */
    protected void unregisterEntity(Entity entity) {
        entity.getComponents().forEach(
            component -> entitiesByComponent.get(component.getType()).remove(entity)
        );
        entities.remove(entity);
        dirty = true;
    }

    /**
     * This method is called when a component is added to an entity.
     * @param entity The entity to be registered.
     * @param componentType The type of the component to be registered.
     */
    protected void registerEntityByComponent(Entity entity, String componentType) {
        if (!entitiesByComponent.containsKey(componentType)) {
            entitiesByComponent.put(componentType, new ArrayList<>());
        }
        entitiesByComponent.get(componentType).add(entity);
        dirty = true;
    }

    /**
     * This method is called when the entity manager is reset.
     */
    public void reset() {
        entities.clear();
        entitiesByComponent.clear();
        dirty = true;
    }

    /**
     * This method returns all entities in the game with the given name.
     * @param name The name of the entities to be returned.
     * @return The entities with the given name.
     */
    public Entity[] getEntitiesByName(String name) {
        ArrayList<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getName().equals(name)) {
                result.add(entity);
            }
        }
        return result.toArray(new Entity[0]);
    }

    /**
     * This method returns all entities in the game with the given keyword in their name.
     * @param keyword The keyword of the entities to be returned.
     * @return The entities with the given keyword in their name.
     */
    public Entity[] getEntitiesByKeyword(String keyword) {
        ArrayList<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getName().contains(keyword)) {
                result.add(entity);
            }
        }
        return result.toArray(new Entity[0]);
    }

    /**
     * This method returns all entities in the game with the given component type.
     * @param componentType The component type of the entities to be returned.
     * @return The entities with the given component type.
     */
    public Entity[] getEntitiesByComponent(String componentType) {
        ArrayList<Entity> list = entitiesByComponent.get(componentType);
        if (list == null) {
            return null;
        } else
            return list.toArray(new Entity[0]);
    }

    /**
     * This method returns the first entity in the game with the given hash id.
     * @param hashId The hash id of the entity to be returned.
     * @return The entity with the given hash id or null if it does not exist.
     */
    public Entity getEntityByHashId(String hashId) {
        for (Entity entity : entities) {
            if (entity.getHashId().equals(hashId)) {
                return entity;
            }
        }
        return null;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}

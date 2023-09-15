package hu.krtn.brigad.engine.logic;

import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityManager;

/**
 * A query is a way to find entities in the entity manager.
 * <li>Query by name: finds entities by their name.</li>
 * <li>Query by keyword: finds entities by their keywords.</li>
 * <li>Query by component: finds entities by their components.</li>
 */
public class Query {

    private final QueryType type;
    private final String name;
    private final String componentType;

    /**
     * The result of the last query.
     * The query cache is invalidated when the entity manager is dirty.
     */
    private Entity[] queryCache = null;

    /**
     * Creates a query by name, or by keyword.
     * @param name The name/keyword to search for.
     * @param keyword If true, the query will find entities by their keywords.
     */
    public Query(String name, boolean keyword) {
        this.type = keyword ? QueryType.KEYWORD : QueryType.NAME;
        this.name = name;
        this.componentType = null;
    }

    /**
     * Creates a query by name.
     * @param name The name to search for.
     */
    public Query(String name) {
        this(name, false);
    }

    /**
     * Creates a query by component.
     * @param component The component type to search for.
     */
    public Query(Class<? extends Component> component) {
        this.type = QueryType.COMPONENT;
        this.name = null;
        this.componentType = component.getCanonicalName();
    }

    /**
     * Executes the query.
     * @return The entities found by the query.
     */
    public Entity[] execute() {
        if (queryCache == null || EntityManager.getInstance().isDirty()) {
            queryCache = switch (type) {
                case NAME -> queryByName();
                case KEYWORD -> queryByKeyword();
                case COMPONENT -> queryByComponent();
            };
        }
        return queryCache;
    }

    private Entity[] queryByKeyword() {
        return EntityManager.getInstance().getEntitiesByKeyword(name);
    }

    private Entity[] queryByName() {
        return EntityManager.getInstance().getEntitiesByName(name);
    }

    private Entity[] queryByComponent() {
        return EntityManager.getInstance().queryEntitiesByComponent(componentType);
    }

}

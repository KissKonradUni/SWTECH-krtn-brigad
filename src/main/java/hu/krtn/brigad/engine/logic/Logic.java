package hu.krtn.brigad.engine.logic;

import hu.krtn.brigad.engine.ecs.Entity;

/**
 * The Logic class is the base class for all logic classes.
 * A lot of game engines use a per-entity logic system, but
 * this engine uses a per-query logic system. This means that
 * the logic classes are not attached to entities, but to
 * queries. This way, the logic classes can be reused for
 * different entities.
 * <br><br>
 * Also, components are only used for data storage, and
 * logic classes are only used for logic.
 */
public abstract class Logic {

    private final Query query;
    private boolean enabled = true;

    /**
     * The query parameter is used to get the entities that
     * the logic class will be applied to.
     * <br><br>
     * It's advised to set the query parameter in the subclass's constructor.
     * @see Query
     * @param query The query that will be used to get the entities.
     */
    public Logic(Query query) {
        this.query = query;
    }

    protected void CallUpdate(float fixedDeltaTime) {
        if (!enabled) return;

        update(query.execute(), fixedDeltaTime);
    }

    protected void CallRender(float deltaTime) {
        if (!enabled) return;

        render(query.execute(), deltaTime);
    }

    protected abstract void update(Entity[] queryTargets, float fixedDeltaTime);

    protected abstract void render(Entity[] queryTargets, float deltaTime);

    /**
     * @param enabled Whether this logic should be enabled or not.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return Whether this logic is enabled or not.
     */
    public boolean isEnabled() {
        return enabled;
    }

}

package hu.krtn.brigad.engine.rendering;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;

import java.util.Arrays;
import java.util.List;

public class LightsLogic extends Logic {

    private static List<Entity> lightsCache = null;

    public LightsLogic() {
        //noinspection unchecked
        super(new Query(new Class[]{LightComponent.class}));
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {
        // wrap the lights into a list
        lightsCache = Arrays.stream(queryTargets).toList();
    }

    public static List<Entity> getLightsCache() {
        return lightsCache;
    }
}

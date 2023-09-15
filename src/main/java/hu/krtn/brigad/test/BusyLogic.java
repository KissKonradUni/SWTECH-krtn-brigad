package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;

public class BusyLogic extends Logic {

    public BusyLogic() {
        super(new Query("LocalPlayer"));
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {

    }

}

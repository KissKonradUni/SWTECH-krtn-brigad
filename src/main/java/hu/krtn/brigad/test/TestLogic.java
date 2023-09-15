package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;

public class TestLogic extends Logic {

    public TestLogic() {
        super(new Query("LocalPlayer"));
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {
        for (Entity queryTarget : queryTargets) {
            TransformComponent tc = (TransformComponent) queryTarget.getComponent(TransformComponent.class);
            tc.setPosition(
                tc.getPosition().add(0, 0, -0.1f * fixedDeltaTime)
            );
        }
    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {

    }

}

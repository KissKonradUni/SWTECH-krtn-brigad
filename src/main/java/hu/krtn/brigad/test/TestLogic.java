package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;

public class TestLogic extends Logic {

    public TestLogic() {
        super(new Query("LocalPlayer"));
    }

    private float time = 0.0f;

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {
        for (Entity queryTarget : queryTargets) {
            TransformComponent tc = (TransformComponent) queryTarget.getComponent(TransformComponent.class);
            tc.setRotation(tc.getRotation().add(
                    0.0f,
                    deltaTime * 100.0f,
                    0.0f
            ));
            tc.setPosition(
                    tc.getPosition().set(
                            0.0f,
                            (float) Math.sin(time),
                            0.0f
                    )
            );
        }
        time += deltaTime;
    }

}

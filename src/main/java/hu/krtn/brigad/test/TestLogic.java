package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;

public class TestLogic extends Logic {

    public TestLogic() {
        super(new Query("Camera"));
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {
        TransformComponent component = (TransformComponent) queryTargets[0].getComponent(TransformComponent.class);
        //component.setRotation(component.getRotation().add(0.0f, 10.0f * deltaTime, 0.0f));
        //component.setPosition(component.getPosition().add(0.0f, 0.0f, 1.0f * deltaTime));
    }

}

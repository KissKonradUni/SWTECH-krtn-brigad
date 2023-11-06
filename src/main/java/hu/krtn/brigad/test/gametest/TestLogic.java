package hu.krtn.brigad.test.gametest;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.TransformComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;
import org.joml.Vector3f;

public class TestLogic extends Logic {

    public TestLogic() {
        super(new Query("LocalPlayer"));
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    private float time = 0.0f;

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {
        TransformComponent component = (TransformComponent) queryTargets[0].getComponent(TransformComponent.class);
        component.setRotation(component.getRotation().add(0.0f, 30.0f * deltaTime, 0.0f));
        component.setPosition(new Vector3f(0.0f, (float) Math.sin(time), -5.0f));
        time += deltaTime;
    }

}

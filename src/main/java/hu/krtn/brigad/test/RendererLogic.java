package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.RendererComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;

import static org.lwjgl.opengl.GL11.*;

public class RendererLogic extends Logic {

    public RendererLogic() {
        super(new Query(RendererComponent.class));
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {
        for (Entity entity : queryTargets) {
            RendererComponent rendererComponent = (RendererComponent) entity.getComponent(RendererComponent.class);
            rendererComponent.getMesh().bind();
            rendererComponent.getShader().bind();

            glDrawElements(GL_TRIANGLES, rendererComponent.getMesh().getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }

}

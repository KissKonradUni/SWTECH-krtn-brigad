package hu.krtn.brigad.engine.rendering;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.component.StaticModelRendererComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.logic.Query;

import static org.lwjgl.opengl.GL11.*;

/**
 * Built-in logic for rendering entities with a renderer component.
 * It is automatically registered when a renderer component is created.
 * @see StaticModelRendererComponent
 */
public class RendererLogic extends Logic {

    public RendererLogic() {
        //noinspection unchecked
        super(new Query(new Class[]{StaticModelRendererComponent.class}));

        if (!LogicManager.getInstance().isLogicPresent(LightsLogic.class))
            LogicManager.getInstance().registerLogic(new LightsLogic());
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {
        for (Entity entity : queryTargets) {
            StaticModelRendererComponent staticModelRendererComponent = (StaticModelRendererComponent) entity.getComponent(StaticModelRendererComponent.class);
            if (staticModelRendererComponent == null) continue;

            if (staticModelRendererComponent.isAlphaBlending()) {
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_ALPHA);
            } else {
                glDisable(GL_BLEND);
            }

            staticModelRendererComponent.bind();
            staticModelRendererComponent.setLights(LightsLogic.getLightsCache());

            glDrawElements(GL_TRIANGLES, staticModelRendererComponent.getMesh().getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }

}

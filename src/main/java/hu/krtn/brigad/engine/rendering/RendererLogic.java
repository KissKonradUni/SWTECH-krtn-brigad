package hu.krtn.brigad.engine.rendering;

import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.ecs.EntityManager;
import hu.krtn.brigad.engine.ecs.component.LightComponent;
import hu.krtn.brigad.engine.ecs.component.RendererComponent;
import hu.krtn.brigad.engine.logic.Logic;
import hu.krtn.brigad.engine.logic.Query;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * Built-in logic for rendering entities with a renderer component.
 * It is automatically registered when a renderer component is created.
 * @see RendererComponent
 */
public class RendererLogic extends Logic {

    private ArrayList<Entity> lightsCache = new ArrayList<>();

    public RendererLogic() {
        //noinspection unchecked
        super(new Query(new Class[]{LightComponent.class, RendererComponent.class}));
    }

    @Override
    protected void update(Entity[] queryTargets, float fixedDeltaTime) {

    }

    @Override
    protected void render(Entity[] queryTargets, float deltaTime) {
        boolean dirty = EntityManager.getInstance().isDirty();
        if (dirty) {
            lightsCache.clear();
            for (Entity entity : queryTargets) {
                LightComponent lightComponent = (LightComponent) entity.getComponent(LightComponent.class);
                if (lightComponent == null) continue;

                lightsCache.add(entity);
            }
        }
        for (Entity entity : queryTargets) {
            RendererComponent rendererComponent = (RendererComponent) entity.getComponent(RendererComponent.class);
            if (rendererComponent == null) continue;

            rendererComponent.bind();
            if (dirty)
                rendererComponent.setLights(lightsCache);

            glDrawElements(GL_TRIANGLES, rendererComponent.getMesh().getIndexCount(), GL_UNSIGNED_INT, 0);
        }
    }

}

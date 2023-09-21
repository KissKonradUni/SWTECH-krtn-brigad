package hu.krtn.brigad.engine.ecs.component;

import hu.krtn.brigad.engine.ecs.Component;
import hu.krtn.brigad.engine.ecs.ComponentDependencyException;
import hu.krtn.brigad.engine.ecs.Entity;
import hu.krtn.brigad.engine.logic.LogicManager;
import hu.krtn.brigad.engine.rendering.Mesh;
import hu.krtn.brigad.engine.rendering.Shader;
import hu.krtn.brigad.engine.rendering.RendererLogic;

/**
 * The renderer component is used to render meshes.
 * It contains a mesh and a shader.
 */
public class RendererComponent extends Component {

    //TODO: Implement file system capabilities
    private final Mesh mesh;
    private final Shader shader;

    /**
     * The constructor of the renderer component.
     * @param mesh The mesh to be rendered.
     * @param shader The shader to be used to render the mesh.
     */
    public RendererComponent(Mesh mesh, Shader shader) {
        super();
        this.mesh = mesh;
        this.shader = shader;

        if (!LogicManager.getInstance().isLogicPresent(RendererLogic.class))
            LogicManager.getInstance().registerLogic(new RendererLogic());
    }

    @Override
    public String serialize() {
        return "{}";
    }

    @Override
    public void deserialize(String data) {

    }

    public Mesh getMesh() {
        return mesh;
    }

    public Shader getShader() {
        return shader;
    }

    /**
     * The renderer component depends on the transform component.
     * It requires the model matrix to be calculated, which is done by the transform component.
     * @see TransformComponent
     * @return The dependencies of the component.
     */
    @Override
    public Class<? extends Component>[] getDependencies() {
        //noinspection unchecked
        return new Class[] { TransformComponent.class };
    }

    /**
     * This method is used to fulfill the dependencies of the component.
     * @param entity The entity that the component is attached to.
     * @throws ComponentDependencyException If a dependency is missing.
     */
    @Override
    public void fulfillDependencies(Entity entity) throws ComponentDependencyException {
        TransformComponent tc = (TransformComponent) entity.getComponent(TransformComponent.class);
        if (tc == null) {
            throw new ComponentDependencyException(TransformComponent.class);
        }
    }
}

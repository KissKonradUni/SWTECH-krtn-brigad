package hu.krtn.brigad.engine.ecs;

import hu.krtn.brigad.editor.ExposedFields;
import hu.krtn.brigad.engine.serialization.Serializable;

/**
 * The base class for all components.
 * Theoretically a component can be anything, but it is recommended to use
 * components for data only. (No logic)
 */
public abstract class Component extends Serializable {

    /**
     * This method is called when the component is added to an entity.
     * @return An array of Component classes that this component depends on.
     */
    public Class<? extends Component>[] getDependencies() { return null; }

    /**
     * This method is used to fulfill the dependencies of the component.
     * @param entity The entity that the component is attached to.
     * @throws ComponentDependencyException If a dependency is missing.
     */
    public void fulfillDependencies(Entity entity) throws ComponentDependencyException {}

    /**
     * This method returns the simple name of the component's class.
     * @return The simple name of the component's class.
     */
    public String getSimpleName() {
        return getClass().getSimpleName();
    }

    /**
     * This variable stores the fields that are exposed to the editor.
     */
    protected final ExposedFields exposedFields = new ExposedFields();

    /**
     * This method is used to determine which fields should be exposed to the editor.
     */
    public abstract void initExposedFields();

    /**
     * This method is used to get the fields that are exposed to the editor.
     * @return The fields that are exposed to the editor.
     */
    public ExposedFields getExposedFields() {
        return exposedFields;
    }

}

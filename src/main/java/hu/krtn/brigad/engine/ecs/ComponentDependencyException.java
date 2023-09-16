package hu.krtn.brigad.engine.ecs;

/**
 * Thrown when a component's dependencies are not fulfilled.
 */
public class ComponentDependencyException extends Exception {

    private final String message;

    public ComponentDependencyException(Class<? extends Component> transformComponentClass) {
        super();
        this.message = "Component dependency not fulfilled: " + transformComponentClass.getCanonicalName();
    }

    @Override
    public String getMessage() {
        return message;
    }

}

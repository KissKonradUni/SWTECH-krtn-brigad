package hu.krtn.brigad.engine.serialization;

/**
 * The base class for all serializable objects.
 * Anything that is serializable must extend this class.
 */
public abstract class Serializable {

    /**
     * Serializes the object to a JSON string.
     * @return The serialized object.
     */
    public abstract String serialize();

    /**
     * Deserializes the object from a JSON string.
     * @param data The serialized object.
     */
    public abstract void deserialize(String data);

    /**
     * The type of the object.
     * It's in the form of a canonical class name.
     * Example: <code>{@code hu.krtn.brigad.engine.serialization.Serializable}</code>
     */
    private final String type;

    /**
     * Creates a new serializable object.
     * It sets the type to the canonical class name of the object.
     * This works even if the object is extended.
     */
    public Serializable() {
        this.type = (getClass().getCanonicalName());
    }

    /**
     * Gets the type of the object.
     * @return The type of the object.
     */
    public String getType() {
        return type;
    }

}

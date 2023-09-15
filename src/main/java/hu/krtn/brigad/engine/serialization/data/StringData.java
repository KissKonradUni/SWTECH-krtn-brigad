package hu.krtn.brigad.engine.serialization.data;

/**
 * String data class for serialization of extra data that is not paired
 * to any component or entity.
 */
public class StringData extends Data<String> {

    public StringData(String data) {
        super(data);
    }

    public StringData() {
        super();
    }

    @Override
    public String serialize() {
        return data;
    }

    /**
     * Deserialize the data from a string.<br><br>
     * Possible exceptions:
     * <li>If the string is null, the data will be set to an empty string.
     * @param data The string to deserialize from.
     */
    @Override
    public void deserialize(String data) {
        try {
            this.setData(data);
        } catch (NullPointerException e) {
            System.err.println("StringData deserialize: data is null");
            this.setData("");
        }
    }

}
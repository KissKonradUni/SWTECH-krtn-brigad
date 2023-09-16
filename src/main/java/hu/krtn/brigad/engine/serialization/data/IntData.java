package hu.krtn.brigad.engine.serialization.data;

import hu.krtn.brigad.engine.window.Logger;

/**
 * Int data class for serialization of extra data that is not paired
 * to any component or entity.
 */
public class IntData extends Data<Integer> {
    public IntData(int data) {
        super(data);
    }

    public IntData() {
        super();
    }

    @Override
    public String serialize() {
        return data.toString();
    }

    /**
     * Deserialize the data from a string.<br><br>
     * Possible exceptions:
     * <li>If the string is not an integer, the data will be set to 0.
     * <li>If the string is null, the data will be set to 0.
     * @param data The string to deserialize from.
     */
    @Override
    public void deserialize(String data) {
        try {
            this.setData(Integer.parseInt(data));
        } catch (NumberFormatException e) {
            Logger.error("IntData deserialize: data is not a number");
            this.setData(0);
        } catch (NullPointerException e) {
            Logger.error("IntData deserialize: data is null");
            this.setData(0);
        }
    }
}

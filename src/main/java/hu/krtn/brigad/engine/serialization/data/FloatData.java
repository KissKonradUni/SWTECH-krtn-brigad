package hu.krtn.brigad.engine.serialization.data;

import hu.krtn.brigad.engine.window.Logger;

/**
 * Float data class for serialization of extra data that is not paired
 * to any component or entity.
 */
public class FloatData extends Data<Float> {
    public FloatData(Float data) {
        super(data);
    }

    public FloatData() {
        super();
    }

    @Override
    public String serialize() {
        return data.toString();
    }

    /**
     * Deserialize the data from a string.<br><br>
     * Possible exceptions:
     * <li>If the string is not a float, the data will be set to 0.
     * <li>If the string is null, the data will be set to 0.
     * @param data The string to deserialize from.
     */
    @Override
    public void deserialize(String data) {
        try {
            this.setData(Float.parseFloat(data));
        } catch (NumberFormatException e) {
            Logger.error("FloatData deserialize: data is not a number");
            this.setData(0f);
        } catch (NullPointerException e) {
            Logger.error("FloatData deserialize: data is null");
            this.setData(0f);
        }
    }
}

package hu.krtn.brigad.engine.serialization.data;

import hu.krtn.brigad.engine.serialization.Serializable;

/**
 * Data class for serialization of extra data that is not paired
 * to any component or entity.<br>
 * If there's need to serialize a data type that is not implemented yet,
 * it can be done by extending this class.<br><br>
 * <b>This class in itself is not serializable, but its subclasses are.</b>
 * @param <T> type of the data
 */
public abstract class Data<T> extends Serializable {

    protected T data;

    public Data(T data) {
        this.data = data;
    }

    public Data() {
        this.data = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        if (data == null) {
            throw new NullPointerException("Data.setData: data is null");
        }
        this.data = data;
    }

}

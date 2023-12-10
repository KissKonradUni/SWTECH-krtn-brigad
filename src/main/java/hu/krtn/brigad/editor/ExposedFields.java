package hu.krtn.brigad.editor;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Contains the fields that are exposed to the editor.
 */
public class ExposedFields {

    public static class Field<T> {
        public Class<?> type;
        public Consumer<T> setter;
        public Supplier<T> getter;

        public Field(Consumer<T> setter, Supplier<T> getter) {
            this.type   = getter.get().getClass();
            this.setter = setter;
            this.getter = getter;
        }
    }

    public static class StringField extends Field<String> {
        public StringField(Consumer<String> setter, Supplier<String> getter) {
            super(setter, getter);
        }
    }

    public static class FloatField extends Field<Float> {
        public FloatField(Consumer<Float> setter, Supplier<Float> getter) {
            super(setter, getter);
        }
    }

    public static class IntegerField extends Field<Integer> {
        public IntegerField(Consumer<Integer> setter, Supplier<Integer> getter) {
            super(setter, getter);
        }
    }

    public static class BooleanField extends Field<Boolean> {
        public BooleanField(Consumer<Boolean> setter, Supplier<Boolean> getter) {
            super(setter, getter);
        }
    }

    public static class Vector3fField extends Field<Vector3f> {
        public Vector3fField(Consumer<Vector3f> setter, Supplier<Vector3f> getter) {
            super(setter, getter);
        }
    }

    public static class Vector4fField extends Field<Vector4f> {
        public Vector4fField(Consumer<Vector4f> setter, Supplier<Vector4f> getter) {
            super(setter, getter);
        }
    }

    public static class EnumField extends Field<Integer> {
        public String[] values;
        public EnumField(Consumer<Integer> setter, Supplier<Integer> getter, String[] values) {
            super(setter, getter);
            this.type = EnumField.class;
            this.values = values;
        }
    }

    public static class ColorField extends Field<Vector3f> {
        public ColorField(Consumer<Vector3f> setter, Supplier<Vector3f> getter) {
            super(setter, getter);
            this.type = ColorField.class;
        }
    }

    private final Map<String, Field<?>> fields = new HashMap<>();

    public void addField(String name, Field<?> field) {
        fields.put(name, field);
    }

    public void removeField(String name) {
        fields.remove(name);
    }

    public <T> void setField(String name, T value) {
        Field<T> field = (Field<T>) fields.get(name);
        field.setter.accept(value);
    }

    public <T> T getField(String name) {
        Field<T> field = (Field<T>) fields.get(name);
        return field.getter.get();
    }

    public Map<String, Field<?>> getFields() {
        return fields;
    }

}
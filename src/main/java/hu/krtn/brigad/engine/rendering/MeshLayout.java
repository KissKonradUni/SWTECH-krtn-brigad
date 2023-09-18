package hu.krtn.brigad.engine.rendering;

public class MeshLayout {

    public enum AttributeTypes {
        VEC4   (Float.BYTES, 4),
        VEC3   (Float.BYTES, 3),
        VEC2   (Float.BYTES, 2),
        FLOAT  (Float.BYTES, 1),
        SCALAR (Integer.BYTES, 1),
        MAT4   (Float.BYTES, 16);

        private final int size;
        private final int count;

        AttributeTypes(int size, int count) {
            this.size = size;
            this.count = count;
        }

        public int getSize() {
            return size * count;
        }

        public int getCount() {
            return count;
        }
    }

    private final AttributeTypes[] layout;

    public MeshLayout(AttributeTypes[] layout) {
        this.layout = layout;
    }

    public int getStride() {
        int stride = 0;
        for (AttributeTypes type : layout) {
            stride += type.getSize();
        }
        return stride;
    }

    public int getAttributeSize() {
        return layout.length;
    }

    public int getAttributeSize(int index) {
        return layout[index].getSize();
    }

    public int getAttributeCount(int index) {
        return layout[index].getCount();
    }

    public int getCountSum() {
        int sum = 0;
        for (AttributeTypes type : layout) {
            sum += type.getCount();
        }
        return sum;
    }

}

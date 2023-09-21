package hu.krtn.brigad.engine.rendering;

/**
 * The vertices of a mesh are stored in a buffer. This class describes the layout of the buffer.
 * The layout means the order and the size of the attributes.
 * This is necessary to tell the GPU how to interpret the buffer, because our buffer is interleaved.
 */
public class MeshLayout {

    /**
     * The types of the attributes.
     */
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

    /**
     * Returns the stride of the buffer.
     * The stride is the size of the vertex in bytes.
     * @return the stride of the buffer
     */
    public int getStride() {
        int stride = 0;
        for (AttributeTypes type : layout) {
            stride += type.getSize();
        }
        return stride;
    }

    /**
     * Returns the amount of attributes.
     * @return the amount of attributes
     */
    public int getAttributeAmount() {
        return layout.length;
    }

    /**
     * Returns the size of the attribute at the given index.
     * @param index the index of the attribute
     * @return the size of the attribute at the given index
     */
    public int getAttributeSize(int index) {
        return layout[index].getSize();
    }

    /**
     * Returns the count of the attribute at the given index.
     * @param index the index of the attribute
     * @return the count of the attribute at the given index
     */
    public int getAttributeAmount(int index) {
        return layout[index].getCount();
    }

    /**
     * Returns the count of all attributes.
     * @return the count of all attributes
     */
    public int getCountSum() {
        int sum = 0;
        for (AttributeTypes type : layout) {
            sum += type.getCount();
        }
        return sum;
    }

}

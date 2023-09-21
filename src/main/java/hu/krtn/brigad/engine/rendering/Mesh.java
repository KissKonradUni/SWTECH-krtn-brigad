package hu.krtn.brigad.engine.rendering;

import static org.lwjgl.opengl.GL30.*;

/**
 * Holds the mesh data.
 * <p>
 *     The mesh data is stored in a state machine within OpenGL.
 * </p>
 * <p>
 *     Every state would be updated separately, but with the Vertex Array Object (VAO) we can store all the states
 *     in one object. Binding the VAO will bind all the states.
 *     <br>
 *     The VAO is a container for the Vertex Buffer Object (VBO) and the Element Buffer Object (EBO).
 *     <br>
 *     The VBO stores the vertex data, the EBO stores the indices of the vertices.
 *     <br>
 *     The VAO stores the following states:
 *     <ul>
 *         <li>the vertex attribute pointers</li>
 *         <li>the vertex buffer object</li>
 *         <li>the element buffer object</li>
 *         <li>the vertex layout</li>
 *         <li>the draw type</li>
 *         <li>etc...</>
 *     </ul>
 * </p>
 */
public class Mesh {

    /**
     * The draw type of the mesh.
     */
    public enum DrawTypes {
        STATIC_DRAW(GL_STATIC_DRAW),
        DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
        STREAM_DRAW(GL_STREAM_DRAW);

        private final int glType;

        DrawTypes(int glType) {
            this.glType = glType;
        }

        public int getGlType() {
            return glType;
        }
    }

    private final int vertexArrayObjectHandle;

    private final int vertexBufferObjectHandle;
    private final int elementBufferObjectHandle;

    private final int vertexCount;
    private final int indexCount;

    /**
     * Creates a mesh.
     * @param vertices the vertices of the mesh
     * @param indices the indices of the vertices
     * @param layout the layout of the vertices
     * @param drawType the draw type of the mesh
     */
    public Mesh(float[] vertices, int[] indices, MeshLayout layout, DrawTypes drawType) {
        vertexCount = vertices.length / layout.getCountSum();
        indexCount  = indices.length;

        vertexArrayObjectHandle = glGenVertexArrays();
        glBindVertexArray(vertexArrayObjectHandle);

        vertexBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObjectHandle);
        glBufferData(GL_ARRAY_BUFFER, vertices, drawType.getGlType());

        elementBufferObjectHandle = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferObjectHandle);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, drawType.getGlType());

        int pointer = 0;
        for (int i = 0; i < layout.getAttributeAmount(); i++) {
            glVertexAttribPointer(i, layout.getAttributeAmount(i), GL_FLOAT, false, layout.getStride(), pointer);
            pointer += layout.getAttributeSize(i);
            glEnableVertexAttribArray(i);
        }

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Binds the mesh.
     */
    public void bind() {
        glBindVertexArray(vertexArrayObjectHandle);
    }

    /**
     * Unbinds the mesh.
     */
    public void unbind() {
        glBindVertexArray(0);
    }

    /**
     * Destroys the mesh.
     */
    public void destroy() {
        glDeleteBuffers(vertexBufferObjectHandle);
        glDeleteBuffers(elementBufferObjectHandle);
        glDeleteVertexArrays(vertexArrayObjectHandle);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

}

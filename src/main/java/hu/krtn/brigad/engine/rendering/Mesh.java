package hu.krtn.brigad.engine.rendering;

import hu.krtn.brigad.engine.window.Logger;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {

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
        for (int i = 0; i < layout.getAttributeSize(); i++) {
            glVertexAttribPointer(i, layout.getAttributeCount(i), GL_FLOAT, false, 0, pointer);
            pointer += layout.getAttributeSize(i);
            glEnableVertexAttribArray(i);
        }

        glBindVertexArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void bind() {
        glBindVertexArray(vertexArrayObjectHandle);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

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

package hu.krtn.brigad.test;

import hu.krtn.brigad.engine.rendering.Mesh;

public class QuadMesh extends Mesh {

    private static final float[] vertices = {
        -0.5f,  0.5f, 0.0f,
         0.5f,  0.5f, 0.0f,
         0.5f, -0.5f, 0.0f,
        -0.5f, -0.5f, 0.0f
    };
    private static final int[] indices = {
        0, 1, 2,
        0, 2, 3
    };

    public QuadMesh() {
        super(vertices, indices, DrawTypes.STATIC_DRAW);
    }

}

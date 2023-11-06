package hu.krtn.brigad.engine.extra;

import hu.krtn.brigad.engine.rendering.Mesh;
import hu.krtn.brigad.engine.rendering.MeshLayout;

public class QuadMesh extends Mesh {

    private static final float[] data = {
        // pos, tex, normal
        -1.0f, 1.0f, 0.0f,   0.0f, 1.0f,   0.0f, 0.0f, 1.0f, // top left
        -1.0f, -1.0f, 0.0f,  0.0f, 0.0f,   0.0f, 0.0f, 1.0f, // bottom left
        1.0f, -1.0f, 0.0f,   1.0f, 0.0f,   0.0f, 0.0f, 1.0f, // bottom right
        1.0f, 1.0f, 0.0f,    1.0f, 1.0f,   0.0f, 0.0f, 1.0f  // top right
    };

    private static final int[] indices = {
        0, 1, 2,
        2, 3, 0
    };

    public QuadMesh() {
        super(data, indices, new MeshLayout(
            new MeshLayout.AttributeTypes[]{
                MeshLayout.AttributeTypes.VEC3,
                MeshLayout.AttributeTypes.VEC2,
                MeshLayout.AttributeTypes.VEC3
            }
        ), DrawTypes.STATIC_DRAW);
    }

}

package hu.krtn.brigad.engine.rendering;

import org.joml.Vector4f;

public class Material {

    private Vector4f ambient;
    private Vector4f diffuse;
    private Vector4f specular;
    private float shininess;

    public Material(Vector4f ambient, Vector4f diffuse, Vector4f specular, float shininess) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
}

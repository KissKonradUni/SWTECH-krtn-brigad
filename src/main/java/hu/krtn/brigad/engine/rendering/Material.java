package hu.krtn.brigad.engine.rendering;

import org.joml.Vector4f;

/**
 * Holds the material properties of a mesh.
 */
// TODO: Implement this class.
public class Material {

    private Vector4f baseColor;
    private float metallic;
    private float roughness;

    public Material(Vector4f baseColor, float metallic, float roughness) {
        this.baseColor = baseColor;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public Vector4f getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(Vector4f baseColor) {
        this.baseColor = baseColor;
    }

    public float getMetallic() {
        return metallic;
    }

    public void setMetallic(float metallic) {
        this.metallic = metallic;
    }

    public float getRoughness() {
        return roughness;
    }

    public void setRoughness(float roughness) {
        this.roughness = roughness;
    }
}

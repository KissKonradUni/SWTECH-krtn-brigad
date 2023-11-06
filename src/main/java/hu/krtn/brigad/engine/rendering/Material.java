package hu.krtn.brigad.engine.rendering;

import org.joml.Vector4f;

/**
 * Holds the material properties of a mesh.
 */
// TODO: Implement this class.
public class Material {

    private Vector4f diffuse;
    private float metallic;
    private float roughness;
    private float ambientOcclusion;
    private float emission;

    private Texture diffuseTexture;
    private Texture metallicTexture;
    private Texture roughnessTexture;
    private Texture ambientOcclusionTexture;
    private Texture emissionTexture;

    // TODO: Make a factory for optional textures.
    public Material(Vector4f diffuse, float metallic, float roughness, float ambientOcclusion, float emission) {
        this.diffuse = diffuse;
        this.metallic = metallic;
        this.roughness = roughness;
        this.ambientOcclusion = ambientOcclusion;
        this.emission = emission;
    }

    public Vector4f getDiffuse() {
        return diffuse;
    }

    public void setDiffuse(Vector4f diffuse) {
        this.diffuse = diffuse;
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

    public float getAmbientOcclusion() {
        // TODO: Implement this.
        return ambientOcclusion;
    }

    public void setAmbientOcclusion(float ambientOcclusion) {
        this.ambientOcclusion = ambientOcclusion;
    }

    public float getEmission() {
        // TODO: Implement this.
        return emission;
    }

    public void setEmission(float emission) {
        this.emission = emission;
    }
}

package hu.krtn.brigad.engine.rendering.shading;

import java.util.Map;

public class FragmentShaderDataV1 extends FragmentShaderData {

    private final Map<String, Integer> textures;
    private final Map<String, String> uniforms;
    private final boolean lightingEnabled;
    private final int maxLights;

    public FragmentShaderDataV1(String path, String source, Map<String, Integer> textures, Map<String, String> uniforms, boolean lightingEnabled, int maxLights) {
        super(path, source);
        this.version = "1.0";
        this.textures = textures;
        this.uniforms = uniforms;
        this.lightingEnabled = lightingEnabled;
        this.maxLights = maxLights;
    }

    public Map<String, Integer> getTextures() {
        return textures;
    }

    public Map<String, String> getUniforms() {
        return uniforms;
    }

    public boolean isLightingEnabled() {
        return lightingEnabled;
    }

    public int getMaxLights() {
        return maxLights;
    }

}

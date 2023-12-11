package hu.krtn.brigad.engine.rendering.shading;

import java.util.Map;

public class VertexShaderDataV1 extends VertexShaderData {

    private final Map<String, String> uniforms;

    public VertexShaderDataV1(String path, String source, Map<String, String> uniforms) {
        super(path, source);
        this.version = "1.0";
        this.uniforms = uniforms;
    }

    public Map<String, String> getUniforms() {
        return uniforms;
    }

}

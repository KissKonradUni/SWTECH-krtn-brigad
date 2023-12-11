package hu.krtn.brigad.engine.rendering.shading;

public class VertexShaderData {

    protected String version = "raw";
    private final String path;
    private final String source;

    public VertexShaderData(String path, String source) {
        this.path = path;
        this.source = source;
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public String getSource() {
        return source;
    }

}

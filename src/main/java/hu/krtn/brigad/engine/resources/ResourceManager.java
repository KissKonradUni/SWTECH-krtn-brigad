package hu.krtn.brigad.engine.resources;

import de.javagl.jgltf.impl.v2.Accessor;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import hu.krtn.brigad.engine.rendering.Mesh;
import hu.krtn.brigad.engine.rendering.MeshLayout;
import hu.krtn.brigad.engine.rendering.Shader;
import hu.krtn.brigad.engine.window.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceManager {

    private static final ResourceCache<Shader> shaderCache = new ResourceCache<>();

    private static ResourceManager INSTANCE;

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourceManager();
        }
        return INSTANCE;
    }

    public String readTextFile(String path) {
        StringBuilder result = new StringBuilder();

        File file = new File(path);
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            Logger.error("File not found: " + path);
            return null;
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            result.append(line).append("\n");
        }
        scanner.close();

        return result.toString();
    }

    public Shader loadShader(String vertexShaderPath, String fragmentShaderPath) {
        if (shaderCache.has(vertexShaderPath + "|" + fragmentShaderPath)) {
            return shaderCache.get(vertexShaderPath + "|" + fragmentShaderPath);
        }

        String vertexShaderSource = readTextFile(vertexShaderPath);
        String fragmentShaderSource = readTextFile(fragmentShaderPath);
        Shader shader = new Shader(vertexShaderSource, fragmentShaderSource);
        shaderCache.put(vertexShaderPath + "|" + fragmentShaderPath, shader);

        return shader;
    }

    public Mesh[] loadGlTF(String path) {
        GltfAssetReader gltfAssetReader = new GltfAssetReader();
        GltfAsset gltfAsset;
        try {
            gltfAsset = gltfAssetReader.read(Path.of(path).toUri());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (gltfAsset == null) {
            Logger.error("Failed to load glTF asset: " + path);
            return null;
        }

        ArrayList<Mesh> result = new ArrayList<>();

        GlTF gltf = (GlTF) gltfAsset.getGltf();
        gltf.getMeshes().forEach(mesh -> {
            result.add(loadMesh(gltf, mesh));
        });

        return result.toArray(new Mesh[0]);
    }

    private Mesh loadMesh(GlTF gltf, de.javagl.jgltf.impl.v2.Mesh mesh) {
        Primitive primitive = loadPrimitive(gltf, mesh.getPrimitives().get(0));
        return new Mesh(primitive.getVertices(), primitive.getIndices(), primitive.getLayout(), Mesh.DrawTypes.STATIC_DRAW);
    }

    private static class Primitive {
        private final int[] indices;
        private final float[] vertices;
        private final MeshLayout layout;

        public Primitive(int[] indices, float[] vertices, MeshLayout layout) {
            this.indices = indices;
            this.vertices = vertices;
            this.layout = layout;
        }

        public int[] getIndices() {
            return indices;
        }

        public float[] getVertices() {
            return vertices;
        }

        public MeshLayout getLayout() {
            return layout;
        }
    }

    private Primitive loadPrimitive(GlTF gltf, MeshPrimitive primitive) {
        Map<String, Integer> attributes = primitive.getAttributes();
        int indices = primitive.getIndices();

        ArrayList<String> accessorTypes = gltf.getAccessors().stream().map(
            accessor -> accessor.getBufferView() + "|" + accessor.getType()
        ).collect(Collectors.toCollection(ArrayList::new));
        ArrayList<MeshLayout.AttributeTypes> attributeTypes = new ArrayList<>();
        accessorTypes.forEach( accessorType -> {
            String[] split = accessorType.split("\\|");
            String type = split[1];
            switch (type) {
                case "SCALAR" -> attributeTypes.add(MeshLayout.AttributeTypes.SCALAR);
                case "FLOAT" -> attributeTypes. add(MeshLayout.AttributeTypes.FLOAT);
                case "VEC2" -> attributeTypes.  add(MeshLayout.AttributeTypes.VEC2);
                case "VEC3" -> attributeTypes.  add(MeshLayout.AttributeTypes.VEC3);
                case "VEC4" -> attributeTypes.  add(MeshLayout.AttributeTypes.VEC4);
                case "MAT4" -> attributeTypes.  add(MeshLayout.AttributeTypes.MAT4);
            }
        });
        attributeTypes.remove(3); // remove the indices

        String base64Data = gltf.getBuffers().get(0).getUri().split(",")[1];
        byte[] data = Base64.getDecoder().decode(base64Data);
        // convert to int array
        int[] intData = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            intData[i] = data[i] & 0xFF;
        }

        int indicesOffset = gltf.getBufferViews().get(indices).getByteOffset();
        int indicesLength = gltf.getBufferViews().get(indices).getByteLength();

        float[] verticesData = new float[(intData.length - indicesLength) / 4];
        for (int i = 0; i < verticesData.length; i++) {
            verticesData[i] = Float.intBitsToFloat(
                (intData[i * 4]) |
                (intData[i * 4 + 1] << 8) |
                (intData[i * 4 + 2] << 16) |
                (intData[i * 4 + 3] << 24)
            );
        }

        int[] indicesData = new int[indicesLength / 2];
        for (int i = 0; i < indicesLength / 2; i++) {
            indicesData[i] = (intData[indicesOffset + i * 2]) | (intData[indicesOffset + i * 2 + 1] << 8);
        }

        return new Primitive(indicesData, verticesData, new MeshLayout(attributeTypes.toArray(new MeshLayout.AttributeTypes[0])));
    }

}

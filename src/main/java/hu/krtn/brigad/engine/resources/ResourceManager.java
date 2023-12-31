package hu.krtn.brigad.engine.resources;

import hu.krtn.brigad.engine.rendering.*;
import hu.krtn.brigad.engine.rendering.shading.Shader;
import hu.krtn.brigad.engine.window.Logger;
import org.joml.Vector4f;
import org.lwjgl.assimp.*;
import org.lwjgl.stb.STBImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A singleton class for managing resources.
 */
public class ResourceManager {

    private static final ResourceCache<Shader> shaderCache = new ResourceCache<>();
    private static final ResourceCache<Texture> textureCache = new ResourceCache<>();
    private static final ResourceCache<StaticModelData[]> meshCache = new ResourceCache<>();

    private static ResourceManager INSTANCE;

    private ResourceManager() {
    }

    public static ResourceManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourceManager();
        }
        return INSTANCE;
    }

    /**
     * Reads a text file and returns its contents as a string.
     * @param path The path of the file.
     * @return The contents of the file.
     */
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

    /**
     * Reads a binary file and returns its contents as a byte buffer.
     * @param path The path of the file.
     * @return The contents of the file.
     */
    public ByteBuffer readBinaryFile(String path) {
        File file = new File(path);
        try {
            return ByteBuffer.wrap(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            Logger.error("File not found: " + path);
            return null;
        }
    }

    /**
     * Loads a shader from the given paths.
     * @param vertexShaderPath The path of the vertex shader.
     * @param fragmentShaderPath The path of the fragment shader.
     * @return The shader.
     */
    public Shader loadShader(String vertexShaderPath, String fragmentShaderPath) {
        if (shaderCache.has(vertexShaderPath + "|" + fragmentShaderPath)) {
            return shaderCache.get(vertexShaderPath + "|" + fragmentShaderPath);
        }

        Shader shader = new Shader(vertexShaderPath, fragmentShaderPath);
        shaderCache.put(vertexShaderPath + "|" + fragmentShaderPath, shader);

        return shader;
    }

    /**
     * Loads a texture from the given path.
     * @param texturePath The path of the texture.
     * @return The texture.
     */
    public Texture loadTexture(String texturePath) {
        String absolutePath = new File(texturePath).getAbsolutePath();

        if (textureCache.has(texturePath)) {
            return textureCache.get(texturePath);
        }

        int[] width = new int[1];
        int[] height = new int[1];
        int[] channels = new int[1];
        ByteBuffer data = STBImage.stbi_load(absolutePath, width, height, channels, 4);

        Texture texture = new Texture(data, width[0], height[0], texturePath);

        textureCache.put(texturePath, texture);
        assert data != null;
        STBImage.stbi_image_free(data);

        return texture;
    }

    public static class StaticModelData {
        public String path;
        public Mesh mesh;
        public Material material;

        public StaticModelData(Mesh mesh, Material material, String path) {
            this.path = path;
            this.mesh = mesh;
            this.material = material;
        }
    }

    /**
     * Loads a static model from the given path.
     * @param path The path of the model.
     * @return The meshes of the model.
     */
    public StaticModelData[] loadStaticModel(String path) {
        String absolutePath = new File(path).getAbsolutePath();

        if (meshCache.has(path)) {
            return meshCache.get(path);
        }

        StaticModelData[] result;
        try (AIScene scene = Assimp.aiImportFile(absolutePath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_FixInfacingNormals)) {
            if (scene == null) {
                Logger.error("Error loading model: " + path);
                return null;
            }

            int materialCount = scene.mNumMaterials();
            Material[] materials = new Material[materialCount];
            for (int i = 0; i < materialCount; i++) {
                try (AIMaterial material = AIMaterial.create(scene.mMaterials().get(i))) {
                    materials[i] = processMaterial(material, absolutePath);
                } catch (NullPointerException e) {
                    Logger.error("Error loading material: " + path);
                    return null;
                }
            }

            int meshCount = scene.mNumMeshes();
            Mesh[] meshes = new Mesh[meshCount];
            int[]  materialIndices = new int[meshCount];
            for (int i = 0; i < meshCount; i++) {
                try (AIMesh mesh = AIMesh.create(scene.mMeshes().get(i))) {
                    meshes[i] = processMesh(mesh);
                    materialIndices[i] = mesh.mMaterialIndex();
                } catch (NullPointerException e) {
                    Logger.error("Error loading mesh: " + path);
                    return null;
                }
            }

            result = new StaticModelData[meshCount];
            for (int i = 0; i < meshes.length; i++) {
                result[i] = new StaticModelData(meshes[i], materials[materialIndices[i]], path);
            }
            return result;
        } catch (Exception e) {
            Logger.error("Error loading model: " + path);
            return null;
        }
    }

    /**
     * Processes a material that was loaded from a model.
     *
     * @param material     The loaded material.
     * @param absolutePath The absolute path of the model.
     * @return The processed material.
     */
    private Material processMaterial(AIMaterial material, String absolutePath) {
        // TODO: complete rework
        AIColor4D color = AIColor4D.create();

        // AIString path = AIString.calloc();
        // Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_BASE_COLOR, 0, path, (IntBuffer) null, null, null, null, null, null);
        // String texturePath = path.dataString();
        // if (!texturePath.isEmpty()) {
        //     Path parentPath = new File(absolutePath).getParentFile().toPath();
        //     texturePath = parentPath.resolve(texturePath).toString();
        //     Texture texture = loadTexture(texturePath);
        // }

        Vector4f baseColor = new Vector4f(1.0f);
        if (Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_BASE_COLOR, Assimp.aiTextureType_NONE, 0, color) == 0) {
            baseColor = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        float metallic = 0.5f;
        if (Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_METALLIC_FACTOR, Assimp.aiTextureType_NONE, 0, color) == 0) {
            metallic = color.r();
        }

        float roughness = 0.5f;
        if (Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_ROUGHNESS_FACTOR, Assimp.aiTextureType_NONE, 0, color) == 0) {
            roughness = color.r();
        }

        float emission = 0.0f;
        if (Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_EMISSIVE, Assimp.aiTextureType_NONE, 0, color) == 0) {
            emission = color.r();
        }

        return new Material(baseColor, metallic, roughness, 0.03f, emission);
    }

    /**
     * Processes a mesh that was loaded from a model.
     * @param mesh The loaded mesh.
     * @return The processed mesh.
     */
    private Mesh processMesh(AIMesh mesh) {
        ArrayList<Float> vertices  = new ArrayList<>();
        ArrayList<Float> uvs       = new ArrayList<>();
        ArrayList<Float> normals   = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        AIVector3D.Buffer aiVertices = mesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }

        AIVector3D.Buffer aiUVs = mesh.mTextureCoords(0);
        while (aiUVs != null && aiUVs.remaining() > 0) {
            AIVector3D aiUV = aiUVs.get();
            uvs.add(aiUV.y());
            uvs.add(aiUV.x());
        }

        AIVector3D.Buffer aiNormals = mesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }

        AIFace.Buffer aiFaces = mesh.mFaces();
        while (aiFaces.remaining() > 0) {
            AIFace aiFace = aiFaces.get();
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }

        FloatBuffer combinedBuffer = FloatBuffer.allocate(vertices.size() / 3 * 8);
        for (int i = 0; i < vertices.size() / 3; i++) {
            combinedBuffer.put(vertices.get(i * 3));
            combinedBuffer.put(vertices.get(i * 3 + 1));
            combinedBuffer.put(vertices.get(i * 3 + 2));

            if (uvs.isEmpty()) {
                combinedBuffer.put(0.0f);
                combinedBuffer.put(0.0f);
            } else {
                combinedBuffer.put(uvs.get(i * 2));
                combinedBuffer.put(uvs.get(i * 2 + 1));
            }

            combinedBuffer.put(normals.get(i * 3));
            combinedBuffer.put(normals.get(i * 3 + 1));
            combinedBuffer.put(normals.get(i * 3 + 2));
        }

        IntBuffer indicesBuffer = IntBuffer.allocate(indices.size());
        for (Integer index : indices) {
            indicesBuffer.put(index);
        }

        return new Mesh(combinedBuffer.array(), indicesBuffer.array(), new MeshLayout(
            new MeshLayout.AttributeTypes[] {
                    MeshLayout.AttributeTypes.VEC3,
                    MeshLayout.AttributeTypes.VEC2,
                    MeshLayout.AttributeTypes.VEC3
            }
        ), Mesh.DrawTypes.STATIC_DRAW);
    }

}

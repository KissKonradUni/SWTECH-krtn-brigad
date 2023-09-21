package hu.krtn.brigad.engine.resources;

import hu.krtn.brigad.engine.rendering.*;
import hu.krtn.brigad.engine.window.Logger;
import org.joml.Vector4f;
import org.lwjgl.assimp.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A singleton class for managing resources.
 */
public class ResourceManager {

    private static final ResourceCache<Shader> shaderCache = new ResourceCache<>();
    private static final ResourceCache<Texture> textureCache = new ResourceCache<>();

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

        String vertexShaderSource = readTextFile(vertexShaderPath);
        String fragmentShaderSource = readTextFile(fragmentShaderPath);
        Shader shader = new Shader(vertexShaderSource, fragmentShaderSource);
        shaderCache.put(vertexShaderPath + "|" + fragmentShaderPath, shader);

        return shader;
    }

    /**
     * Loads a texture from the given path.
     * @param texturePath The path of the texture.
     * @return The texture.
     */
    private Texture loadTexture(String texturePath) {
        // TODO: Implement texture loading

        return null;
    }

    /**
     * Loads a static model from the given path.
     * @param path The path of the model.
     * @return The meshes of the model.
     */
    public Mesh[] loadStaticModel(String path) {
        String absolutePath = new File(path).getAbsolutePath();

        Mesh[] meshes;
        try (AIScene scene = Assimp.aiImportFile(absolutePath, Assimp.aiProcess_Triangulate | Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_FixInfacingNormals)) {
            if (scene == null) {
                Logger.error("Error loading model: " + path);
                return null;
            }

            int materialCount = scene.mNumMaterials();
            for (int i = 0; i < materialCount; i++) {
                try (AIMaterial material = AIMaterial.create(scene.mMaterials().get(i))) {
                    processMaterial(material);
                }
            }

            int meshCount = scene.mNumMeshes();
            meshes = new Mesh[meshCount];
            for (int i = 0; i < meshCount; i++) {
                try (AIMesh mesh = AIMesh.create(scene.mMeshes().get(i))) {
                    meshes[i] = processMesh(mesh);
                }
            }
        } catch (Exception e) {
            Logger.error("Error loading model: " + path);
            return null;
        }

        return meshes;
    }

    /**
     * Processes a material that was loaded from a model.
     * @param material The loaded material.
     * @return The processed material.
     */
    private Material processMaterial(AIMaterial material) {
        AIColor4D color = AIColor4D.create();

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(material, Assimp.aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String texturePath = path.dataString();
        if (!texturePath.isEmpty()) {
            Texture texture = loadTexture(texturePath);
        }

        Vector4f ambient = new Vector4f(1.0f);
        if (Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_AMBIENT, Assimp.aiTextureType_NONE, 0, color) == 0) {
            ambient = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Vector4f diffuse = new Vector4f(1.0f);
        if (Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_DIFFUSE, Assimp.aiTextureType_NONE, 0, color) == 0) {
            diffuse = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        Vector4f specular = new Vector4f(1.0f);
        if (Assimp.aiGetMaterialColor(material, Assimp.AI_MATKEY_COLOR_SPECULAR, Assimp.aiTextureType_NONE, 0, color) == 0) {
            specular = new Vector4f(color.r(), color.g(), color.b(), color.a());
        }

        return new Material(ambient, diffuse, specular, 0.5f);
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
            uvs.add(aiUV.x());
            uvs.add(aiUV.y());
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

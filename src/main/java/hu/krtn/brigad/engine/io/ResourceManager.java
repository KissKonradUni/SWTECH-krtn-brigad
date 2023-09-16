package hu.krtn.brigad.engine.io;

import hu.krtn.brigad.engine.rendering.Shader;
import hu.krtn.brigad.engine.window.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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

}

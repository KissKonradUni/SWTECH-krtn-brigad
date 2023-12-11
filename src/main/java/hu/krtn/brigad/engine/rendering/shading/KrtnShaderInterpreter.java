package hu.krtn.brigad.engine.rendering.shading;

import hu.krtn.brigad.engine.window.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KrtnShaderInterpreter {

    public static VertexShaderData interpretVertexShader(String path, String source) {
        // Check for marker
        if (!source.startsWith("////KRTN")) {
            Logger.warn("No shader marker found! Treating as normal shader.");
            return new VertexShaderData(path, source);
        }

        // Check for marker end
        if (!source.contains("////END")) {
            Logger.warn("Shader marker found, but no end marker found! Treating as normal shader.");
            return new VertexShaderData(path, source);
        }

        // Cut out the marker part for use
        String marker = source.substring(source.indexOf("////KRTN"), source.indexOf("////END") + 8);
        source = source.substring(source.indexOf("////END") + 8);

        // Remove comments from marker. Comments are marked with ";;" and end with a newline.
        marker = marker.replaceAll(";;.*\n", "");

        // ////VERSION <version>
        if (!marker.contains("////VERSION")) {
            Logger.warn("No version marker found! Treating as normal shader.");
            return new VertexShaderData(path, source);
        }
        Pattern versionPattern = Pattern.compile("(////VERSION) (.*)");
        Matcher versionMatcher = versionPattern.matcher(marker);
        if (!versionMatcher.find()) {
            Logger.warn("Invalid version marker found! Treating as normal shader.");
            return new VertexShaderData(path, source);
        }
        String version = versionMatcher.group(2);

        // Supressed switch statement warning because it is not needed.
        //noinspection SwitchStatementWithTooFewBranches
        return switch (version) {
            case "1.0" -> interpretVertexShaderV1(path, source, marker);
            default -> {
                Logger.warn("Unknown version marker found! Treating as normal shader.");
                yield new VertexShaderData(path, source);
            }
        };
    }

    private static VertexShaderData interpretVertexShaderV1(String path, String source, String marker) {
        // ////UNIFORMS (optional, multiline)
        Map<String, String> uniforms = new HashMap<>();

        if (marker.contains("////UNIFORMS START")) {
            if (!marker.contains("////UNIFORMS END")) {
                Logger.warn("Uniform marker found, but no end marker found! Treating as normal shader.");
                return new VertexShaderData(path, source);
            }

            Pattern uniformsPattern = Pattern.compile("(////UNIFORMS START)\\n((.*\\n)*)(////UNIFORMS END)", Pattern.MULTILINE);
            Matcher uniformsMatcher = uniformsPattern.matcher(marker);
            if (!uniformsMatcher.find()) {
                Logger.warn("Invalid uniform marker found! Treating as normal shader.");
                return new VertexShaderData(path, source);
            }
            String uniformsString = uniformsMatcher.group(2);

            if (uniformsString.isEmpty()) {
                Logger.warn("Uniform marker found, but no uniforms found! Treating as normal shader.");
                return new VertexShaderData(path, source);
            }

            String[] uniformLines = uniformsString.split("\n");
            for (String uniformLine : uniformLines) {
                Pattern uniformLinePattern = Pattern.compile("(//)(.*) (.*)");
                Matcher uniformLineMatcher = uniformLinePattern.matcher(uniformLine);
                if (!uniformLineMatcher.find()) {
                    Logger.warn("Invalid uniform line found! Treating as normal shader.");
                    return new VertexShaderData(path, source);
                }
                String name = uniformLineMatcher.group(3).trim();
                String type = uniformLineMatcher.group(2).trim();

                if (name.isEmpty()) {
                    Logger.warn("Empty uniform name found! Treating as normal shader.");
                    return new VertexShaderData(path, source);
                }
                if (uniforms.containsKey(name)) {
                    Logger.warn("Duplicate uniform name found! Treating as normal shader.");
                    return new VertexShaderData(path, source);
                }

                uniforms.put(name, type);
            }
        }

        return new VertexShaderDataV1(path, source, uniforms);
    }

    public static FragmentShaderData interpretFragmentShader(String path, String source) {
        // Check for marker
        if (!source.startsWith("////KRTN")) {
            Logger.warn("No shader marker found! Treating as normal shader.");
            return new FragmentShaderData(path, source);
        }

        // Check for marker end
        if (!source.contains("////END")) {
            Logger.warn("Shader marker found, but no end marker found! Treating as normal shader.");
            return new FragmentShaderData(path, source);
        }

        // Cut out the marker part for use
        String marker = source.substring(source.indexOf("////KRTN"), source.indexOf("////END") + 8);
        source = source.substring(source.indexOf("////END") + 8);

        // Remove comments from marker. Comments are marked with ";;" and end with a newline.
        marker = marker.replaceAll(";;.*\n", "");

        // ////VERSION <version>
        if (!marker.contains("////VERSION")) {
            Logger.warn("No version marker found! Treating as normal shader.");
            return new FragmentShaderData(path, source);
        }
        Pattern versionPattern = Pattern.compile("(////VERSION) (.*)");
        Matcher versionMatcher = versionPattern.matcher(marker);
        if (!versionMatcher.find()) {
            Logger.warn("Invalid version marker found! Treating as normal shader.");
            return new FragmentShaderData(path, source);
        }
        String version = versionMatcher.group(2);

        // Supressed switch statement warning because it is not needed.
        //noinspection SwitchStatementWithTooFewBranches
        return switch (version) {
            case "1.0" -> interpretFragmentShaderV1(path, source, marker);
            default -> {
                Logger.warn("Unknown version marker found! Treating as normal shader.");
                yield new FragmentShaderData(path, source);
            }
        };
    }

    private static FragmentShaderData interpretFragmentShaderV1(String path, String shader, String marker) {
        // Name and dimensions of the samplers
        Map<String, Integer> textureUniforms = new HashMap<>();

        // ////TEXTURES (optional, multiline)
        if (marker.contains("////TEXTURES START")) {
            if (!marker.contains("////TEXTURES END")) {
                Logger.warn("Texture marker found, but no end marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }

            Pattern texturePattern = Pattern.compile("(////TEXTURES START)\\n((.*\\n)*)(////TEXTURES END)", Pattern.MULTILINE);
            Matcher textureMatcher = texturePattern.matcher(marker);
            if (!textureMatcher.find()) {
                Logger.warn("Invalid texture marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }
            String textures = textureMatcher.group(2);

            if (textures.isEmpty()) {
                Logger.warn("Texture marker found, but no textures found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }

            String[] textureLines = textures.split("\n");
            for (String textureLine : textureLines) {
                Pattern textureLinePattern = Pattern.compile("(//)(.*) (.*)");
                Matcher textureLineMatcher = textureLinePattern.matcher(textureLine);
                if (!textureLineMatcher.find()) {
                    Logger.warn("Invalid texture line found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }
                String name = textureLineMatcher.group(2).trim();
                String dimensionsString = textureLineMatcher.group(3).trim();
                int dimensions;
                try {
                    dimensions = Integer.parseInt(dimensionsString);
                } catch (NumberFormatException e) {
                    Logger.warn("Invalid texture dimensions found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }

                if (name.isEmpty()) {
                    Logger.warn("Empty texture name found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }
                if (textureUniforms.containsKey(name)) {
                    Logger.warn("Duplicate texture name found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }
                if (dimensions < 1 || dimensions > 3) {
                    Logger.warn("Invalid texture dimensions found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }

                textureUniforms.put(name, dimensions);
            }
        }

        // ////UNIFORMS (optional, multiline)
        Map<String, String> uniforms = new HashMap<>();

        if (marker.contains("////UNIFORMS START")) {
            if (!marker.contains("////UNIFORMS END")) {
                Logger.warn("Uniform marker found, but no end marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }

            Pattern uniformsPattern = Pattern.compile("(////UNIFORMS START)\\n((.*\\n)*)(////UNIFORMS END)", Pattern.MULTILINE);
            Matcher uniformsMatcher = uniformsPattern.matcher(marker);
            if (!uniformsMatcher.find()) {
                Logger.warn("Invalid uniform marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }
            String uniformsString = uniformsMatcher.group(2);

            if (uniformsString.isEmpty()) {
                Logger.warn("Uniform marker found, but no uniforms found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }

            String[] uniformLines = uniformsString.split("\n");
            for (String uniformLine : uniformLines) {
                Pattern uniformLinePattern = Pattern.compile("(//)(.*) (.*)");
                Matcher uniformLineMatcher = uniformLinePattern.matcher(uniformLine);
                if (!uniformLineMatcher.find()) {
                    Logger.warn("Invalid uniform line found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }
                String name = uniformLineMatcher.group(3).trim();
                String type = uniformLineMatcher.group(2).trim();

                if (name.isEmpty()) {
                    Logger.warn("Empty uniform name found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }
                if (uniforms.containsKey(name)) {
                    Logger.warn("Duplicate uniform name found! Treating as normal shader.");
                    return new FragmentShaderData(path, shader);
                }

                uniforms.put(name, type);
            }
        }

        // ////LIGHTS <enabled/disabled>
        boolean lightsEnabled = false;
        if (marker.contains("////LIGHTS")) {
            Pattern lightsPattern = Pattern.compile("(////LIGHTS) (.*)");
            Matcher lightsMatcher = lightsPattern.matcher(marker);
            if (!lightsMatcher.find()) {
                Logger.warn("Invalid lights marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }
            String lights = lightsMatcher.group(2).trim();
            if (lights.equals("enabled")) {
                lightsEnabled = true;
            } else if (!lights.equals("disabled")) {
                Logger.warn("Invalid lights marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }
        }

        // ////MAX_LIGHTS <number>
        int maxLights = 0;
        if (marker.contains("////MAX_LIGHTS")) {
            Pattern maxLightsPattern = Pattern.compile("(////MAX_LIGHTS) (.*)");
            Matcher maxLightsMatcher = maxLightsPattern.matcher(marker);
            if (!maxLightsMatcher.find()) {
                Logger.warn("Invalid max lights marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }
            String maxLightsString = maxLightsMatcher.group(2).trim();
            try {
                maxLights = Integer.parseInt(maxLightsString);
            } catch (NumberFormatException e) {
                Logger.warn("Invalid max lights marker found! Treating as normal shader.");
                return new FragmentShaderData(path, shader);
            }
        }

        return new FragmentShaderDataV1(path, shader, textureUniforms, uniforms, lightsEnabled, maxLights);
    }

}

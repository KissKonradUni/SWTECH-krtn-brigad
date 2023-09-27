#version 410 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

struct vertex_data {
    vec3 position;
    vec2 texCoord;
    vec3 normal;

    vec3 viewPos;
};
out vertex_data f_PBRInfo;

struct pbr_data {
    vec3 albedo;
    float metallic;
    float roughness;
    float ambientOcclusion;
    float emissive;
};
out pbr_data f_PBRData;

struct light_data {
    vec3 position;
    vec3 color;
    float intensity;
};
out light_data f_lights[8];
flat out int f_numLights;

uniform mat4 model;
uniform mat4 proj;
uniform mat4 view;
uniform vec3 viewPos;

uniform pbr_data PBRData;
uniform light_data lights[8];
uniform int lightCount;

void main() {
    gl_Position = proj * view * model * vec4(position, 1.0);

    f_PBRInfo.position = vec3(model * vec4(position, 1.0));
    f_PBRInfo.texCoord = uv;
    f_PBRInfo.normal   = mat3(transpose(inverse(model))) * normal;

    f_PBRInfo.viewPos = viewPos;

    f_PBRData = PBRData;
    f_PBRData.ambientOcclusion = 0.03;
    f_PBRData.emissive = 0.0;

    f_lights = lights;
    f_numLights = lightCount;
}

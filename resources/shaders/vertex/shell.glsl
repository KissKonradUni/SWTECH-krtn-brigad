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

out vec3 f_localPos;

uniform mat4 model;
uniform mat4 proj;
uniform mat4 view;
uniform vec3 viewPos;

void main() {
    gl_Position = proj * view * model * vec4(position, 1.0);

    f_PBRInfo.position = vec3(model * vec4(position, 1.0));
    f_PBRInfo.texCoord = uv;
    f_PBRInfo.normal   = mat3(transpose(inverse(model))) * normal;

    f_PBRInfo.viewPos = viewPos;

    f_localPos = position;
}

#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

out vec3 fragPosition;
out vec2 fragTexCoord;
out vec3 fragNormal;
out vec3 fragViewPos;

uniform mat4 model;
uniform mat4 proj;
uniform mat4 view;

void main() {
    gl_Position = proj * view * model * vec4(position, 1.0);

    fragPosition = vec3(model * vec4(position, 1.0));
    fragTexCoord = uv;
    fragNormal = mat3(transpose(inverse(model))) * normal;

    fragViewPos = vec3(view * vec4(0.0, 0.0, 0.0, 1.0));
}
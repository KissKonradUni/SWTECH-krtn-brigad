////KRTN

// ;; This version is the KRTN marker's version.
////VERSION 1.0

// ;; (url) TODO: add documentation link for usage
////UNIFORMS START
//mat4 model
//mat4 projection
//mat4 view
//vec3 viewPos
////UNIFORMS END

////END

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

uniform mat4 model;
uniform mat4 projection;
uniform mat4 view;
uniform vec3 viewPos;

void main() {
    gl_Position = projection * view * model * vec4(position, 1.0);

    f_PBRInfo.position = vec3(model * vec4(position, 1.0));
    f_PBRInfo.texCoord = uv;
    f_PBRInfo.normal   = mat3(transpose(inverse(model))) * normal;

    f_PBRInfo.viewPos = viewPos;
}

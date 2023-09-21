#version 410 core

in vec3 fragPosition;
in vec2 fragTexCoord;
in vec3 fragNormal;
in vec3 fragViewPos;

out vec4 color;

vec3 objectColor = vec3(0.0f, 0.8f, 0.3f);
float specularStrength = 0.5f;
float ambientStrength = 0.1f;
float shininess = 64.0f;

vec3 lightPos = vec3(0.0f, 0.0f, 5.0f);
vec3 lightColor = vec3(1.0f, 1.0f, 1.0f);

void main() {
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPos - fragPosition);
    vec3 viewDir = normalize(fragViewPos - fragPosition);
    vec3 reflectDir = reflect(-lightDir, norm);

    float spec = pow(max(dot(norm, reflectDir), 0.0f), shininess);
    vec3 specular = spec * lightColor * specularStrength;

    float diff = max(dot(norm, lightDir), 0.0f);
    vec3 diffuse = diff * lightColor;

    vec3 ambient = ambientStrength * lightColor;

    vec3 result = (ambient + diffuse + specular) * objectColor;
    color = vec4(result, 1.0f);
}
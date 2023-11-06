#version 410 core

struct vertex_data {
    vec3 position;
    vec2 texCoord;
    vec3 normal;

    vec3 viewPos;
};
in vertex_data f_PBRInfo;

in vec3 f_localPos;

struct pbr_data {
    float metallic;
    float roughness;
    float ambientOcclusion;
    float emission;
};
uniform pbr_data f_PBRData;
uniform sampler2D albedo;

struct light_data {
    vec3 position;
    vec3 color;
    float intensity;
};
uniform light_data f_lights[8];
uniform int f_numLights;

uniform int f_layer;

out vec4 color;

const float PI = 3.14159265359;

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
    float a      = roughness*roughness;
    float a2     = a*a;
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float num   = a2;
    float denom = (NdotH2 * (a2 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return num / denom;
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
    float r = (roughness + 1.0);
    float k = (r*r) / 8.0;

    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
    float NdotV = max(dot(N, V), 0.0);
    float NdotL = max(dot(N, L), 0.0);
    float ggx2  = GeometrySchlickGGX(NdotV, roughness);
    float ggx1  = GeometrySchlickGGX(NdotL, roughness);

    return ggx1 * ggx2;
}

const uint k = 1103515245U;

// https://www.shadertoy.com/view/dtfGDl
vec3 hash( uvec3 x )
{
    x *= k;
    x = ((x>>2u)^(x.yzx>>1u)^x.zxy)*k;
    return vec3(x)*(1.0/float(0xffffffffU));
}

void main() {
    vec3 N = normalize(f_PBRInfo.normal);                       // normal
    vec3 V = normalize(f_PBRInfo.viewPos - f_PBRInfo.position); // view direction

    vec4 f_albedo = texture(albedo, f_PBRInfo.texCoord); // albedo

    int max_layer = 256;
    float density = 200.0f;

    // Shell texture smth
    uvec3 base_pos = uvec3((f_PBRInfo.texCoord + vec2(density)) * density, 0.0f);
    float circles = clamp(1.0f - length(fract(f_PBRInfo.texCoord * density) - vec2(0.5f)), 0.0f, 1.0f);
    float val = clamp(hash(base_pos).r * circles + 0.1f, 0.0f, 1.0f);

    f_albedo *= f_layer / (max_layer / 2.0f);

    vec3 Lo = vec3(0.0);
    for (int i = 0; i < f_numLights; i++) {
        vec3 L = normalize(f_lights[i].position - f_PBRInfo.position); // light direction
        vec3 H = normalize(V + L);                                     // half vector

        float distance = length(f_lights[i].position - f_PBRInfo.position);
        float attenuation = 1.0 / (distance * distance);
        vec3 radiance = f_lights[i].color * f_lights[i].intensity * attenuation;

        float NDF = DistributionGGX(N, H, f_PBRData.roughness); // distribution
        float G = GeometrySmith(N, V, L, f_PBRData.roughness);  // geometry

        vec3 F0 = vec3(0.04);                                    // F0 = 0.04 for metals
        F0      = mix(F0, f_albedo.xyz, f_PBRData.metallic); // F0 = albedo lerp
        vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);        // fresnel

        vec3 numerator = NDF * G * F;
        float denominator = 4.0 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0) + 0.0001;
        vec3 specular = numerator / max(denominator, 0.001);    // specular BRDF

        vec3 kS = F;
        vec3 kD = vec3(1.0) - kS;
        kD *= 1.0 - f_PBRData.metallic;

        float NdotL = max(dot(N, L), 0.0);
        Lo += (kD * f_albedo.xyz / PI + specular) * radiance * NdotL; // light
    }

    vec3 ambient = vec3(0.03) * f_albedo.xyz * f_PBRData.ambientOcclusion; // ambient

    vec3 col = ambient + Lo;
    col = col / (col + vec3(1.0));
    col = pow(col, vec3(1.0/2.2));

    col += f_PBRData.emission;

    color = vec4(col, 1.0);

    if (val < (1.0f / max_layer) * f_layer )
        discard;
}

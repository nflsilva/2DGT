#version 410 core

in vec2 textureCoords;
flat in int textureIndex;

uniform sampler2D in_samplers[100];

out vec4 out_color;

void main()
{
    vec4 color = texture(in_samplers[textureIndex], textureCoords);
    if(color.a == 0.0f) discard;
    out_color = color;
};
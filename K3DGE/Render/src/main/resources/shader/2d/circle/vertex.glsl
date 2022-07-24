#version 330 core

layout (location=0) in vec2 in_position;
layout (location=1) in float in_radius;
layout (location=2) in vec2 in_translation;
layout (location=3) in float in_rotation;
layout (location=4) in vec2 in_scale;
layout (location=5) in vec4 in_color;

uniform mat4 in_projectionMatrix;

out vec4 color;

vec2 translate(vec2 v, vec2 t){
    return v + t;
}
vec2 rotate(vec2 v, float a) {
    float s = sin(a);
    float c = cos(a);
    mat2 m = mat2(c, -s, s, c);
    return m * v;
}
vec2 scale(vec2 v, vec2 s) {
    return v * s;
}

void main(){

    vec2 final_position = in_position;
    //final_position = scale(final_position, in_scale);
    //final_position = rotate(final_position, in_rotation);
    final_position = translate(final_position, in_translation);

    color = in_color;

    gl_Position = in_projectionMatrix * vec4(final_position, 0f, 1f);
    gl_PointSize = in_radius * 2;
};
#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;

uniform sampler2D u_texture;
uniform vec4 u_color;
uniform vec2 u_resolution;

void main() {
    vec4 textureColor = texture2D(u_texture, v_texCoord);
    vec2 center = u_resolution * 0.5;
    float distance = length(gl_FragCoord.xy - center);
    float radius = length(center);
    float factor = 1.0 - distance / radius;
    vec4 finalColor = mix(textureColor, u_color, factor);
    gl_FragColor = finalColor;
}
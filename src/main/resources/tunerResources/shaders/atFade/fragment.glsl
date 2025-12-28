#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_percent;
varying vec2 v_texCoord;


void main() {
    vec4 color = texture2D(u_texture, v_texCoord);

    float fadeStart = u_percent;
    float fadeWidth = 0.2;

    float fadeEnd = fadeStart + fadeWidth;

    float alpha = 1.0;

    if (v_texCoord.x > fadeStart) {
        alpha = 1.0 - smoothstep(fadeStart, fadeEnd, v_texCoord.x);
        alpha = pow(alpha, 0.7);
    }

    gl_FragColor = vec4(color.rgb, color.a * alpha);
}
#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;
uniform sampler2D u_texture;
uniform float u_glowRadius; // 控制发光半径
uniform vec4 u_glowColor;   // 发光颜色
uniform float u_glowIntensity; // 发光强度

void main() {
    vec4 originalColor = texture2D(u_texture, v_texCoord);

    // 计算周围像素
    float glow = 0.0;
    for(float x = -u_glowRadius; x <= u_glowRadius; x += 1.0) {
        for(float y = -u_glowRadius; y <= u_glowRadius; y += 1.0) {
            vec2 offset = vec2(x, y) / 512.0; // 根据纹理大小调整
            vec4 neighborColor = texture2D(u_texture, v_texCoord + offset);
            glow += neighborColor.a;
        }
    }

    glow = glow / (4.0 * u_glowRadius * u_glowRadius);
    vec4 glowEffect = u_glowColor * glow * u_glowIntensity;

    gl_FragColor = originalColor + glowEffect * (1.0 - originalColor.a);
}
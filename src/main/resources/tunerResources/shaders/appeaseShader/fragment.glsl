#ifdef GL_ES
precision mediump float;
#endif

uniform float u_time;
uniform vec2 u_resolution;

// 假设传入的原始颜色
uniform vec4 originalColor;

void main() {
    // 规范化的像素坐标（从 0 到 1）
    vec2 uv = gl_FragCoord.xy / u_resolution.xy - 0.5;

    // 偏移量，根据时间变化
    vec2 offset = vec2(sin(u_time * 2.0) * 0.5, cos(u_time * 2.0) * 0.5);
    uv.x -= offset.x;
    uv.y -= offset.y;

    // 随时间变化的像素颜色
    vec3 col = 0.5 + 0.9 * sin(-u_time + (uv.x * uv.x) + (uv.y * uv.y) + vec3(8, 1, 3))
    * cos(u_time + (uv.y * uv.x) + (uv.y * uv.y) + vec3(5, 1, 6))
    + 0.5 * cos(u_time + (uv.y * uv.x) + (uv.y * uv.y) + vec3(1, 2, 3))
    * cos(u_time + (uv.y * uv.x) + (uv.y * uv.y) + vec3(13, 5, 8))
    * cos(u_time + (uv.y * uv.x) + (uv.y * uv.y) + vec3(1, 2, 1))
    + 0.5 * cos(u_time + (uv.y * uv.x) + (uv.y * uv.y) + vec3(1, 2, 3));

    // 混合颜色
    vec3 mixedColor = mix(originalColor.rgb, col, 0.5); // 0.5 表示混合比例

    // 输出到屏幕
    gl_FragColor = vec4(mixedColor, originalColor.a);
}
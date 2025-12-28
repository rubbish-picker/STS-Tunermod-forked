#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoord;

uniform sampler2D u_channel0;
uniform float u_time;
uniform vec4 u_color;

const vec4 redLaser = vec4(1.0, 0.1, 0.1, 1.0);
const vec4 white = vec4(1.0, 1.0, 1.0, 1.0);
const float centerIntensity = 16.0;
const float laserStartPercentage = 0.0;

void main() {
    vec4 laserColor = u_color;

    vec2 uv = v_texCoord.xy;

//    laserColor.rgb = 0.5 + 0.5 * cos(u_time + uv.xyx + vec3(0, 2, 4));

    //    vec4 baseColor = texture2D(u_channel1, uv);
    vec4 baseColor = vec4(0, 0, 0, 0);

    float intensity = 1.0 - abs(uv.y - 0.5);
    intensity = pow(intensity, 6.0);

    if (uv.x < laserStartPercentage) {
        intensity = mix(0.0, intensity, pow(uv.x / laserStartPercentage, 0.5));
    }

    vec2 samplePoint = uv;
    samplePoint.x = samplePoint.x * 0.1 - u_time;
    samplePoint.y = samplePoint.y * 2.0;

    float sampleIntensity = texture2D(u_channel0, samplePoint).r;
    vec4 sampleColor;
    sampleColor.r = sampleIntensity * laserColor.r;
    sampleColor.b = sampleIntensity * laserColor.b;
    sampleColor.g = sampleIntensity * laserColor.g;

    vec4 effectColor = sampleColor * intensity * 2.0;
    effectColor = effectColor + white * centerIntensity * (pow(intensity, 4.0) * sampleIntensity);

    laserColor = mix(laserColor, effectColor, 0.8);
    baseColor *= pow(1.0 - intensity, 3.0);

    baseColor = mix(baseColor, laserColor, intensity * 2.0);

    gl_FragColor = baseColor;
}
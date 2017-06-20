#ifndef __SHADER_H
#define __SHADER_H

#define STR(s) #s
#define STRV(s) STR(s)
#define SH_POSITION         0
#define SH_TEXTURE          1

const char gVertexShader[] = "#version 300 es                       \n"
        "layout (location = "STRV(SH_POSITION)") in vec3 position;  \n"
        "layout (location = "STRV(SH_TEXTURE)") in vec2 texCoord;   \n"
        "out vec2 TexCoord;                                         \n"
        "uniform mat4 st_matrix;                                    \n"
        "void main() {                                              \n"
        "  gl_Position = vec4(position, 1.0);                       \n"
        "  TexCoord = texCoord;                                     \n"
        "  TexCoord = (st_matrix * vec4(texCoord, 1.0, 1.0)).xy;    \n"
        "}\n";

const char gFragmentShader[] = "#version 300 es                     \n"
        "#extension GL_OES_EGL_image_external : require       \n"
        "precision mediump float;                                   \n"
        "in vec2 TexCoord;                                          \n"
        "uniform samplerExternalOES camTexture;                     \n"
        "uniform sampler2D edgeTexture;                             \n"
        "uniform sampler2D filterTexture;                           \n"
        "out vec4 color;                                            \n"
        "void main() {                                              \n"
        "  vec4 tCamera = texture(camTexture, TexCoord);        \n"
        "  vec4 tEdge = texture(edgeTexture, TexCoord);         \n"
        "  tCamera = tCamera * tEdge;                               \n"
        "  float blueColor = tCamera.b * 63.0;\n"
        "  vec2 quad1;\n"
        "  quad1.y = floor(blueColor/8.0);\n"
        "  quad1.x = floor(blueColor) - (quad1.y * 8.0);\n"
        "  vec2 quad2;\n"
        "  quad2.y = floor(ceil(blueColor)/7.999);\n"
        "  quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n"
        "  vec2 texPos1;\n"
        "  texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * tCamera.r);\n"
        "  texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * tCamera.g);\n"
        "  vec2 texPos2;\n"
        "  texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * tCamera.r);\n"
        "  texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * tCamera.g);\n"
        "  vec4 newColor1 = texture(filterTexture, texPos1);\n"
        "  vec4 newColor2 = texture(filterTexture, texPos2);\n"
        "  vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n"
        "  color = vec4(newColor.rgb, tCamera.w);\n"
        "}\n";
//const char gFragmentShader[] = "#version 300 es         \n"
//        "precision mediump float;                       \n"
//        "in vec2 TexCoord;                              \n"
//        "uniform sampler2D tTexture;                    \n"
//        "out vec4 color;                                \n"
//        "void main() {                                  \n"
//        "  color = texture(tTexture, TexCoord);         \n"
//        "}\n";
const char fragmentHandle[][128] = {
        "camTexture",
        "edgeTexture",
        "filterTexture"
};
const GLfloat rectVertex[] = {
        -1, -1, 0,
        -1, 1, 0,
        1, -1, 0,
        1, 1, 0,
};

const GLfloat rectTexture[] = {
        0, 0,
        0, 1,
        1, 0,
        1, 1,
};
#endif //__SHADER_H

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
        "#extension GL_OES_EGL_image_external : require             \n"
        "precision mediump float;                                   \n"
        "in vec2 TexCoord;                                          \n"
        "uniform samplerExternalOES camTexture;                     \n"
        "uniform sampler2D edgeTexture;                             \n"
        "uniform sampler2D hefeTexture1;                            \n"
        "out vec4 color;                                            \n"
        "void main() {                                              \n"
        "  vec3 tCamera = texture(camTexture, TexCoord).rgb;        \n"
        "  vec3 tEdge = texture(edgeTexture, TexCoord).rgb;         \n"
        "  tCamera = tCamera * tEdge;                               \n"
        "  tCamera = vec3(                                          \n"
        "    texture(hefeTexture1, vec2(tCamera.r, .16666)).r,      \n"
        "    texture(hefeTexture1, vec2(tCamera.g, .5)).g,          \n"
        "    texture(hefeTexture1, vec2(tCamera.b, .83333)).b);     \n"
        "  color = vec4(tCamera, 1.0);                              \n"
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
        "hefeTexture1",
        "hefeTexture2",
        "hefeTexture3",
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

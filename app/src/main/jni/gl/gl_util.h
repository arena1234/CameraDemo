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

const char gFragmentShader[] = "#version 300 es         \n"
        "#extension GL_OES_EGL_image_external : require \n"
        "precision mediump float;                       \n"
        "in vec2 TexCoord;                              \n"
        "uniform samplerExternalOES tTexture;           \n"
        "out vec4 color;                                \n"
        "void main() {                                  \n"
        "  color = texture(tTexture, TexCoord);         \n"
        "}\n";
//const char gFragmentShader[] = "#version 300 es         \n"
//        "precision mediump float;                       \n"
//        "in vec2 TexCoord;                              \n"
//        "uniform sampler2D tTexture;                    \n"
//        "out vec4 color;                                \n"
//        "void main() {                                  \n"
//        "  color = texture(tTexture, TexCoord);         \n"
//        "}\n";
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

#ifndef __SHADER_H
#define __SHADER_H

#define STR(s) #s
#define STRV(s) STR(s)
#define SH_POSITION         0
#define SH_TEXTURE          1

const char gVertexShader[] = "#version 300 es                       \n"
        "layout (location = "STRV(SH_POSITION)") in vec3 position;  \n"
        "void main() {                                              \n"
        "  gl_Position = vec4(position, 1.0);                       \n"
        "}\n";

const char gFragmentShader[] = "#version 300 es     \n"
        "precision mediump float;                   \n"
        "out vec4 color;                            \n"
        "void main() {                              \n"
        "  color = vec4(1.0f, 0.5f, 0.2f, 1.0f);    \n"
        "}\n";
const GLfloat rectVertex[] = {
        -0.5f, -0.5f, 0,
        -0.5f, 0.5f, 0,
        0.5f, -0.5f, 0,
        0.5f, 0.5f, 0,
};

const GLfloat rectTexture[] = {
        0, 0,
        0, 1,
        1, 0,
        1, 1,
};
#endif //__SHADER_H

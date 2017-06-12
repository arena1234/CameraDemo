#include "gl_camera.h"

GLCamera::GLCamera():GLBase() {
}

GLCamera::~GLCamera() {
    free(pVAO);
    free(pVBO);
}

void GLCamera::onSurfaceCreated(GLuint *textureIds, GLuint size) {
    LOGI("[GLCamera:onSurfaceCreated]");
    mProgramShader = createProgram(gVertexShader, gFragmentShader);


    pVAO = (GLuint *) malloc(sizeof(GLuint));
    pVBO = (GLuint *) malloc(2 * sizeof(GLuint));
    glGenVertexArrays(1, pVAO);
    glGenBuffers(2, pVBO);
    LOGD("[GLCamera:onSurfaceCreated]VAO=%d, VBO=%d, %d", pVAO[0], pVBO[0], pVBO[1]);


    glGenTextures(size, textureIds);
    mTextureId = textureIds[0];
    glBindTexture(GL_TEXTURE_2D, mTextureId);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    LOGD("[GLCamera:onSurfaceCreated]mTextureId=%d", mTextureId);
}

void GLCamera::onSurfaceChanged(GLuint w, GLuint h) {
    LOGI("[GLCamera:onSurfaceChanged]w=%d, h=%d", w, h);
    glViewport(0, 0, w, h);

    bindBuffer();
}

void GLCamera::onDrawFrame() {
    glEnable(GL_DEPTH_TEST);
    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glUseProgram(mProgramShader);
    glBindVertexArray(pVAO[0]);
    checkGLError("draw glBindVertexArray +");
    glBindTexture(GL_TEXTURE_2D, mTextureId);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, sizeof(rectVertex) / sizeof(GLfloat) / 3);
    // 解绑VAO
    glBindVertexArray(0);
    checkGLError("draw glBindVertexArray -");
}

void GLCamera::bindBuffer() {
    glBindVertexArray(pVAO[0]);
    checkGLError("bindBuffer glBindVertexArray +");
    // 绑定顶点坐标并设置顶点坐标
    glBindBuffer(GL_ARRAY_BUFFER, pVBO[0]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(rectVertex),
                 rectVertex, GL_STATIC_DRAW);
    // 设置顶点属性指针
    glVertexAttribPointer(SH_POSITION, 3, GL_FLOAT, GL_FALSE,
                          3 * sizeof(GLfloat), (GLvoid *) 0);
    glEnableVertexAttribArray(SH_POSITION);

//    // 绑定纹理坐标并设置纹理坐标
//    glBindBuffer(GL_ARRAY_BUFFER, pVBO[1]);
//    glBufferData(GL_ARRAY_BUFFER, sizeof(rectTexture),
//                 rectTexture, GL_DYNAMIC_DRAW);
//    // 设置纹理属性指针
//    glVertexAttribPointer(SH_TEXTURE, 2, GL_FLOAT, GL_FALSE,
//                          2 * sizeof(GLfloat), (GLvoid *) 0);
//    glEnableVertexAttribArray(SH_TEXTURE);
    // 解绑VAO
    glBindVertexArray(0);
    checkGLError("updateBuffer glBindVertexArray -");
}
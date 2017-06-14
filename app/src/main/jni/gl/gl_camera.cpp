#include "gl_camera.h"

GLCamera::GLCamera() : GLBase() {
}

GLCamera::~GLCamera() {
    free(pVAO);
    free(pVBO);
}

GLuint * GLCamera::onSurfaceCreated(GLuint *size) {
    LOGI("[GLCamera:onSurfaceCreated]");
    mProgramShader = createProgram(gVertexShader, gFragmentShader);
    mSTMatrixHandle = glGetUniformLocation(mProgramShader, "st_matrix");
    char handle[][128] = {
            "camTexture",
            "borderTexture",
            "hefeTexture1",
            "hefeTexture2",
            "hefeTexture3",
    };
    mTexSize = 5;
    *size = mTexSize;
    pTextureId = (GLuint *) malloc(mTexSize * sizeof(GLuint));
    pTextureHandle = (GLint *) malloc(mTexSize * sizeof(GLint));
    for (int i = 0; i < mTexSize; i++) {
        pTextureHandle[i] = glGetUniformLocation(mProgramShader, handle[i]);
    }

    pVAO = (GLuint *) malloc(sizeof(GLuint));
    pVBO = (GLuint *) malloc(2 * sizeof(GLuint));
    glGenVertexArrays(1, pVAO);
    glGenBuffers(2, pVBO);


    glGenTextures(mTexSize, pTextureId);

    glBindTexture(GL_TEXTURE_EXTERNAL_OES, pTextureId[0]);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

    for (int i = 1; i < mTexSize; i++) {
        glBindTexture(GL_TEXTURE_2D, pTextureId[i]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }
    return pTextureId;
}

void GLCamera::onSurfaceChanged(GLuint w, GLuint h) {
    LOGI("[GLCamera:onSurfaceChanged]w=%d, h=%d", w, h);
    glViewport(0, 0, w, h);
    bindBuffer();
}

void GLCamera::onDrawFrame(GLfloat *stMatrix) {
    glEnable(GL_DEPTH_TEST);
    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    glUseProgram(mProgramShader);
    glBindVertexArray(pVAO[0]);
    checkGLError("draw glBindVertexArray +");

    glUniformMatrix4fv(mSTMatrixHandle, 1, GL_FALSE, stMatrix);

    if (pTextureHandle[0] >= 0) {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_EXTERNAL_OES, pTextureId[0]);
        glUniform1i(pTextureHandle[0], 0);
    }

    for(int i=1; i<mTexSize; i++){
        if (pTextureHandle[i] >= 0) {
            glActiveTexture(GL_TEXTURE0+i);
            glBindTexture(GL_TEXTURE_2D, pTextureId[i]);
            glUniform1i(pTextureHandle[i], i);
        }
    }

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

    // 绑定纹理坐标并设置纹理坐标
    glBindBuffer(GL_ARRAY_BUFFER, pVBO[1]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(rectTexture),
                 rectTexture, GL_DYNAMIC_DRAW);
    // 设置纹理属性指针
    glVertexAttribPointer(SH_TEXTURE, 2, GL_FLOAT, GL_FALSE,
                          2 * sizeof(GLfloat), (GLvoid *) 0);
    glEnableVertexAttribArray(SH_TEXTURE);
    // 解绑VAO
    glBindVertexArray(0);
    checkGLError("updateBuffer glBindVertexArray -");
}
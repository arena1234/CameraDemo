#ifndef __GL_CAMERA_H
#define __GL_CAMERA_H

#include <GLES/glext.h>
#include "gl_base.h"
#include "gl_util.h"

class GLCamera : GLBase {
public:
    GLCamera();

    virtual ~GLCamera();

    GLuint * onSurfaceCreated(GLuint *size);

    void onSurfaceChanged(GLuint w, GLuint h);

    void onDrawFrame(GLfloat *stMatrix);

private:
    GLuint mProgramShader;
    GLuint *pTextureId;
    GLuint *pVAO, *pVBO;
    GLint mSTMatrixHandle;
    GLint *pTextureHandle;
    GLuint mTexSize;

    void bindBuffer();
};

#endif //__GL_CAMERA_H

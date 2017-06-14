#ifndef __GL_CAMERA_H
#define __GL_CAMERA_H

#include <GLES/glext.h>
#include "gl_base.h"
#include "gl_util.h"

class GLCamera : GLBase {
public:
    GLCamera();

    virtual ~GLCamera();

    void onSurfaceCreated(GLuint *textureIds, GLuint size);

    void onSurfaceChanged(GLuint w, GLuint h);

    void onDrawFrame(GLfloat *stMatrix);

private:
    GLuint mProgramShader;
    GLuint mTextureId;
    GLuint *pVAO, *pVBO;
    GLuint mTextureTarget;
    GLint mSTMatrixHandle;

    void bindBuffer();
};

#endif //__GL_CAMERA_H

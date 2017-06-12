#ifndef __GL_CAMERA_H
#define __GL_CAMERA_H

#include "gl_base.h"
#include "gl_util.h"

class GLCamera : GLBase {
public:
    GLCamera();

    virtual ~GLCamera();

    void onSurfaceCreated(GLuint *textureIds, GLuint size);

    void onSurfaceChanged(GLuint w, GLuint h);

    void onDrawFrame();
private:
    GLuint mProgramShader;
    GLuint mTextureId;
    GLuint *pVAO, *pVBO;

    void bindBuffer();
};

#endif //__GL_CAMERA_H

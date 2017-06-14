#ifndef GL_BASE_H
#define GL_BASE_H

#include <GLES3/gl3.h>
#include <stdlib.h>
#include <sys/time.h>
#include "log.h"

class GLBase {
public:
    GLBase();

    virtual ~GLBase();

    virtual GLuint * onSurfaceCreated(GLuint *size) = 0;

    virtual void onSurfaceChanged(GLuint w, GLuint h) = 0;

    virtual void onDrawFrame(GLfloat *stMatrix) = 0;

protected:
    GLuint createProgram(const char *pVertexSource, const char *pFragmentSource);

    GLboolean checkGLError(const char *op);

    GLuint64 getCurrentTimeUs();

    GLuint64 getCurrentTimeMs();

private :
    GLuint loadShader(GLenum shaderType, const char *pSource);
};

#endif //GL_BASE_H

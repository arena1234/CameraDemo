#ifndef __NDK_CPP_H
#define __NDK_CPP_H

#include <jni.h>
#include <pthread.h>
#include <android/bitmap.h>
#include "gl/gl_camera.h"
#include "log.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved);
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved);

#ifdef __cplusplus
}
#endif

#endif //__NDK_CPP_H

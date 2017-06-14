#include "ndk_cpp.h"

jobject mObj;
jmethodID mHandleMessageId;
pthread_t mThread;
jboolean bAttachThread = JNI_FALSE;
jboolean bExitMsgQueue = JNI_FALSE;
JavaVM *mVm;
GLCamera *glCamera;

JNIEnv *getJNIEnv() {
    JNIEnv *env = NULL;
    if (mVm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        int status = mVm->AttachCurrentThread(&env, 0);
        if (status < 0) {
            return NULL;
        }
        bAttachThread = JNI_TRUE;
    }
    return env;
}

// 单位毫秒
pthread_cond_t cond;
pthread_mutex_t mutex;
struct timeval now;
struct timespec outtime;

void sleepMs(int nHm) {
    gettimeofday(&now, NULL);
    now.tv_usec += 1000 * nHm;
    if (now.tv_usec > 1000000) {
        now.tv_sec += now.tv_usec / 1000000;
        now.tv_usec %= 1000000;
    }

    outtime.tv_sec = now.tv_sec;
    outtime.tv_nsec = now.tv_usec * 1000;

    pthread_cond_timedwait(&cond, &mutex, &outtime);
}

void sendMsg(jint msg) {
    JNIEnv *env = getJNIEnv();
    env->CallVoidMethod(mObj, mHandleMessageId, msg);
}

void *thread_run(void *arg) {
    JNIEnv *env = getJNIEnv();
    LOGI("[ndk:thread_run]start thread");
    while (!bExitMsgQueue) {
        sendMsg(1234);
        jthrowable exception = env->ExceptionOccurred();
        if (exception) {
            env->ExceptionDescribe();
        }
        sleepMs(2000);
    }
    pthread_detach(mThread);
    pthread_exit(0);
    LOGI("[ndk:thread_run]release thread");
    return NULL;
}

void initCallBack(JNIEnv *env, jobject thiz) {
    jclass mClass = env->GetObjectClass(thiz);
    mObj = env->NewGlobalRef(thiz);
    mHandleMessageId = env->GetMethodID(mClass, "handleMessage", "(I)V");
    bExitMsgQueue = JNI_FALSE;
    pthread_create(&mThread, NULL, thread_run, NULL);
}

void init(JNIEnv *env, jobject thiz) {
    LOGI("[ndk:init]");
//    initCallBack(env, thiz);
    glCamera = new GLCamera();
}

void release(JNIEnv *env, jobject thiz) {
    bExitMsgQueue = JNI_TRUE;
    delete glCamera;
    LOGI("[ndk:release]");
}

jintArray onSurfaceCreated(JNIEnv *env, jobject thiz) {
    GLuint size = 5;
    GLuint *temp = glCamera->onSurfaceCreated(&size);
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, (jint *) temp);
    return result;
}

void onSurfaceChanged(JNIEnv *env, jobject thiz, jint w, jint h) {
    glCamera->onSurfaceChanged(w, h);
}

void onDrawFrame(JNIEnv *env, jobject thiz, jfloatArray stMatrix) {
    glCamera->onDrawFrame(env->GetFloatArrayElements(stMatrix, NULL));
}

JNINativeMethod gMethods[] = {
        {"nativeInit",             "()V",   (void *) init},
        {"nativeRelease",          "()V",   (void *) release},
        {"nativeOnSurfaceCreated", "()[I",  (void *) onSurfaceCreated},
        {"nativeOnSurfaceChanged", "(II)V", (void *) onSurfaceChanged},
        {"nativeOnDrawFrame",      "([F)V", (void *) onDrawFrame},
};

int JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    mVm = vm;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass javaClass = env->FindClass("com/tcl/camerademo/opengl/NdkJava");
    if (javaClass == NULL) {
        return JNI_ERR;
    }
    if (env->RegisterNatives(javaClass, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}

void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    bExitMsgQueue = JNI_TRUE;
    if (bAttachThread) {
        bAttachThread = JNI_FALSE;
        vm->DetachCurrentThread();
    }
    LOGI("[ndk:JNI_OnUnload]");
}
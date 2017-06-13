package com.tcl.camerademo.opengl;

import android.opengl.EGLExt;
import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by qiangwang on 6/12/17.
 */

public class EGLHelper {
    private static final String TAG = "CAM_EglHelper";
    private EGL10 mEgl;
    private EGLDisplay mEglDisplay;
    private EGLSurface mEglSurface;
    private EGLConfig mEglConfig;
    private EGLContext mEglContext;
    private Surface mSurface;

    public EGLHelper(Surface surface) {
        mSurface = surface;
    }

    public boolean createSurface() {
        // step 1
        mEgl = (EGL10) EGLContext.getEGL();

        // step 2
        mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        // step 3
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        // step 4
        int configSpec[] = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 0,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_RENDERABLE_TYPE, EGLExt.EGL_OPENGL_ES3_BIT_KHR,//EGL14.EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE};
        int[] num_config = new int[1];
        if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, null, 0, num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#1 failed");
        }
        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException("No configs match configSpec");
        }
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!mEgl.eglChooseConfig(mEglDisplay, configSpec, configs, numConfigs, num_config)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        mEglConfig = configs[0];

        // step 5
        int[] attrib_list = {
                0x3098/*EGL_CONTEXT_CLIENT_VERSION*/,
                3,
                EGL10.EGL_NONE};
        mEglContext = mEgl.eglCreateContext(mEglDisplay, mEglConfig,
                EGL10.EGL_NO_CONTEXT, attrib_list);
        if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT) {
            mEglContext = null;
            throw new IllegalArgumentException("eglCreateContext failed");
        }
        mEglSurface = null;

        // step 6
        destroySurface();
        try {
            mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, mSurface, null);
        } catch (IllegalArgumentException e) {
        }
        if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
            int error = mEgl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                Log.e(TAG, "eglCreateWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
            }
            return false;
        }

        // step 7
        if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {
            return false;
        }

        return true;
    }

    public int swap() {
        if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
            return mEgl.eglGetError();
        }
        return EGL10.EGL_SUCCESS;
    }

    private void destroySurface() {
        if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
            mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);
            mEgl.eglDestroySurface(mEglDisplay, mEglSurface);
            mEglSurface = null;
        }
    }

    public void finish() {
        destroySurface();
        if (mEglContext != null) {
            if (!mEgl.eglDestroyContext(mEglDisplay, mEglContext)) {
                Log.e(TAG, "eglDestroyContext fail ! display:" + mEglDisplay + " context: " + mEglContext);
            }
            mEglContext = null;
        }
        if (mEglDisplay != null) {
            mEgl.eglTerminate(mEglDisplay);
            mEglDisplay = null;
        }
    }
}

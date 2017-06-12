package com.tcl.camerademo.opengl;

import android.content.Context;
import android.opengl.EGLExt;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public abstract class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private GLThread mGLThread;
    private boolean mDetached;
    private Handler mHandler;

    public GLSurfaceView(Context context) {
        super(context);
        init();
    }

    public GLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mGLThread = new GLThread();
        mGLThread.start();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                refresh();
            }
        };
        refresh();
    }

    private void refresh() {
        mHandler.sendEmptyMessageDelayed(0, 30);
    }

    private void disRefresh() {
        mHandler.removeMessages(0);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mGLThread != null) mGLThread.surfaceCreated();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mGLThread != null) mGLThread.surfaceDestroyed();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if(mGLThread != null) mGLThread.surfaceChanged(w, h);
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (mDetached) {
//            mGLThread.start();
//        }
//        mDetached = false;
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        if (mGLThread != null) {
//            mGLThread.exit();
//        }
//        mDetached = true;
//        super.onDetachedFromWindow();
//    }

    public void onPause() {
        if (mGLThread != null) {
            mGLThread.exit();
        }
        mDetached = true;
        disRefresh();
    }

    public void onResume() {
        if (mDetached) {
            mGLThread = new GLThread();
            mGLThread.start();
        }
        mDetached = false;
        refresh();
    }

    class EglHelper {
        private static final String TAG = "CAM_EglHelper";
        private EGL10 mEgl;
        private EGLDisplay mEglDisplay;
        private EGLSurface mEglSurface;
        private EGLConfig mEglConfig;
        private EGLContext mEglContext;

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
                mEglSurface = mEgl.eglCreateWindowSurface(mEglDisplay, mEglConfig, getHolder(), null);
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

    class GLThread extends Thread {
        private static final String TAG = "CAM_GLThread";
        private EglHelper mEglHelper;
        private boolean mHasSurface;
        private boolean bExitGLThread;
        private int mWidth;
        private int mHeight;

        GLThread() {
            super();
            mWidth = 0;
            mHeight = 0;
        }

        @Override
        public void run() {
            setName("GLThread " + getId());

            try {
                glThreadRunnable();
            } finally {
                finish();
                mGLThread = null;
            }
        }

        private void glThreadRunnable() {
            mEglHelper = new EglHelper();

            boolean createEglContext = true;
            boolean createEglSurface = true;
            boolean sizeChanged = true;
            bExitGLThread = false;

            while (true) {
                if (bExitGLThread) break;
                if (mHasSurface) {
                    if (createEglSurface) {
                        if (!mEglHelper.createSurface()) {
                            continue;
                        }
                        createEglSurface = false;
                    }

                    if (createEglContext) {
                        onSurfaceCreated();
                        createEglContext = false;
                    }

                    if (sizeChanged) {
                        onSurfaceChanged(mWidth, mHeight);
                        sizeChanged = false;
                    }
                    onDrawFrame();

                    mEglHelper.swap();
                }
            }
        }

        private void finish() {
            Log.i(TAG, "finish");
            mEglHelper.finish();
        }

        public void exit() {
            Log.i(TAG, "exit");
            bExitGLThread = true;
        }

        public void surfaceCreated() {
            mHasSurface = true;
        }

        public void surfaceDestroyed() {
            mHasSurface = false;
        }

        public void surfaceChanged(int w, int h) {
            mWidth = w;
            mHeight = h;
        }
    }

    protected abstract void onSurfaceCreated();

    protected abstract void onSurfaceChanged(int width, int height);

    protected abstract void onDrawFrame();
}
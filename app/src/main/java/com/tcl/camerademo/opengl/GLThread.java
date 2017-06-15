package com.tcl.camerademo.opengl;

import android.util.Log;
import android.view.Surface;

public class GLThread extends Thread {
    private static final String TAG = "CAM_GLThread";
    private EGLHelper mEglHelper;
    private boolean mHasSurface;
    private boolean bExitGLThread;
    private int mWidth;
    private int mHeight;
    private GLListener mListener;
    private Surface mSurface;
    private Object mWaitObject;

    public GLThread(Surface surface) {
        super();
        mWidth = 0;
        mHeight = 0;
        mSurface = surface;
        mWaitObject = new Object();
    }

    public void setGLListener(GLListener listener) {
        mListener = listener;
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

    public void refresh() {
        synchronized (mWaitObject) {
            mWaitObject.notifyAll();
        }
    }

    @Override
    public void run() {
        setName("GLThread " + getId());

        try {
            glThreadRunnable();
        } finally {
            mEglHelper.finish();
        }
    }

    private void glThreadRunnable() {
        if (mSurface == null) return;
        mEglHelper = new EGLHelper(mSurface);

        boolean createEglContext = true;
        boolean createEglSurface = true;
        boolean sizeChanged = true;
        bExitGLThread = false;

        while (true) {
            synchronized (mWaitObject) {
                if (bExitGLThread) break;
                if (mHasSurface) {
                    if (createEglSurface) {
                        if (!mEglHelper.createSurface()) {
                            continue;
                        }
                        createEglSurface = false;
                    }

                    if (createEglContext) {
                        if (mListener != null) mListener.onGLCreated();
                        createEglContext = false;
                    }

                    if (sizeChanged) {
                        if (mListener != null) mListener.onGLChanged(mWidth, mHeight);
                        sizeChanged = false;
                    }

                    if (mListener != null) mListener.onGLDrawFrame();

                    mEglHelper.swap();

                    try {
                        mWaitObject.wait();
                    } catch (InterruptedException mE) {
                        mE.printStackTrace();
                    }
                }
            }
        }
    }

    public interface GLListener {
        void onGLCreated();

        void onGLChanged(int w, int h);

        void onGLDrawFrame();
    }
}
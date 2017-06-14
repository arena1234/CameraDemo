package com.tcl.camerademo.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

public class Preview implements GLThread.GLListener {
    private static final String TAG = "CAM_Preview";
    private NdkJava mNdkJava;
    private GLThread mGLThread;
    private boolean mDetached;
    private Handler mHandler;
    private SurfaceTexture mCameraSurfaceTexture;

    public Preview(Surface surface) {
        mNdkJava = new NdkJava();
        mGLThread = new GLThread(surface);
        mGLThread.setGLListener(this);
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

    public void onDestroy() {
        mNdkJava.release();
    }

    public void surfaceCreated(Surface surface) {
        Log.i(TAG, "surfaceCreated");
        if (mGLThread != null) mGLThread.surfaceCreated();
    }

    public void surfaceChanged(Surface surface, int w, int h) {
        Log.i(TAG, "surfaceChanged");
        if (mGLThread != null) mGLThread.surfaceChanged(w, h);
        if (mDetached) {
            mGLThread = new GLThread(surface);
            mGLThread.start();
        }
        mDetached = false;
        refresh();
    }

    public void surfaceDestroyed() {
        Log.i(TAG, "surfaceDestroyed");
        if (mGLThread != null) mGLThread.surfaceDestroyed();

        if (mGLThread != null) {
            mGLThread.exit();
            try {
                mGLThread.join();
            } catch (InterruptedException mE) {
                mE.printStackTrace();
            }
            mGLThread = null;
        }
        mDetached = true;
        disRefresh();
    }

    @Override
    public void onGLCreated() {
        int[] textureid = mNdkJava.onSurfaceCreated();
        if (mListener != null) mListener.onTexturePrepared(textureid);
    }

    @Override
    public void onGLChanged(int w, int h) {
        mNdkJava.onSurfaceChanged(w, h);
    }

    int fpsCount = 0;
    long lastTime = 0;

    @Override
    public void onGLDrawFrame() {
        if (mCameraSurfaceTexture != null) {
            synchronized (mCameraSurfaceTexture) {
                mCameraSurfaceTexture.updateTexImage();
                float matrix[] = new float[16];
                Matrix.setIdentityM(matrix, 0);
                mCameraSurfaceTexture.getTransformMatrix(matrix);
                mNdkJava.onDrawFrame(matrix);

                fpsCount++;
                long currTime = System.currentTimeMillis();
                if (currTime - lastTime >= 5000) {
                    lastTime = currTime;
                    Log.d(TAG, "fps=" + (fpsCount * 0.2f));
                    fpsCount = 0;
                }
            }
        }
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mCameraSurfaceTexture = surfaceTexture;
    }

    public interface TextureListener {
        void onTexturePrepared(final int[] textureid);
    }

    private TextureListener mListener;

    public void setTextureListener(TextureListener listener) {
        mListener = listener;
    }
}

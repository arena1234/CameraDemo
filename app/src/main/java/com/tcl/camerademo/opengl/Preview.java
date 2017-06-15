package com.tcl.camerademo.opengl;

import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

import com.tcl.camerademo.ImageUtil;

public class Preview implements GLThread.GLListener {
    private static final String TAG = "CAM_Preview";
    private NdkJava mNdkJava;
    private GLThread mGLThread;
    private boolean mDetached;
    private Handler mHandler;
    private SurfaceTexture mCameraSurfaceTexture;
    private int fps = 24;
    private boolean bAutoRefresh;

    public Preview(Surface surface) {
        mNdkJava = new NdkJava();
        mGLThread = new GLThread(surface);
        mGLThread.setGLListener(this);
        mGLThread.start();
        bAutoRefresh = true;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                refresh();
            }
        };
    }

    public void refresh() {
        if (mGLThread != null) mGLThread.refresh();
        if(bAutoRefresh) mHandler.sendEmptyMessageDelayed(0, (int) (1000.0 / fps));
    }

    private void disRefresh() {
        mHandler.removeMessages(0);
    }

    public void setAutoRefresh(boolean auto){
        bAutoRefresh = auto;
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
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid[1]);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, ImageUtil.bitmap[0], 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid[2]);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, ImageUtil.bitmap[1], 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid[3]);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, ImageUtil.bitmap[2], 0);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureid[4]);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, ImageUtil.bitmap[3], 0);
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
                    Log.d(TAG, "fps=" + fpsCount * 0.2f);
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

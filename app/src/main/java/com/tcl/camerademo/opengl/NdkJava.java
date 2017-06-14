package com.tcl.camerademo.opengl;

import android.util.Log;

public class NdkJava {
    private static final String TAG = "CAM_NdkJava";

    public NdkJava() {
        init();
    }

    public int[] onSurfaceCreated() {
        return nativeOnSurfaceCreated();
    }

    public void onSurfaceChanged(int w, int h) {
        nativeOnSurfaceChanged(w, h);
    }

    public void onDrawFrame(float[] stMatrix) {
        nativeOnDrawFrame(stMatrix);
    }

    private void init() {
        nativeInit();
    }

    public void release() {
        nativeRelease();
    }

    public void handleMessage(int msg) {
        Log.d(TAG, "rev msg : " + msg);
    }

    private native int[] nativeOnSurfaceCreated();

    private native void nativeOnSurfaceChanged(int w, int h);

    private native void nativeOnDrawFrame(float[] stMatrix);

    private native void nativeInit();

    private native void nativeRelease();

    static {
        System.loadLibrary("opengl_jni");
    }
}

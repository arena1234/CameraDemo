package com.tcl.camerademo.opengl;

import android.content.Context;
import android.util.AttributeSet;

public class CameraSurfaceView extends GLSurfaceView {
    private NdkJava mNdkJava;

    public CameraSurfaceView(Context context) {
        super(context);
        init();
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mNdkJava = new NdkJava();
    }

    public void onDestroy(){
        mNdkJava.release();
    }

    @Override
    public void onSurfaceCreated() {
        if (mNdkJava != null) mNdkJava.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        if (mNdkJava != null) mNdkJava.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        if (mNdkJava != null) mNdkJava.onDrawFrame();
    }
}

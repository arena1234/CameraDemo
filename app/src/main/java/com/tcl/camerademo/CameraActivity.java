package com.tcl.camerademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tcl.camerademo.opengl.Preview;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    private static final String TAG = "CAM_CameraActivity";
    private SurfaceView mSurfaceView;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mSurfaceView = new SurfaceView(this));
        mSurfaceView.getHolder().addCallback(this);
        mPreview = new Preview(mSurfaceView.getHolder().getSurface());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreview.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPreview.surfaceCreated(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mPreview.surfaceChanged(holder.getSurface(), width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mPreview.surfaceDestroyed();
    }
}

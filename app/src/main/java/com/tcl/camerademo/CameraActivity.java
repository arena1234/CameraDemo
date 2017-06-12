package com.tcl.camerademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tcl.camerademo.opengl.CameraSurfaceView;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CAM_CameraActivity";
    CameraSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mSurfaceView = new CameraSurfaceView(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSurfaceView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.onResume();
    }
}

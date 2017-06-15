package com.tcl.camerademo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.tcl.camerademo.opengl.Preview;

import java.util.Arrays;

public class CameraActivity extends BaseActivity implements
        SurfaceHolder.Callback,
        Preview.TextureListener {
    private static final String TAG = "CAM_CameraActivity";
    private SurfaceView mSurfaceView;
    private Preview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageUtil.init(this);
        setContentView(mSurfaceView = new SurfaceView(this));
        mSurfaceView.getHolder().addCallback(this);
        mPreview = new Preview(mSurfaceView.getHolder().getSurface());
        mPreview.setTextureListener(this);
        mPreview.setAutoRefresh(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreview.onDestroy();
        closeCamera();
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

    @Override
    public void onTexturePrepared(final int[] textureid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraSurfaceTexture = new SurfaceTexture(textureid[0]);
                mCameraSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        mPreview.refresh();
                    }
                });
                mPreview.setSurfaceTexture(mCameraSurfaceTexture);
                openCamera();
            }
        });
    }

    private CameraDevice mCameraDevice = null;
    private CameraManager mCameraManager = null;
    private CameraCharacteristics mCameraCharacteristics = null;
    private CaptureRequest.Builder mPreviewBuilder = null;
    private Handler mHandler = null;
    private SurfaceTexture mCameraSurfaceTexture;

    private void openCamera() {
        try {
            mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = "" + CameraCharacteristics.LENS_FACING_FRONT;
            mCameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCameraManager.openCamera(cameraId, mStateCB, mHandler);
            Log.d(TAG, "openCamera");
        } catch (CameraAccessException e) {
            Log.e(TAG, "Open camera fail.");
        }
    }

    private void closeCamera() {
        if (null != mCameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private void startPreview(CameraDevice camera) {
        try {
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCameraSurfaceTexture.setDefaultBufferSize(1280, 720);
            Surface surface = new Surface(mCameraSurfaceTexture);
            mPreviewBuilder.addTarget(surface);
            mPreviewBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT);
            mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
            camera.createCaptureSession(Arrays.asList(surface), mSessionPreviewCB, mHandler);
            Log.d(TAG, "createCaptureSession");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback mStateCB = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.d(TAG, "mStateCB onOpened");
            mCameraDevice = camera;
            startPreview(camera);
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.e(TAG, "mStateCB onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.e(TAG, "mStateCB onError " + error);
        }
    };

    private CameraCaptureSession.StateCallback mSessionPreviewCB =
            new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    Log.d(TAG, "mSessionPreviewCB onConfigured ");
                    try {
                        session.setRepeatingRequest(mPreviewBuilder.build(), null, mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "mSessionPreviewCB onConfigureFailed ");
                }
            };

    private CameraCaptureSession.CaptureCallback mSessionCaptureCB =
            new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }

                @Override
                public void onCaptureFailed(CameraCaptureSession session, CaptureRequest request, CaptureFailure failure) {
                    super.onCaptureFailed(session, request, failure);
                }
            };
}

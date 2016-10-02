package com.example.xq.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.xq.helper.CameraHelper;
import com.example.xq.mysolution.R;
import com.example.xq.utils.BitmapUtil;
import com.example.xq.utils.DisplayUtil;
import com.example.xq.utils.GPUImageFilterTools;
import com.example.xq.utils.MagicParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class CameraActivity extends AppCompatActivity {
    public static final int RequestCode = 1234;
    public static final String TAG = "TAG";
    private GPUImage mGPUImage;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageFilter mFilter;
    private GPUImageFilterTools.FilterAdjuster mFilterAdjuster;
    private SwitchGLSurfaceView switchGLSurfaceView;
    private int i = 0;
    private File pictureFile;
    private Camera camera;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        mGPUImage = new GPUImage(this);
        switchGLSurfaceView = (SwitchGLSurfaceView)findViewById(R.id.surfaceView);
        tabLayout = (TabLayout)findViewById(R.id.filtername);
        GPUImageFilterTools.init();//初始化滤镜种类
        initFilterName();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchFilterTo(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Point p = DisplayUtil.getScreenMetrics(CameraActivity.this);
        //设置switchGLSurfaceView大小
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(p.x,p.x);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        switchGLSurfaceView.setLayoutParams(params);
        mGPUImage.setGLSurfaceView(switchGLSurfaceView);

        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();
        switchFilterTo(i);
        switchGLSurfaceView.setSwitchFilter(new SwitchGLSurfaceView.Switch(){
            @Override
            public void left() {
                if(--i<0){
                    Log.i(TAG,"已经最左了left");
                    i=0;
                }
                Log.i(TAG,"left");
                Log.i(TAG,"names:" + GPUImageFilterTools.filters.names.get(i));
                switchFilterTo(i);
            }

            @Override
            public void right() {
                if(++i>=GPUImageFilterTools.filters.filters.size()){
                    Log.i(TAG,"已经最右了right");
                    i=GPUImageFilterTools.filters.filters.size()-1;
                }
                Log.i(TAG,"right");
                Log.i(TAG,"names:" + GPUImageFilterTools.filters.names.get(i));
                switchFilterTo(i);
            }
        });
    }
    /*切换滤镜*/
    private void switchFilterTo(int i) {
        this.i = i;
        tabLayout.getTabAt(i).select();
        GPUImageFilter filter = GPUImageFilterTools.createFilterForType(CameraActivity.this, GPUImageFilterTools.filters.filters.get(i));
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImage.setFilter(mFilter);
            mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(mFilter);
        }
        Log.i(TAG,"filter");
    }

    public void initFilterName(){
        for(int i=0;i<GPUImageFilterTools.filters.names.size();i++){
            tabLayout.addTab(tabLayout.newTab().setText(GPUImageFilterTools.filters.names.get(i)));
        }
    }

   /*保存图片*/
    public void onClick(View v){
        if (mCamera.mCameraInstance.getParameters().getFocusMode().equals(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            takePicture();
        } else {
            mCamera.mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(final boolean success, final Camera camera) {
                    takePicture();
                }
            });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String string = "";
            for (String str:permissions) {
                string+=str;
            }
            Log.i(TAG,string);
            for(int i=0;i<grantResults.length;i++){
                Log.i(TAG,"grantResults:" + grantResults[i]);
            }
            takePicture();
        } else {
            Toast.makeText(this,"没有权限",Toast.LENGTH_LONG).show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void takePicture() {
        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RequestCode);
            return;
        }
        // TODO get a size that is about the size of the screen
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();
        params.setRotation(90);
        for (int i=0;i<params.getSupportedPictureSizes().size();i++){
            if(1920<=params.getSupportedPictureSizes().get(i).width&&1080<=params.getSupportedPictureSizes().get(i).height){
                params.setPictureSize(params.getSupportedPictureSizes().get(i).width,params.getSupportedPictureSizes().get(i).height);//调整清晰度
                break;
            }
        }
        Log.i(TAG, "getPictureSize: " + params.getPictureSize().width + "x" + params.getPictureSize().height);

        for (Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i(TAG, "Supported: " + size.width + "x" + size.height);

        }

        mCamera.mCameraInstance.setParameters(params);
        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {
                        pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("ASDF",
                                    "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ASDF", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ASDF", "Error accessing file: " + e.getMessage());
                        }
                        data = null;
                        CameraActivity.this.camera = camera;
                        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        Bitmap bp = BitmapUtil.centerSquareScaleBitmap(bitmap,bitmap.getWidth());
                        save(bp);
                    }
                });
}

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            Toast.makeText(CameraActivity.this,"没有存储卡",Toast.LENGTH_LONG).show();
            return null;
        }
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            Log.i(TAG,"mediaStorageDir:" + mediaStorageDir.getAbsolutePath());
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    //将裁剪后的正方形图片保存到相册里
    public void save(Bitmap bitmap){
        final GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mGPUImage.saveToPictures(bitmap, "GPUImage",
                System.currentTimeMillis() + ".jpg",
                new GPUImage.OnPictureSavedListener() {
                    @Override
                    public void onPictureSaved(final Uri
                                                       uri) {
                        Toast.makeText(CameraActivity.this,"saved success:",Toast.LENGTH_LONG).show();
                        pictureFile.delete();
                        camera.startPreview();
                        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mCamera.onResume();
    }

    @Override
    protected void onPause() {
        mCamera.onPause();
        super.onPause();
    }

    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    CameraActivity.this, mCurrentCameraId);
            CameraHelper.CameraInfo2 cameraInfo = new CameraHelper.CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
            for (int i=0;i<parameters.getSupportedPreviewSizes().size();i++){
                if(1280<=parameters.getSupportedPreviewSizes().get(i).width&&720<=parameters.getSupportedPreviewSizes().get(i).height){
                    parameters.setPreviewSize(parameters.getSupportedPreviewSizes().get(i).width,parameters.getSupportedPreviewSizes().get(i).height);//调整清晰度
                    break;
                }
            }
            mCameraInstance.setParameters(parameters);
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /** A safe way to get an instance of the Camera object. */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;
            MagicParams.context = null;
        }
    }
}

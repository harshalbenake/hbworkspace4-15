package com.example.harshalbenake.imageprocessing1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.Timestamp;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import utils.Utility;

public class CameraActivity extends Activity {

    private ImageView iv_profile;
    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int sRC_ActionImageCapture = 1001;
    private boolean safeToTakePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_camera);
        System.out.println("CameraActivityNew");
        initLayout();
    }


    /**
     * Initialize Layout
     */
    private void initLayout() {
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        iv_profile = (ImageView) findViewById(R.id.iv_profile);
        Button btn_capture=(Button)findViewById(R.id.btn_capture);
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);
        /*new Timer().scheduleAtFixedRate(new TimerTask() {
            public void run() {
                captureImage();
            }
        }, 1000, 10000);
*/
    }


    /**
     * This method is used to capture image.
     */
    private void captureImage() {
        System.out.println("captureImage called");
        if (safeToTakePicture) {
            try {
                //Toast.makeText(getBaseContext(),"Image Captured",Toast.LENGTH_SHORT).show();
                // get an image from the camera
                if(mCamera==null){
                    mCamera = getCameraInstance();
                }
                mCamera.takePicture(null, null, mPicture);
                safeToTakePicture = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * Check if this device has a camera
     */

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /**
     * A basic Camera preview class
     */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                setCameraDisplayOrientation(0, mCamera);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                safeToTakePicture = true;
            } catch (Exception e) {
                System.out.println("Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                System.out.println("Error starting camera preview: " + e.getMessage());
            }
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            System.out.println("onPictureTaken called");
            File file = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name));
            if(!file.exists()) {
                file.mkdirs();
            }
            File pictureFile = new File(file, getResources().getString(R.string.app_name) + ".jpg");
            mCamera.startPreview();
            if (pictureFile == null) {
                //no path to picture, return
                safeToTakePicture = true;
                System.out.println("Error creating media file, check storage permissions: ");
                return;
            }

            try {
//                Uri uri = Uri.fromFile(pictureFile);
//                final InputStream imageStream = getContentResolver().openInputStream(uri);
//                final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

              /*  ExifInterface exif = null;
                try {
                    exif = new ExifInterface(pictureFile.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                Bitmap bmRotated = rotateBitmap(bitmap, orientation);
*/

              /*  Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                iv_profile.setImageBitmap(rotatedBitmap);*/


                try {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    final Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    scanFile(bmRotated,pictureFile);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv_profile.setImageBitmap(bmRotated);
                            iv_profile.invalidate();
                        }
                    });

                  /*  iv_profile.post(new Runnable() {
                        int j = 0;

                        @Override
                        public void run() {
                            iv_profile.setImageBitmap(bmRotated);
                            if (j++ < 10) {
                                iv_profile.postDelayed(this, 1000);
                            }
                        }
                    });*/
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (Exception e) {
                System.out.println("Error accessing file: " + e.getMessage());
            }
            //finished saving picture
            safeToTakePicture = true;
        }
    };



    /**
     * set Camera Display Orientation
     *
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(int cameraId, Camera camera) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        int result;
        if (Build.VERSION.SDK_INT > 10) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360; // compensate the mirror
            } else { // back-facing
                result = (info.orientation - degrees + 360) % 360;
            }
            camera.setDisplayOrientation(result);
        } else {
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                camera.setDisplayOrientation(90);
            } else {
                camera.setDisplayOrientation(270);
            }
        }
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {


        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private void scanFile(Bitmap bitmap, File pictureFile) {
        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fileOutputStream);
//            myOutWriter.close();
//            fileOutputStream.close();
//            System.out.println("File saved");

            if(pictureFile!=null) {
                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.init(Environment.getExternalStorageDirectory().getPath() + "/tesseract/", "eng"); // myDir + "/tessdata/eng.traineddata" must be present
                baseApi.setImage(pictureFile);
                String recognizedText = baseApi.getUTF8Text(); // Log or otherwise display this string...
                System.out.println("recognizedText: " + recognizedText);
                Utility.writeLogIntoFile(CameraActivity.this, recognizedText);
                baseApi.end();
            }else{
                Toast.makeText(CameraActivity.this,"pictureFile: "+pictureFile,Toast.LENGTH_LONG).show();
            }
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            System.out.println("byteArray: " + byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

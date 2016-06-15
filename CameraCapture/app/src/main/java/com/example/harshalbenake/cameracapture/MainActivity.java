package com.example.harshalbenake.cameracapture;

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
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {

    private TextInputLayout til_number, til_confirmnumber;
    private ImageView iv_profile;
    private boolean isPASSWORD_HIDDEN = true;
    private Camera mCamera;
    private CameraPreview mPreview;
    public static final int sRC_ActionImageCapture = 1001;
    private boolean safeToTakePicture = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initLayout();

    }


    /**
     * Initialize Layout
     */
    private void initLayout() {
        til_number = (TextInputLayout) findViewById(R.id.til_number);
        til_confirmnumber = (TextInputLayout) findViewById(R.id.til_confirmnumber);
        final EditText et_confirmnumber = (EditText) findViewById(R.id.et_confirmnumber);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        Button btn_captureimage = (Button) findViewById(R.id.btn_captureimage);
        Button btn_next = (Button) findViewById(R.id.btn_next);

        iv_profile = (ImageView) findViewById(R.id.iv_profile);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);


        btn_captureimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        et_confirmnumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (et_confirmnumber.getRight() - et_confirmnumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (isPASSWORD_HIDDEN == true) {
                            // show password
                            et_confirmnumber.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            isPASSWORD_HIDDEN = false;
                            int position = et_confirmnumber.length();
                            Editable etext = et_confirmnumber.getText();
                            Selection.setSelection(etext, position);
                        } else if (isPASSWORD_HIDDEN == false) {
                            // hide password
                            et_confirmnumber.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            isPASSWORD_HIDDEN = true;
                            int position = et_confirmnumber.length();
                            Editable etext = et_confirmnumber.getText();
                            Selection.setSelection(etext, position);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

    }


    /**
     * Validating data
     *
     * @param strNumber
     * @param strConfirmNumber
     * @return
     */
    public boolean isValidateData(String strNumber, String strConfirmNumber) {
        if (strNumber != null && !strNumber.equalsIgnoreCase("")
                && strConfirmNumber != null && !strConfirmNumber.equalsIgnoreCase("")
                && strNumber.equalsIgnoreCase(strConfirmNumber)
                ) {
            til_number.setErrorEnabled(false);
            til_confirmnumber.setErrorEnabled(false);
            return true;
        } else {
            if (strNumber == null || strNumber.equalsIgnoreCase("")) {
                til_number.setError("");
                til_number.setError(getResources().getString(R.string.msg_enter_number));
                til_number.requestFocus();
            } else if (strConfirmNumber == null || strConfirmNumber.equalsIgnoreCase("")) {
                til_confirmnumber.setError("");
                til_confirmnumber.setError(getResources().getString(R.string.msg_enter_confirmnumber));
                til_confirmnumber.requestFocus();
            } else if (strNumber != null && !strNumber.equalsIgnoreCase("")
                    && strConfirmNumber != null && !strConfirmNumber.equalsIgnoreCase("")
                    && !strNumber.equalsIgnoreCase(strConfirmNumber)) {
                til_confirmnumber.setError("");
                til_confirmnumber.setError(getResources().getString(R.string.msg_same_confirmnumber));
                til_confirmnumber.requestFocus();
            }
            return false;
        }
    }


    /**
     * This method is used to capture image.
     */
    private void captureImage() {
        String strNumber = til_number.getEditText().getText().toString();
        String strConfirmNumber = til_confirmnumber.getEditText().getText().toString();
        if (isValidateData(strNumber, strConfirmNumber)) {
           /* //camera stuff
            Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //folder stuff
            File file = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name));
            file.mkdirs();
            File image = new File(file, strNumber + ".jpg");
            Uri uri = Uri.fromFile(image);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(imageIntent, sRC_ActionImageCapture);*/

            if (safeToTakePicture) {
                // get an image from the camera
                mCamera.takePicture(null, null, mPicture);
                safeToTakePicture = false;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        System.out.println(" " + requestCode + " " + resultCode);
        String strNumber = til_number.getEditText().getText().toString();
        switch (requestCode) {
            case sRC_ActionImageCapture:
                if (resultCode == RESULT_OK) {
                    try {
                        String file = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name);
                        File image = new File(file, strNumber + ".jpg");
                        Uri uri = Uri.fromFile(image);
                        final InputStream imageStream = getContentResolver().openInputStream(uri);
                        final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                        iv_profile.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
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

            File pictureFile = getOutputMediaFile();
            mCamera.startPreview();
            Toast.makeText(MainActivity.this,"Image Captured",Toast.LENGTH_SHORT).show();
            if (pictureFile == null) {
                //no path to picture, return
                safeToTakePicture = true;
                System.out.println("Error creating media file, check storage permissions: ");
                return;
            }

            try {
                Uri uri = Uri.fromFile(pictureFile);
                final InputStream imageStream = getContentResolver().openInputStream(uri);
                final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

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
                    Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    iv_profile.setImageBitmap(bmRotated);
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
     * Create a file Uri for saving an image or video
     */
    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        String strNumber = til_number.getEditText().getText().toString();
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = Environment.getExternalStorageDirectory();

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String file = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.app_name);
        File mediaFile = new File(file, strNumber + ".jpg");
        return mediaFile;
    }

    /**
     * set Camera Display Orientation
     *
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        int result;
        if (Build.VERSION.SDK_INT > 10) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(cameraId, info);
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
}

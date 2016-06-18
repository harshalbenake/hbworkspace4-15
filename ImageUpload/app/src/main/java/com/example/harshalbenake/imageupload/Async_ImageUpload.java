package com.example.harshalbenake.imageupload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * This async task is used to send ImageUpload to server.
 * Created by <b>Harshal Benake</b> on 10/11/15.
 */
public class Async_ImageUpload extends AsyncTask<String, String, String> {
    private MainActivity mActivity;
    ProgressDialog progressDialog = null;
    private ResponseManager mResponseManager;
    private String status;

    public Async_ImageUpload(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (isConnected(mActivity)) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setProgressStyle(android.R.attr.progressBarStyleLarge);
            progressDialog.setMessage("Loading");
            progressDialog.show();
            progressDialog.setCancelable(false);
        } else {
            System.out.println("Internet lost");
            cancel(true);
        }
    }

    @Override
    public String doInBackground(String... params) {
        if (!isCancelled()) {
            try {
                sendPostDataRegistrationWS();
                if (mResponseManager != null && mResponseManager.response != null
                        && !mResponseManager.response.equalsIgnoreCase("")) {
                    if (mResponseManager.status == ResponseManager.SC_OK) {
                        System.out.println("Async_ImageUpload response: " + mResponseManager.response);
                        JSONObject jsonObject = new JSONObject(mResponseManager.response);
                        status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");
                        if (status != null && status.equalsIgnoreCase("200")) {

                        }
                        return message;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String message) {
        super.onPostExecute(message);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (message != null && !message.equalsIgnoreCase("")) {
            System.out.println("message: " + message);
        }
    }

    /**
     * This method is use to check the device internet connectivity.
     *
     * @param context
     * @return true :if your device is connected to internet.
     * false :if your device is not connected to internet.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null)
            return false;
        if (info.getState() != NetworkInfo.State.CONNECTED)
            return false;

        return true;
    }


    /**
     * This webservice method is used to send data to server.
     *
     * @return
     */
    public ResponseManager sendPostDataRegistrationWS() {
        ResponseManager responseManager = new ResponseManager();
        responseManager.status = ResponseManager.exception;
        responseManager.response = "Server Error";
        try {
            final String strUrl = mActivity.getResources().getString(R.string.webservice_url);
            String strFilePath = Environment.getExternalStorageDirectory() + File.separator + "hb" + File.separator + "156" + ".jpg";
            File file = new File(strFilePath);
            HttpURLConnection httpURLConnection = null;
            DataOutputStream dataOutputStream = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int maxBufferSize = 1 * 2048 * 2048;
            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(strUrl);
            // Open a HTTP  connection to  the URL
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoInput(true); // Allow Inputs
            httpURLConnection.setDoOutput(true); // Allow Outputs
            httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setRequestProperty("UploadedImage", file.getName());
            //  conn.setRequestProperty("Params","Registration");
            dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

            //first parameter - type
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"parameter\"" + lineEnd + lineEnd
                    + "parameter" + lineEnd);

            //uploaded_file parameter - UploadedImage
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + file.getName() + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);

            // create a buffer of  maximum size
            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            // send multipart form data necesssary after file data...
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            //close the streams //
            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();

            // Execute HTTP Post Request
            String strResponse =  httpURLConnection.getResponseMessage();
            System.out.println("response " + ": " + strResponse);
            int statusCode = httpURLConnection.getResponseCode();
            if (statusCode == HttpStatus.SC_OK) {
                responseManager.status = statusCode;
                responseManager.response = strResponse;
                responseManager.message = "";
            } else {
                responseManager.status = statusCode;
                responseManager.response = ResponseManager.handledResponseMessage(mActivity, statusCode);
                responseManager.message = ResponseManager.handledResponseMessage(mActivity, statusCode);
            }
            return responseManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseManager;
    }
}

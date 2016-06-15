package com.example.harshalbenake.decodeqrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decodeQRCode();
            }
        });
    }

    public void decodeQRCode(){
        try
        {
            File directory = new File (Environment.getExternalStorageDirectory() + "/hb");
            File file = new File(directory, "hbcode.jpg"); //or any other format supported
            FileInputStream inputStream = new FileInputStream(file);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null)
            {
                System.out.println("uri is not a bitmap," + inputStream.toString());
            }
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            bitmap = null;
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
                Result result = reader.decode(bBitmap);
                System.out.println("XML result: "+result);
                JSONObject xmlJSONObj = XML.toJSONObject(result.toString());
                String jsonPrettyPrintString = xmlJSONObj.toString();
                System.out.println(jsonPrettyPrintString);
                System.out.println("JSON result: "+jsonPrettyPrintString);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

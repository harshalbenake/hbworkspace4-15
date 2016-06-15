package com.example.harshalbenake.zipcompressfiles;

import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class MainActivity extends Activity {

    String inputPathExternal = Environment.getExternalStorageDirectory().getPath()+ "/ZipDemo/";
    String inputPathInternal = Environment.getExternalStorageDirectory()+ "/hb/";
    String inputFile = "Apply.zip";
    String outputPath = Environment.getExternalStorageDirectory().getPath()+ "/UnZipDemo/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // declare an array for storing the files
        // i.e the path of your source files
        String[] s = new String[2];

    // Type the path of the files in here
        s[0] = inputPathInternal + "/CAS.db";
        s[1] = inputPathInternal + "/hb.txt"; // /sdcard/ZipDemo/textfile.txt

    // first parameter is d files second parameter is zip file name
        ZipManager zipManager = new ZipManager();

    // calling the zip function
        zipManager.zip(s, inputPathInternal + inputFile);
    }

}

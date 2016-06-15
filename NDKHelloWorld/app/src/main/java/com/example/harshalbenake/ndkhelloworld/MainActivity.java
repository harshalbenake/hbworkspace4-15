package com.example.harshalbenake.ndkhelloworld;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    static {
        System.loadLibrary("helloworld"); // "helloworldjni.dll" in Windows, "libhelloworldjni.so" in Unixes
    }

    // A native method that returns a Java String to be displayed on the
    // TextView
    public native String getMessage();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a TextView.
        TextView textView = new TextView(this);
        // Retrieve the text from native method getMessage()
        textView.setText(getMessage());
        setContentView(textView);
    }
}

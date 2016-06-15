package com.example.harshalbenake.imposeimage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;


public class MainActivity extends Activity {

    private ImageView resultImage;
    private ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        resultImage = (ImageView) findViewById(R.id.imageView3);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView1.setImageDrawable(null);
                Crop.pickImage(MainActivity.this);
            }
        });

        imageView1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(null, shadowBuilder, view, 0);
                return true;
            }
        });

        imageView2.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent motionEvent) {
                if (motionEvent.getAction() == DragEvent.ACTION_DROP) {
                    float left = motionEvent.getX();
                    float top = motionEvent.getY();
                    Bitmap firstImage = BitmapFactory.decodeResource(getResources(), R.drawable.proof);
                    imageView1.destroyDrawingCache();
                    imageView1.buildDrawingCache();
                    Bitmap secondImage = imageView1.getDrawingCache();
                    Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
                    Canvas canvas = new Canvas(result);
                    canvas.drawBitmap(firstImage, 0f, 0f, null);
                    canvas.drawBitmap(secondImage, left - 130, top - 60, null);
                    resultImage.setImageBitmap(result);
                }
                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(requestCode + " resultCode: " + resultCode);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            Crop.of(data.getData(), destination).withAspect(400, 200).start(this);
        } else if (requestCode == Crop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                imageView1.setImageURI(Crop.getOutput(data));
            } else if (resultCode == Crop.RESULT_ERROR) {
                System.out.println(Crop.getError(data).getMessage());
            }
        }
    }
}

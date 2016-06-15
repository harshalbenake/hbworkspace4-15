package com.example.harshalbenake.openpdfile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

public class Main2Activity extends PdfViewerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    @Override
    public int getPreviousPageImageResource() {
        return 0;
    }

    @Override
    public int getNextPageImageResource() {
        return 0;
    }

    @Override
    public int getZoomInImageResource() {
        return 0;
    }

    @Override
    public int getZoomOutImageResource() {
        return 0;
    }

    @Override
    public int getPdfPasswordLayoutResource() {
        return 0;
    }

    @Override
    public int getPdfPageNumberResource() {
        return 0;
    }

    @Override
    public int getPdfPasswordEditField() {
        return 0;
    }

    @Override
    public int getPdfPasswordOkButton() {
        return 0;
    }

    @Override
    public int getPdfPasswordExitButton() {
        return 0;
    }

    @Override
    public int getPdfPageNumberEditField() {
        return 0;
    }

}

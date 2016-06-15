package com.example.harshalbenake.openpdfile;


import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.os.Bundle;

public class Second extends PdfViewerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    public int getPreviousPageImageResource() {
        return R.drawable.ic_launcher;
    }

    public int getNextPageImageResource() {
        return R.drawable.ic_launcher;
    }

    public int getZoomInImageResource() {
        return 0;
    }

    public int getZoomOutImageResource() {
        return 0;
    }

    public int getPdfPasswordLayoutResource() {
        return 0;
    }

    public int getPdfPageNumberResource() {
        return 0;
    }

    public int getPdfPasswordEditField() {
        return 0;
    }

    public int getPdfPasswordOkButton() {
        return 1;
    }

    public int getPdfPasswordExitButton() {
        return 1;
    }

    public int getPdfPageNumberEditField() {
        return 0;
    }
}
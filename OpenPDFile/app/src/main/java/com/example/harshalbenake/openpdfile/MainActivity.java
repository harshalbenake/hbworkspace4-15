package com.example.harshalbenake.openpdfile;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.joanzapata.pdfview.PDFView;
//Imports:
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import net.sf.andpdf.nio.ByteBuffer;
import net.sf.andpdf.refs.HardReference;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.io.File;


/**
 * This class is used for pdf viewer screen.
 * Created by <b>Harshal Benake</b> on 06/11/15.
 * Link:- http://stackoverflow.com/a/19180918
 */
public class MainActivity extends Activity {
    String urlPdf="https://commonsware.com/Android/Android-1_0-CC.pdf";
    String strGoogleDocs = "https://docs.google.com/gview?embedded=true&url=";
    String mainUrl="http://172.24.11.50/CAST/Docs/381/IM/381_Application_126_04122015114525.pdf";
    private ImageView imageView;
    private int currentPage = 0;
    private Button next, previous;

    //Globals:
    private WebView wv;
    private int ViewSize = 0;


    /**
     * Hack because of a bug in PDFview; It crashes when you load a second PDF
     */
    private void reinitPdfView() {



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//Settings
        PDFImage.sShowImages = true; // show images
        PDFPaint.s_doAntiAlias = true; // make text smooth
        HardReference.sKeepCaches = true; // save images in cache

        //Setup webview
        wv = (WebView)findViewById(R.id.webView1);
        wv.getSettings().setBuiltInZoomControls(true);//show zoom buttons
        wv.getSettings().setSupportZoom(true);//allow zoom
        //get the width of the webview
        wv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                ViewSize = wv.getWidth();
                wv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });

        pdfLoadImages();//load images

      /*  File file = new File("/sdcard/sample.pdf");

        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(),
                        "No Application Available to View PDF",
                        Toast.LENGTH_SHORT).show();
            }

        }
*/

      /* final PDFView pdfView=(PDFView)findViewById(R.id.pdfview);

        new DownloadFile(MainActivity.this).execute(urlPdf, "casPDF.pdf");


        final File pdfFile = new File(Environment.getExternalStorageDirectory() + "/cas/" + "casPDF.pdf");  // -> filename = maven.pdf
        File pdfFileURL= new File(Uri.parse(urlPdf).toString());
        if(pdfFile.exists()) {
          runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  pdfView.fromFile(pdfFile)
//                .pages(0, 2, 1, 3, 3, 3)
                          .defaultPage(1)
                          .showMinimap(false)
                          .enableSwipe(true)
              *//*  .onDraw(onDrawListener)
                .onLoad(onLoadCompleteListener)
                .onPageChange(onPageChangeListener)*//*
                          .load();
              }
          });
        }*/


       /* new DownloadFile(MainActivity.this).execute(urlPdf, "Android-1_0-CC.pdf");

        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File pdfFile = new File(Environment.getExternalStorageDirectory() + "/cas/" + "abc.pdf");  // -> filename = maven.pdf
                Uri path = Uri.fromFile(pdfFile);
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(pdfIntent);
            }
        });*/


       /* next = (Button) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage++;
                render();
            }
        });
        previous = (Button) findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPage--;
                render();
            }
        });
        render();*/
    }

    private void pdfLoadImages()
    {
        try
        {
            // run async
            new AsyncTask<Void, Void, Void>()
            {
                // create and show a progress dialog
                ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", "Opening...");

                @Override
                protected void onPostExecute(Void result)
                {
                    //after async close progress dialog
                    progressDialog.dismiss();
                }

                @Override
                protected Void doInBackground(Void... params)
                {
                    try
                    {
                        // select a document and get bytes
                        File file = new File(Environment.getExternalStorageDirectory().getPath()+"/CAS.pdf");
                        RandomAccessFile raf = new RandomAccessFile(file, "r");
                        FileChannel channel = raf.getChannel();
                        ByteBuffer bb = ByteBuffer.NEW(channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size()));
                        raf.close();
                        // create a pdf doc
                        PDFFile pdf = new PDFFile(bb);
                        //Get the first page from the pdf doc
                        PDFPage PDFpage = pdf.getPage(1, true);
                        //create a scaling value according to the WebView Width
                        final float scale = ViewSize / PDFpage.getWidth() * 0.95f;
                        //convert the page into a bitmap with a scaling value
                        Bitmap page = PDFpage.getImage((int)(PDFpage.getWidth() * scale), (int)(PDFpage.getHeight() * scale), null, true, true);
                        //save the bitmap to a byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        page.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        stream.close();
                        byte[] byteArray = stream.toByteArray();
                        //convert the byte array to a base64 string
                        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        //create the html + add the first image to the html
                        String html = "<!DOCTYPE html><html><body bgcolor=\"#7f7f7f\"><img src=\"data:image/png;base64,"+base64+"\" hspace=10 vspace=10><br>";
                        //loop through the rest of the pages and repeat the above
                        for(int i = 2; i <= pdf.getNumPages(); i++)
                        {
                            PDFpage = pdf.getPage(i, true);
                            page = PDFpage.getImage((int)(PDFpage.getWidth() * scale), (int)(PDFpage.getHeight() * scale), null, true, true);
                            stream = new ByteArrayOutputStream();
                            page.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            stream.close();
                            byteArray = stream.toByteArray();
                            base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            html += "<img src=\"data:image/png;base64,"+base64+"\" hspace=10 vspace=10><br>";
                        }
                        html += "</body></html>";
                        //load the html in the webview
                        final String finalHtml = html;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wv.loadDataWithBaseURL("", finalHtml, "text/html","UTF-8", "");
                            }
                        });
                    }
                    catch (Exception e)
                    {
                        Log.d("CounterA", e.toString());
                    }
                    return null;
                }
            }.execute();
            System.gc();// run GC
        }
        catch (Exception e)
        {
            Log.d("error", e.toString());
        }
    }

  /*  private void render() {
        try {
            imageView = (ImageView) findViewById(R.id.imageView);
            int REQ_WIDTH = 1;
            int REQ_HEIGHT = 1;
            REQ_WIDTH = imageView.getWidth();
            REQ_HEIGHT = imageView.getHeight();

            Bitmap bitmap = Bitmap.createBitmap(REQ_WIDTH, REQ_HEIGHT, Bitmap.Config.ARGB_4444);
            File file = new File("/sdcard/Download/test.pdf");
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

            if (currentPage < 0) {
                currentPage = 0;
            } else if (currentPage > renderer.getPageCount()) {
                currentPage = renderer.getPageCount() - 1;
            }

            Matrix m = imageView.getImageMatrix();
            Rect rect = new Rect(0, 0, REQ_WIDTH, REQ_HEIGHT);
            renderer.openPage(currentPage).render(bitmap, rect, m, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            imageView.setImageMatrix(m);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}

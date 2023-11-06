package com.example.whatsappshareapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PDF_URL = "https://comzent.in/comzentapp/uploads/pro/10/BusinesCRMnew.pdf" ;
    private static final String PDF_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    private static final String PDF_FILENAME = "comzent_sample-9.pdf";
    Button shareButton,imageButton,videoButton,pdfButton,downloadButton;
    Uri uri;
    EditText editText;
    TextView textView;
    int flag = 0;
    private File pdfFile = new File(PDF_DIRECTORY,PDF_FILENAME);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("Directory: "+PDF_DIRECTORY);

        shareButton=findViewById(R.id.shareButton);
        imageButton=findViewById(R.id.imageButton);
        videoButton=findViewById(R.id.videoButton);
        pdfButton=findViewById(R.id.pdfButton);
        editText=findViewById(R.id.editText);
        textView=findViewById(R.id.textView);
        downloadButton=findViewById(R.id.downloadButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=1;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2595);
            }
        });

        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=2;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, 2596);
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=3;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 2597);
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfFile.exists()) {
                    Uri uri = MyFileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", pdfFile);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("application/pdf");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.setPackage("com.whatsapp");
                    try {
                        startActivity(shareIntent);
                        Log.d("Share", "Sharing PDF to WhatsApp");
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        Log.e("ShareError", "WhatsApp not installed");
                    }
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DownloadFile.downloadFile(PDF_URL,PDF_FILENAME,getApplicationContext(),PDF_DIRECTORY);
                        }
                    }).start();
                }

            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editText.getText().toString();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setPackage("com.whatsapp");
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if(flag==1) {
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                }else if(flag == 2){
                    ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("label", message);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "Message copied, paste before sending.", Toast.LENGTH_SHORT).show();
                    shareIntent.setType("application/pdf");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(Intent.createChooser(shareIntent, "Share PDF"));

                }else if(flag==3){
                    shareIntent.setType("video/*");
                    startActivity(Intent.createChooser(shareIntent, "Share VIDEO"));
                }else{
                    Toast.makeText(MainActivity.this, "Select File First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2595 && resultCode == RESULT_OK) {
            uri=data.getData();
            textView.setText("IMAGE Selected");
        }else if(requestCode == 2596 && resultCode == RESULT_OK){
            uri=data.getData();
            textView.setText("PDF Selected");
        }else if(requestCode == 2597 && resultCode == RESULT_OK){
            uri=data.getData();
            textView.setText("VIDEO Selected");
        }
    }
}
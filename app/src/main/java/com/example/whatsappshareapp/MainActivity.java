package com.example.whatsappshareapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    Button shareButton,imageButton,videoButton,pdfButton;
    Uri uri;
    EditText editText;
    TextView textView;
    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareButton=findViewById(R.id.shareButton);
        imageButton=findViewById(R.id.imageButton);
        videoButton=findViewById(R.id.videoButton);
        pdfButton=findViewById(R.id.pdfButton);
        editText=findViewById(R.id.editText);
        textView=findViewById(R.id.textView);

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
package com.example.whatsappshareapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadFile {
    public static void downloadFile(String url, String fileName, Context applicationContext, String pdfDirectory) {
        try {
            URL fileURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) fileURL.openConnection();
            connection.connect();

            File downloadFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            FileOutputStream outputStream = new FileOutputStream(downloadFile);

            InputStream inputStream = connection.getInputStream();
            long totalSize = connection.getContentLength();
            byte[] buffer = new byte[1024];
            int bytesRead;
            long downloadedSize = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                downloadedSize += bytesRead;
            }
            inputStream.close();
            outputStream.close();
            shareToWhatsapp(applicationContext,pdfDirectory,fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Invalid Url");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Download Failed");
        }
    }

    private static void shareToWhatsapp(Context context,String PDF_DIRECTORY,String PDF_FILENAME) {
        File pdfFile = new File(PDF_DIRECTORY,PDF_FILENAME);
        Uri uri = MyFileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setPackage("com.whatsapp");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(shareIntent);
            Log.d("Share", "Sharing PDF to WhatsApp");
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Log.e("ShareError", "WhatsApp not installed");
        }
    }
}

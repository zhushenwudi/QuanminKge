package com.ilab.quanminkge;

import android.app.Notification;
import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SDCardListener extends FileObserver {
    private String pathDir;
    private Notification notification;
    private Context context;

    SDCardListener(Context context, String path, Notification notification) {
        super(path);
        this.context = context;
        pathDir = path;
        this.notification = notification;
    }

    @Override
    public void onEvent(int event, String path) {
        if (event == FileObserver.CREATE) {
            appendFile(pathDir + "temp.txt", path + "\n");
        }
    }

    private void appendFile(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                NotificationManagerCompat.from(context).notify(1, notification);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
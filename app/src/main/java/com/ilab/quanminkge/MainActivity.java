package com.ilab.quanminkge;

import android.app.Notification;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ilab.quanminkge.AppUtils.copyFile;
import static com.ilab.quanminkge.AppUtils.createNotificationChannel;
import static com.ilab.quanminkge.AppUtils.toArrayByFileReader;

public class MainActivity extends AppCompatActivity {
    private SDCardListener listener;
    private List<File> list = new ArrayList<>();
    private Notification notification;
    private String ExternalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String appPath = ExternalStorageDirectory + "/Android/data/com.tencent.karaoke/files/opus/";
    private String saveDir = "mySong";//保存的位置为sdcard根目录，这里需要指定保存的目录名

    //插入曲目
    public void btn_insertList(View view) {
        initNotification("插入成功");
        try {
            File[] files = new File(appPath).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
            File file = new File(appPath + "temp.txt");
            file.createNewFile();
            listener = new SDCardListener(getApplicationContext(), appPath, notification);
            listener.startWatching();
            myToast("前往app点击歌曲加入心仪清单吧");
        } catch (Exception e) {
            e.printStackTrace();
            myToast("未知错误");
        }
    }

    //保存曲目
    public void btn_download(View view) {
        initNotification("保存成功");
        File[] files = new File(appPath).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.length() / (1024 * 1024) > 1) {
                    file.delete();
                }
            }
        }
        String[] array = toArrayByFileReader(appPath + "temp.txt");
        for (String str : array) {
            list.add(new File(appPath + "tmp_cache/" + str));
        }
        while (true) {
            boolean is = false;
            File currentFile = null;
            for (File f : list) {
                if (f.exists()) {
                    is = copyFile(f, ExternalStorageDirectory + "/" + saveDir + "/" + f.getName() + ".mp3");
                    if (is) {
                        currentFile = f;
                    }
                }
            }
            if (is) {
                list.remove(currentFile);
                break;
            }
        }
        NotificationManagerCompat.from(getApplicationContext()).notify(1, notification);
    }

    private void initNotification(String msg) {
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext(), createNotificationChannel(this));
        notification = notificationCompatBuilder
                // Title for API <16 (4.0 and below) devices.
                .setContentTitle(msg)
                // Content for API <24 (7.0 and below) devices.
                //.setContentText("内容")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setCategory(Notification.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listener.stopWatching();
    }

    private void myToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}

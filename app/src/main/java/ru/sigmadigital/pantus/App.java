package ru.sigmadigital.pantus;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class App extends Application {

    private static App context;
    private static String filesPath = "/data/data/ru.sigmadigital.pantus/files";

    public static String getFilesPath() {
        return filesPath;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        filesPath = getFilesDir().getPath();
        File mdir = new File(filesPath + "/models/");
        mdir.mkdirs();
        copyAssets();
    }

    public static Context getAppContext() {
        return context;
    }


    private void copyAssets() {
        File dir = new File(filesPath + "/copy/");
        dir.mkdirs();
        try {
            String[] names = getAssets().list("");
            System.out.println(new Gson().toJson(names));
            for (String name : names) {
                if (!name.contains(".")) {
                    continue;
                }
                InputStream is = getAssets().open(name);

                File f = new File(filesPath + "/copy/" + name);
                if (f.exists()) {
                    f.delete();
                }
                OutputStream os = new FileOutputStream(filesPath + "/copy/" + name);
                byte[] buf = new byte[1024];
                int r;
                while ((r = is.read(buf)) > -1) {
                    os.write(buf, 0, r);
                }
                os.flush();
                os.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
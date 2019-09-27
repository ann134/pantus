package ru.sigmadigital.pantus.api.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ru.sigmadigital.pantus.api.BaseResponse;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.api.Sender;
import ru.sigmadigital.pantus.util.HashUtil;

public class GetModelTask extends AsyncTask<Void, Integer, Integer> {

    private Context context;
    private String id;
    private OnGetModelListener listener;

    public String getId() {
        return id;
    }

    public GetModelTask(Context context, String id, OnGetModelListener listener) {
        this.context = context;
        this.id = id;
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Void... voids) {


        BaseResponse checkResp = Sender.sendGet(Network.BASE_URL + "/api/items/" + id + "/model/md5", new HashMap<String, String>());
        File fmd5 = new File(context.getFilesDir() + "/models/" + id + ".md5");

        String md5 = readFile(fmd5);

        if (checkResp.getCode() != 200 || (checkResp.getCode() == 200)) {
            String md5Server = new Gson().fromJson(checkResp.getData(), String.class);
            if (md5.toLowerCase().equals(md5Server.toLowerCase())) {
                return (0);
            }
        }

        try {
            publishProgress(1);
            Network.disavleSSLValidation();
            ByteArrayOutputStream resp = Sender.loadData(Network.BASE_URL + "/api/items/" + id + "/model", new HashMap<String, String>());
            File chacheZip = new File(context.getFilesDir() + "/" + System.currentTimeMillis() + ".zip");
            OutputStream os = new FileOutputStream(chacheZip);
            os.write(resp.toByteArray());
            os.flush();
            os.close();


            File cache = new File(context.getFilesDir() + "/models/" + id + "/");
            if (cache.exists()) {
                deleteFile(cache);
            }

            cache.mkdirs();
            ZipFile zipFile = new ZipFile(chacheZip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File f = new File(cache.getAbsolutePath() + "/" + entry.getName());
                Log.e("App", entry.getName());
                if (entry.isDirectory()) {
                    f.mkdirs();
                } else {

                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(f);
                    byte[] bytes = new byte[1024];
                    int length;
                    while ((length = is.read(bytes)) >= 0) {
                        fos.write(bytes, 0, length);
                    }
                    is.close();
                    fos.close();
                }
            }

            /*сохраним мд5*/
            if (fmd5.exists()) {
                fmd5.delete();
            }
            md5 = HashUtil.md5(chacheZip);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fmd5)));
            br.write(md5);
            br.flush();
            br.close();
            /****/
            chacheZip.delete();
            return 0;
        } catch (Exception e) {
            Log.e("App", "Error", e);
        }
        Log.e("App", "Get null model");
        return 0;
    }

    private boolean deleteFile(File dir) {
        if (dir.isFile()) {
            return dir.delete();
        } else if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                } else {
                    deleteFile(f);;
                }
            }
            return dir.delete();
        }
        return true;

    }


    private String readFile(File file) {
        try {
            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str;
            StringBuffer sb = new StringBuffer();
            while ((str = br.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(str);
            }
            is.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void onPostExecute(Integer count) {
        super.onPostExecute(count);
        if (listener != null) {
            listener.onGetModel();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (listener != null && values[0] == 1) {
            listener.onNeedLoadModelFromNetwork();
        }
    }




    public interface OnGetModelListener {
        void onGetModel();

        void onNeedLoadModelFromNetwork();
    }
}

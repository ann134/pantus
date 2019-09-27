package ru.sigmadigital.pantus.api.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.api.Sender;

public class GetSignaturesImagesTask extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private OnSignaqturesImagesLoadListener listener;
    private List<String> signatures;

    public GetSignaturesImagesTask(Context context, OnSignaqturesImagesLoadListener listener, List<String> signatures) {
        this.context = context;
        this.listener = listener;
        this.signatures = signatures;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File imgsDir = new File(context.getFilesDir() + "/imgs/");
        if(!imgsDir.exists()){
            imgsDir.mkdirs();
        }
        String[] imgs = new File(context.getFilesDir() + "/imgs/").list();
        int pos = 0;
        for (String sig : signatures) {
            boolean needLoad = true;
         /*   for (String img : imgs) {
                String i = img;
                if (i.contains(".")) {
                    i = i.substring(0, i.indexOf("."));
                }
                if (i.equals(sig)) {
                    if()
                    needLoad = false;
                    break;
                }
            }*/
            if (needLoad) {
                Network.disavleSSLValidation();
                Map<String, String> headers = new HashMap<>();

                ByteArrayOutputStream resp = Sender.loadData(Network.BASE_URL + "/api/items/" + sig + "/signature", headers);
                if (resp.size() > 0) {
                    File f = new File(context.getFilesDir() + "/imgs/" + sig + ".jpeg");
                    if (f.exists()) {
                        f.delete();
                    }
                    try {
                        OutputStream os = new FileOutputStream(f);
                        os.write(resp.toByteArray());
                        os.flush();
                        os.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            publishProgress(pos);
            pos++;
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (listener != null) {
            String sig = signatures.get(values[0]);
            listener.onLoadSignature(sig, values[0], signatures.size());
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (listener != null) {
            listener.onLoadAllSignatures();
        }
    }

    public interface OnSignaqturesImagesLoadListener {
        void onLoadSignature(String name, int pos, int count);

        void onLoadAllSignatures();
    }
}

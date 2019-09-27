package ru.sigmadigital.pantus.api.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.ar.rendering.ObjLoader;
import ru.sigmadigital.pantus.ar.rendering.ObjModel;
import ru.sigmadigital.pantus.ar.rendering.RenderModel;

public class LoadModelTask extends AsyncTask<Void, Void, RenderModel> {


    private String id;
    private int pos;
    private int subPos;
    private OnLoadModelListener listener;

    public LoadModelTask(String id, int pos, int subPos, OnLoadModelListener listener) {
        this.id = id;
        this.pos = pos;
        this.subPos = subPos;
        this.listener = listener;
    }


    @Override
    protected RenderModel doInBackground(Void... voids) {
        try {
            File file;
            if (subPos > -1) {
                Log.e("App","pos="+pos+" sPos="+ subPos);
                String[] subPosNames = new File(App.getFilesPath() + "/models/" + id+"/").list();
                String[] names = new File(App.getFilesPath() + "/models/" + id + "/" + subPosNames[subPos]+"/").list();
                int i = 0;
                String name = "";
                for (String nm : names) {
                    Log.e("App", nm);
                    if (nm.toLowerCase().contains(".obj")) {

                        Log.e("App", "find obj");
                        if (i == pos) {
                            name = nm;
                            break;
                        }
                        i++;
                    }
                }
                file = new File(App.getFilesPath() + "/models/" + id + "/" + subPosNames[subPos] + "/" + name);
                Log.e("File", file.toString());
            } else {
                String[] names = new File(App.getFilesPath() + "/models/" + id+"/").list();
                int i = 0;
                String name = "";
                for (String nm : names) {
                    if (nm.toLowerCase().endsWith(".obj")) {

                        if (i == pos) {
                            name = nm;
                            break;
                        }
                        i++;
                    }
                }
                file = new File(App.getFilesPath() + "/models/" + id + "/" + name);
            }
            ObjModel objModel = ObjLoader.loadObject(file.getAbsolutePath());
           // ObjModel objModel = ObjLoader.loadObject(file.getAbsolutePath());
            RenderModel renderModel = new RenderModel(objModel);
            return renderModel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(RenderModel model) {
        super.onPostExecute(model);
        if (listener != null) {
            listener.onLoadModel(model);
        }
    }

    public interface OnLoadModelListener {
        void onLoadModel(RenderModel model);
    }
}

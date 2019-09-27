package ru.sigmadigital.pantus.ar.rendering;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigCreator {

    private ConfigCreator() {
    }

    public static String create(Context context){
        String[] imgs = new File(context.getFilesDir()+"/imgs").list();
        List<ImageItem> items = new ArrayList<>();
        for(String img : imgs){
            ImageItem it = new ImageItem();
            it.name = img.substring(0, img.indexOf("."));
            it.image = "files/imgs/"+img;
            items.add(it);
        }
        Config config = new Config();
        config.images = items;
        return new Gson().toJson(config);
    }


    private  static  class ImageItem{
        String name;
        String image;
        //double[] size = {8.56, 5.4};
    }

    private  static  class Config{
        List<ImageItem> images;
    }

}

package ru.sigmadigital.pantus.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GsonHelper {

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new DateDeserializer());
        //builder.setDateFormat("MMM d, yyyy hh:mm:ss a");
        try {


        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.create();
        //2019-01-09T00:25:53.000+0000
    }

    static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();

            SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy, hh:mm:ss a", Locale.ENGLISH);
            //format.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                return format.parse(date);
            } catch (ParseException exp) {
                System.out.println("Failed to parse Date: "+date+" as" + format.toPattern());
                exp.printStackTrace();
                return null;
            }
        }
    }
}

package ru.sigmadigital.pantus.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsResponse implements Serializable {

    private long id;
    private String label;
    private String text;
    private Date date;
    private String link;

    public String getLink() {
        return link;
    }

    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLink(String link) {
        this.link = link;
    }



    public String getNewsDate() {
        if(date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            return dateFormat.format(date);
        }else{
            return "";
        }
    }


}

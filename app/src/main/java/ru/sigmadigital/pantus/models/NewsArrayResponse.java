package ru.sigmadigital.pantus.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsArrayResponse implements Serializable {

    private int allCount = 0;
    private List<NewsResponse> news = new ArrayList<>();

    public int getAllCount() {
        return allCount;
    }

    public List<NewsResponse> getNews() {
        return news;
    }

    public void update(NewsArrayResponse newNews) {
        allCount = newNews.getAllCount();
        news.addAll(newNews.getNews());
    }

}


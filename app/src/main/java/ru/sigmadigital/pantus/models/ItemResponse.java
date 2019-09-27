/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.pantus.models;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class ItemResponse implements Serializable {

    private long id;
    private String name;
    private double price;
    private int countAtStock;
    private String brand;
    private String article;
    private String oem;
    private String description;
    private String code;
    private String video;
    private String link;
    private String applicability;
    private String weight;
    private String itemTypes;
    private String country;
    private CategoryResponse category;
    private List<ItemTextResponse> texts = new ArrayList<>();

    public List<ItemTextResponse> getTexts() {
        return texts;
    }

    public void setTexts(List<ItemTextResponse> texts) {
        this.texts = texts;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return formatPrice(price);
    }

    public int getCountAtStock() {
        return countAtStock;
    }

    public String getBrand() {
        return brand;
    }

    public String getArticle() {
        return article;
    }

    public String getOem() {
        return oem;
    }

    public String getDescription() {
        return description;
    }

    public String getItemTypes() {
        return itemTypes;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public String getCode() {
        return code;
    }

    public String getVideo() {
        return video;
    }

    public String getLink() {
        return link;
    }

    public String getApplicability() {
        return applicability;
    }

    public String getWeight() {
        return weight;
    }

    public String getCountry() {
        return country;
    }

    public String getCategoryName() {
        if (category != null)
            return category.getName();
        return "_";
    }

    private static String formatPrice(double i) {
        DecimalFormat df = new DecimalFormat("###,###.00");
        DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
        dfs.setGroupingSeparator(' ');
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        return df.format(i);
    }

}

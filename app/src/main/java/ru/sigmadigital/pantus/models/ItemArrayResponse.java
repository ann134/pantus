/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.pantus.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ItemArrayResponse implements Serializable {

    private long allCount;
    private List<ItemResponse> items = new ArrayList<>();

    public long getAllCount() {
        return allCount;
    }

    public List<ItemResponse> getItems() {
        return items;
    }

    public void update(ItemArrayResponse itemsNew) {
        allCount = itemsNew.getAllCount();
        items.addAll(itemsNew.getItems());
    }

    public void setItems(List<ItemResponse> items) {
        this.items = items;
    }
}

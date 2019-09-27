/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.pantus.models;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CategoryArrayResponse implements Serializable {

    private long allCount;
    private List<CategoryResponse> categories;

    public CategoryArrayResponse (List<CategoryResponse> categories) {
        this.categories = categories;
        allCount = categories.size();
    }

    public long getAllCount() {
        return allCount;
    }

    public List<CategoryResponse> getCategories() {
        if (categories != null) {
            return categories;
        } else {
            return new ArrayList<>();
        }
    }

    public void sortCategories() {
        if (categories != null) {
            Collections.sort(categories, new Comparator<CategoryResponse>() {
                @Override
                public int compare(CategoryResponse o1, CategoryResponse o2) {
                    if (o1.getPriority() == o2.getPriority()) {
                        return 0;
                    }
                    return o1.getPriority() > o2.getPriority() ? 1 : -1;
                }
            });
        }
    }
}

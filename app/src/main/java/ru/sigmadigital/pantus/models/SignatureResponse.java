/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.pantus.models;

import com.google.gson.Gson;

/**
 *
 * @author dima
 */
public class SignatureResponse {

    private long id;
    private long size;
    private String md5;

    public SignatureResponse(long id, long size, String md5) {
        this.id = id;
        this.size = size;
        this.md5 = md5;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}

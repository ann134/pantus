package ru.sigmadigital.pantus.api.asynctask;

import android.net.Uri;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.pantus.api.BaseResponse;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.api.Response;
import ru.sigmadigital.pantus.api.Sender;
import ru.sigmadigital.pantus.models.NewsArrayResponse;

public class NewsTask extends AsyncTask<Void, Void, NewsArrayResponse> {

    private int from;
    private int count;
    private OnGetNewsListener listener;

    public NewsTask(int from, int count, OnGetNewsListener listener) {
        this.count = count;
        this.from = from;
        this.listener = listener;
    }

    @Override
    protected NewsArrayResponse doInBackground(Void... voids) {
        Network.disavleSSLValidation();
        Map<String, String> headers = new HashMap<>();

        Uri params = new Uri.Builder()
                .appendQueryParameter("from", String.valueOf(from))
                .appendQueryParameter("count", String.valueOf(count))
                .build();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "/api/news" + params.toString(), headers);
        if (resp != null && resp.getCode() == 200) {
            return Response.fromJson(resp.getData(), NewsArrayResponse.class);
        } else {

        }
        return null;
    }

    @Override
    protected void onPostExecute(NewsArrayResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onGetNews(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetNewsListener {
        void onGetNews(NewsArrayResponse news);
    }
}

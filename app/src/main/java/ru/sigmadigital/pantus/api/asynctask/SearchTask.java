package ru.sigmadigital.pantus.api.asynctask;

import android.net.Uri;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.pantus.api.BaseResponse;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.api.Response;
import ru.sigmadigital.pantus.api.Sender;
import ru.sigmadigital.pantus.models.ItemArrayResponse;

public class SearchTask extends AsyncTask<Void, Void, ItemArrayResponse> {

    private int from;
    private int count;
    private String query;
    private OnGetItemsListener listener;

    public SearchTask(int from, int count, String query, OnGetItemsListener listener) {
        this.count = count;
        this.from = from;
        this.query = query;
        this.listener = listener;
    }

    @Override
    protected ItemArrayResponse doInBackground(Void... voids) {
        Network.disavleSSLValidation();
        Map<String, String> headers = new HashMap<>();

        Uri params = new Uri.Builder()
                .appendQueryParameter("from", String.valueOf(from))
                .appendQueryParameter("count", String.valueOf(count))
                .appendQueryParameter("query", query)
                .build();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "/api/items/search" + params.toString(), headers);
        if (resp != null && resp.getCode() == 200) {
            return Response.fromJson(resp.getData(), ItemArrayResponse.class);
        } else {

        }
        return null;
    }

    @Override
    protected void onPostExecute(ItemArrayResponse baseResponse) {
        super.onPostExecute(baseResponse);
        if (listener != null && !isCancelled()) {
            listener.onGetItems(baseResponse);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetItemsListener {
        void onGetItems(ItemArrayResponse itemArrayResponse);
    }
}


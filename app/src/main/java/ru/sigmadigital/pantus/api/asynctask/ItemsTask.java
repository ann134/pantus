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

public class ItemsTask extends AsyncTask<Void, Void, ItemArrayResponse> {

    private int from;
    private int count;
    private long categoryId;

    private OnGetItemsListener listener;

    public ItemsTask(int from, int count, long categoryId, OnGetItemsListener listener) {
        this.from = from;
        this.count = count;
        this.categoryId = categoryId;
        this.listener = listener;
    }

    @Override
    protected ItemArrayResponse doInBackground(Void... voids) {
        Network.disavleSSLValidation();
        Map<String, String> headers = new HashMap<>();

        Uri params = new Uri.Builder()
                .appendQueryParameter("from", String.valueOf(from))
                .appendQueryParameter("count", String.valueOf(count))
                .appendQueryParameter("category", String.valueOf(categoryId))
                .build();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "/api/items" + params.toString(), headers);
        if (resp != null && resp.getCode() == 200) {
            return Response.fromJson(resp.getData(), ItemArrayResponse.class);
        } else {

        }
        return null;
    }

    @Override
    protected void onPostExecute(ItemArrayResponse itemArrayResponse) {
        super.onPostExecute(itemArrayResponse);
        listener.onGetItems(itemArrayResponse);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetItemsListener {
        void onGetItems(ItemArrayResponse itemArrayResponse);
    }
}

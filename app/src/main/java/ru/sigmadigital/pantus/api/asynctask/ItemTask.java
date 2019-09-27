package ru.sigmadigital.pantus.api.asynctask;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.pantus.api.BaseResponse;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.api.Response;
import ru.sigmadigital.pantus.api.Sender;
import ru.sigmadigital.pantus.models.ItemResponse;

public class ItemTask extends AsyncTask<Void, Void, ItemResponse> {

    private long id;

    private OnGetItemListener listener;

    public ItemTask(long id, OnGetItemListener listener) {
        this.id = id;
        this.listener = listener;
    }

    @Override
    protected ItemResponse doInBackground(Void... voids) {
        Network.disavleSSLValidation();
        Map<String, String> headers = new HashMap<>();
        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "/api/items/" + id, headers);
        if (resp != null && resp.getCode() == 200) {
            return Response.fromJson(resp.getData(), ItemResponse.class);
        } else {

        }
        return null;
    }

    @Override
    protected void onPostExecute(ItemResponse item) {
        if (listener != null) {
            listener.onGetItem(item);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetItemListener {
        void onGetItem(ItemResponse itemResponse);
    }
}

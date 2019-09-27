package ru.sigmadigital.pantus.api.asynctask;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import ru.sigmadigital.pantus.api.BaseResponse;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.api.Response;
import ru.sigmadigital.pantus.api.Sender;
import ru.sigmadigital.pantus.models.CategoryArrayResponse;

public class CategoriesTask extends AsyncTask<Void,Void, CategoryArrayResponse> {

    private OnGetCategoriesResponseListener listener;

    public CategoriesTask(OnGetCategoriesResponseListener listener) {
        this.listener = listener;
    }

    @Override
    protected CategoryArrayResponse doInBackground(Void... voids) {
        Network.disavleSSLValidation();
        Map<String, String> headers = new HashMap<>();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "/api/categories", headers);
        if (resp != null && resp.getCode() == 200) {
            CategoryArrayResponse categories = Response.fromJson(resp.getData(), CategoryArrayResponse.class);
            if (categories != null) {
                categories.sortCategories();
            }
            return categories;
        } else {

        }
        return null;
    }

    @Override
    protected void onPostExecute(CategoryArrayResponse categoryArrayResponse) {
        super.onPostExecute(categoryArrayResponse);
        listener.onGetCategoriesResponse(categoryArrayResponse);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public interface OnGetCategoriesResponseListener{
        void onGetCategoriesResponse(CategoryArrayResponse categoryArrayResponse);
    }

}



package ru.sigmadigital.pantus.api.asynctask;

import android.os.AsyncTask;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sigmadigital.pantus.App;
import ru.sigmadigital.pantus.api.BaseResponse;
import ru.sigmadigital.pantus.api.Network;
import ru.sigmadigital.pantus.api.Sender;
import ru.sigmadigital.pantus.models.SignatureResponse;
import ru.sigmadigital.pantus.util.GsonHelper;
import ru.sigmadigital.pantus.util.HashUtil;

public class GetSignaturesTask extends AsyncTask<Void, Void, List<String>> {

    private OnGetSignaturesListener listener;

    public GetSignaturesTask(OnGetSignaturesListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        Network.disavleSSLValidation();
        Map<String, String> headers = new HashMap<>();

        BaseResponse resp = Sender.sendGet(Network.BASE_URL + "/api/items/signatures", headers);
        if (resp != null && resp.getCode() == 200) {
            Type listType = new TypeToken<List<SignatureResponse>>() {
            }.getType();
            List<SignatureResponse> signatures = GsonHelper.getGson().fromJson(resp.getData(), listType);

            List<String> needLoad = new ArrayList<>();
            for (SignatureResponse sr : signatures) {
                File file = new File(App.getFilesPath() + "/imgs/" + sr.getId() + ".jpeg");
                if (file.exists()) {
                    String md5 = HashUtil.md5(file);
                    if (!md5.equals(sr.getMd5())) {
                        needLoad.add("" + sr.getId());
                    }
                } else {
                    needLoad.add("" + sr.getId());
                }
            }
            return needLoad;
        } else {

        }
        return new ArrayList<>();
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        if (listener != null) {
            listener.onGetSignaturesList(strings);
        }
    }

    public interface OnGetSignaturesListener {
        void onGetSignaturesList(List<String> signatures);
    }
}

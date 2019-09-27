package ru.sigmadigital.pantus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import ru.sigmadigital.pantus.R;
import ru.sigmadigital.pantus.api.asynctask.GetSignaturesImagesTask;
import ru.sigmadigital.pantus.api.asynctask.GetSignaturesImagesTask.OnSignaqturesImagesLoadListener;
import ru.sigmadigital.pantus.api.asynctask.GetSignaturesTask;

public class SplashActivity extends AppCompatActivity implements GetSignaturesTask.OnGetSignaturesListener, OnSignaqturesImagesLoadListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GetSignaturesTask(this).execute();
    }

    @Override
    public void onGetSignaturesList(List<String> signatures) {
        new GetSignaturesImagesTask(this, this, signatures).execute();
    }

    @Override
    public void onLoadAllSignatures() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onLoadSignature(String name, int pos, int count) {
        Log.e("App","load "+name+" is " +pos+" of " + count);
    }
}

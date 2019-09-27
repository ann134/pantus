package ru.sigmadigital.pantus.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import ru.sigmadigital.pantus.R;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private static final String API_KEY = "AIzaSyCcF8v3AGd1DexvSn7sRVyazkxIz-2Wu5w";
    private static String VIDEO_ID = "b1RKaRgVFKk";

    private YouTubePlayerView player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if(getIntent().hasExtra("video_link")){
            VIDEO_ID = getVideoIdFromUrl(getIntent().getStringExtra("video_link"));
        }

        if (appInstalledOrNot("com.google.android.youtube")) {
            player = findViewById(R.id.youtube_player);

            player.initialize(API_KEY, this);
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getIntent().getStringExtra("video_link")));
            startActivity(intent);
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (youTubePlayer == null)
            return;

        // Start buffering
        if (!b) {
            youTubePlayer.setFullscreen(true);

            youTubePlayer.cueVideo(VIDEO_ID);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            String version = pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES).versionName;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < version.length(); i++) {
                if (version.charAt(i) != '.') {
                    sb.append(version.charAt(i));
                } else {
                    break;
                }
            }
            if (Integer.parseInt(sb.toString()) >= 13) {
                return true;
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private String getVideoIdFromUrl(String url){
        String[] strings =  url.replace("https://www.youtube.com/watch?v=", "").split("&");
        return strings[0];
    }
}

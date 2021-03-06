package com.example.mindrate.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.mindrate.activity.LogInActivity;
import com.example.mindrate.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * This class aims to upload questionnaires' answer to server running in background.
 * <p>
 * <p>
 * <br>Project: MindRate</br>
 * <br>Package: com.example.mindrate.service</br>
 * <br>Author: Ecko Tan</br>
 * <br>E-mail: eckotan@icloud.com</br>
 * <br>Created at 2017/2/13:22:11</br>
 * </p>
 */
public class UploadService extends IntentService {

    private static final String TAG = "UploadService";

    private static boolean isBound;

    public UploadService() {
        super("uploadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // get answer from intent
        String answer = intent.getStringExtra("questionnaireAnswer");
        // upload
        HttpUtil.post(LogInActivity.SERVER + "/receive_answer/", answer, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.w(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(enabled);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

}

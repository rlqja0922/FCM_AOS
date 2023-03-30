package com.example.fcmtest;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.fcmtest.retrofit.RetrofitInterface;
import com.example.fcmtest.retrofit.TestData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LongPolling extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_long_polling);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call();
            }
        });
    }
    private void fcm(){
//        Intent fcm = new Intent(LongPolling.this, MyFirebaseMessagingService.class);
//        if (Build.VERSION.SDK_INT >= 26) {
//            LongPolling.this.startForegroundService(fcm);
//        }
//        else {
//            LongPolling.this.startService(fcm);
//        }
    }
    private void call(){
        Test();
    }
    private void Test(){
        String ipStr = "dev.rebornsoft.co.kr:21903";
        Log.d(TAG, "TestData: " + ipStr);
        try {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + ipStr)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

            TestData testData = new TestData();
            Call<TestData> call = retrofitInterface.gettest(testData);
            call.enqueue(new Callback<TestData>() {
                @Override
                public void onResponse(Call<TestData> call, Response<TestData> response) {
                    if (response.isSuccessful()) {
                        TestData testData1 = response.body();
                        Boolean status = testData1.getStatus();
                        Integer time = testData1.getTime();
                        Log.d(TAG, String.valueOf(time)+ "초");
                            if (!status){
                                call();
                            }else if (status){
                                Toast.makeText(LongPolling.this,time+"초",Toast.LENGTH_SHORT).show();
                            }

                        }
                }

                @Override
                public void onFailure(Call<TestData> call, Throwable t) {
                    Log.d(TAG,"에러메세지"+t.getMessage());
                    t.getMessage();
                    if(t.getMessage() == "timeout"){
                        Log.d(TAG, "onFailure: timeout Long-Polling");
//                        call();
                    }else {

                    }
                    }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onStop(){
        super.onStop();

        fcm();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
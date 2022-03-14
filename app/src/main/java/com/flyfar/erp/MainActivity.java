package com.flyfar.erp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {

    String autoLogin = "https://erp.flyfar.tech/Api/user.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("HardwareIds")
                String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                String userApi = autoLogin + "deviceId="+android_id;
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        userApi,
                        null,
                        response -> {

                            JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();

                            try {
                                if(jsonObject.get("empId").getAsString().toString() == "0" &&
                                        jsonObject.get("name").getAsString().toString() != "0" &&
                                        jsonObject.get("email").getAsString().toString() != "0" ){

                                    Intent c = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(c);
                                    finish();


                                }else {
                                    String DempId = jsonObject.get("empId").getAsString().toString();
                                    String Dname = jsonObject.get("name").getAsString().toString();
                                    String Demail = jsonObject.get("email").getAsString().toString();

                                    Intent b = new Intent(MainActivity.this, Attendance.class);
                                    b.putExtra("empId", DempId); //Your id
                                    b.putExtra("empEmail", Demail); //Your email
                                    b.putExtra("empName", Dname); //Your name
                                    startActivity(b);
                                    finish();

                                }

                            }catch(Exception ignored) {

                            }

                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {


                            }
                        }
                );
                requestQueue.add(objectRequest);
            }
        }, 2000);
    }
}

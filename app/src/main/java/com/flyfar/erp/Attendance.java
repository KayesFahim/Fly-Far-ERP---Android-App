package com.flyfar.erp;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Attendance extends AppCompatActivity {

    TextView time, date, Location, workinghoursIcon;
    ImageView CheckInBtn,CheckOutBtn;

    String baseUrl = "https://erp.flyfar.tech/Api/attendance.php?";
    String Emp_Id;
    String Comment;;
    String mcheckInTime= "No Data";
    String mcheckOutTime= "No Data";
    String WorkingHours = "Not Set";
    private ProgressBar progressBar;
    int i = 0;
    CountDownTimer cTimer = null;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Intent intent = getIntent();

        Emp_Id = intent.getStringExtra("empId");
        String Email = intent.getStringExtra("empEmail");
        String Name = intent.getStringExtra("empName");
        progressBar = findViewById(R.id.progress_bar);





        Location = findViewById(R.id.location);
        Location.setText(getWifiName(Attendance.this));

        CheckInBtn =  findViewById(R.id.checkIn);
        CheckInBtn.setVisibility(View.VISIBLE);
        CheckOutBtn =  findViewById(R.id.checkOut);
        CheckOutBtn.setVisibility(View.INVISIBLE);

        workinghoursIcon = findViewById(R.id.workinghoursIcon);
        workinghoursIcon.setText(WorkingHours);

        ImageView message = findViewById(R.id.message);
        message.setVisibility(View.INVISIBLE);





        time = findViewById(R.id.text2);
        date = findViewById(R.id.text3);
        TextView ckIn = findViewById(R.id.checkinclock);
        ckIn.setText(mcheckInTime);
        TextView ckOut = findViewById(R.id.checkoutclock);
        ckOut.setText(mcheckOutTime);


        String Localtime = new SimpleDateFormat("hh : mm a", Locale.getDefault()).format(Calendar.getInstance().getTime());
        time.setText(Localtime);


        LocalDateTime myDateObj = LocalDateTime.now();

        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("EEEE, MMM dd yyyy");

        String formattedDate = myDateObj.format(myFormatObj);
        date.setText(formattedDate);

        String Comment = " ";

        String apiURl = baseUrl + "empId=" + Emp_Id + "&check=true";

        RequestQueue requestQueue = Volley.newRequestQueue(Attendance.this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiURl,
                null,
                response -> {

                    JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();

                    if((jsonObject.get("checkinTime").getAsString() != null) && !(jsonObject.get("checkOutTime").getAsString().isEmpty())){

                        message.setVisibility(View.VISIBLE);
                        ckIn.setText(jsonObject.get("checkinTime").getAsString());
                        ckOut.setText(jsonObject.get("checkOutTime").getAsString());

                        CheckInBtn.setVisibility(View.INVISIBLE);

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormats
                                = new SimpleDateFormat("HH:mm");

                        // Parsing the Time Period
                        Date date1 = null;
                        try {
                            date1 = simpleDateFormats.parse(jsonObject.get("checkinTime").getAsString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date date2 = null;
                        try {
                            date2 = simpleDateFormats.parse(jsonObject.get("checkOutTime").getAsString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Calculating the difference in milliseconds
                        if (date1 !=null && date2 != null) {
                            long differenceInMilliSeconds
                                    = Math.abs(date2.getTime() - date1.getTime());

                            // Calculating the difference in Hours
                            long differenceInHours
                                    = (differenceInMilliSeconds / (60 * 60 * 1000))
                                    % 24;

                            // Calculating the difference in Minutes
                            long differenceInMinutes
                                    = (differenceInMilliSeconds / (60 * 1000)) % 60;

                            WorkingHours = differenceInHours + " H " + differenceInMinutes + " min";
                            workinghoursIcon.setText(differenceInHours + " H " + differenceInMinutes + " min");
                        }


                    }else if(jsonObject.get("checkinTime").getAsString() != null ){

                        ckIn.setText(jsonObject.get("checkinTime").getAsString());
                        CheckOutBtn.setVisibility(View.VISIBLE);


                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }
        );
        requestQueue.add(objectRequest);

        CheckInBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    progressBar.setVisibility(View.VISIBLE);
                    startTimer();
                    CheckInAction();

                }else{if(event.getAction() == MotionEvent.ACTION_UP){
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                    cancelTimer();
                }

                }
                return true;
            }

            private void cancelTimer() {
                if(cTimer!=null)
                    cTimer.cancel();
            }

            private void startTimer() {
                cTimer = new CountDownTimer(5000, 200) {
                    public void onTick(long millisUntilFinished) {
                        progressBar.setProgress(progressBar.getProgress() + 20);

                    }
                    public void onFinish() {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.INVISIBLE);
                        cancelTimer();
                    }
                };
                cTimer.start();

            }
        });


        CheckOutBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    progressBar.setVisibility(View.VISIBLE);
                    startTimer();
                    CheckOutAction();


                }else {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                    cancelTimer();
                }

                }
                return true;
            }

            private void cancelTimer() {
                if(cTimer!=null)
                    cTimer.cancel();
            }

            private void startTimer() {
                cTimer = new CountDownTimer(5000, 200) {
                    public void onTick(long millisUntilFinished) {
                        progressBar.setProgress(progressBar.getProgress() + 20);
                    }
                    public void onFinish() {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                };
                cTimer.start();


            }
        });





    }

    private void CheckInAction() {
        String apiURl = baseUrl +"checkin=true&comment=Late&empId="+Emp_Id;

        RequestQueue requestQueue = Volley.newRequestQueue(Attendance.this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiURl,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {

                        JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();

                        String Result = jsonObject.get("action").getAsString();
                        Handler handler=new Handler();
                        Runnable r=new Runnable() {
                            public void run() {
                                //what ever you do here will be done after 3 seconds delay.
                            }
                        };
                        handler.postDelayed(r, 3000);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Attendance.this);

                        // set title
                        alertDialogBuilder.setTitle("Check In Alert");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Check In Successfully Recorded")
                                .setCancelable(false)
                                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                    }
                                });


                        AlertDialog alertDialog = alertDialogBuilder.create();

                        alertDialog.show();
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

    private void CheckOutAction() {
        String apiURl = baseUrl +"checkout=true&comment=Late&empId="+Emp_Id;

        RequestQueue requestQueue = Volley.newRequestQueue(Attendance.this);

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiURl,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {

                        JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();

                        String Result = jsonObject.get("action").getAsString();

                        Handler handler=new Handler();
                        Runnable r=new Runnable() {
                            public void run() {
                                //what ever you do here will be done after 3 seconds delay.
                            }
                        };
                        handler.postDelayed(r, 3000);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Attendance.this);
                        alertDialogBuilder.setTitle("Check OUt Alert");
                        alertDialogBuilder
                                .setMessage("Check Out Successfully Recorded")
                                .setCancelable(false)
                                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);
                                    }
                                });


                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();


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


    public String getWifiName(Context context) {
        String ssid = "Your Location: Out Of Office";
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
            ssid = "Your Location: "+wifiInfo.getSSID();
        }
        return ssid;
    }

}
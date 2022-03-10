package com.flyfar.erp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText nameEdt, jobEdt;
    private Button postDataBtn;
    private TextView responseTV;
    private ProgressDialog progressDialog;
    String baseUrl = "https://erp.flyfar.tech/Api/login.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEdt = findViewById(R.id.userEmail);
        jobEdt = findViewById(R.id.userPassword);
        postDataBtn = findViewById(R.id.signin);
        responseTV = findViewById(R.id.response);




        // adding on click listener to our button.
        postDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = nameEdt.getText().toString();
                String password = jobEdt.getText().toString();

                String apiURl = baseUrl + "email=" + email + "&password=" + password;

                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Loading.....");
                progressDialog.show();
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.GET,
                        apiURl,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                responseTV.setText(response.toString());

                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(response.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                JSONObject getSth = null;
                                try {
                                    getSth = jsonObject.getJSONObject("user");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Object emp_id = null;
                                Object emailId = null;
                                Object name = null;

                                try {
                                    emp_id = getSth.get("EMP_ID");
                                    emailId = getSth.get("email");
                                    name = getSth.get("name");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String empId = emp_id.toString();
                                String empEmail =  emailId.toString();
                                String empName =  name.toString();


                                Intent b = new Intent(LoginActivity.this, Attendance.class);
                                b.putExtra("empId", empId); //Your id
                                b.putExtra("empEmail", empEmail); //Your email
                                b.putExtra("empName", empName); //Your name
                                startActivity(b);
                                finish();
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Rest Response", error.toString());

                            }
                        }
                );
                requestQueue.add(objectRequest);
                progressDialog.dismiss();

            }
        });
    }
}
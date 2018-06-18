package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ruvimbo on 25/10/2017.
 */

public class Login2 extends Fragment {

    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //private static final String LOGIN_URL = "https://hammerandtongues.com/webservice/login.php";
    //private static final String EWALL_URL = "https://hammerandtongues.com/webservice/getewalletbal.php";
    private static final String LOGIN_URL = "https://https://10.0.2.2:8012:8012/webservice/registerlogin.php";
    private static final String EWALL_URL = "https://https://10.0.2.2:8012:8012/webservice/getewalletbal.php";


    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_CATEGORIES = "posts";
    int success = 0;

    EditText username, password;
    String uname, pword;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.login, container, false);
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);



            username = (EditText) view.findViewById(R.id.username);
            password = (EditText) view.findViewById(R.id.password);
            Button signin = (Button) view.findViewById(R.id.btnlogin);
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    uname = username.getText().toString();
                    pword = password.getText().toString();
                    // new AttemptLogin().execute();

                }

            }

            );

        return view;
    }


    private void saveUser(final String username,final String password) {
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success", "" + s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int success = jsonObject.getInt("success");
                    if (success == 1) {
                        Toast.makeText(getContext(), (s), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), (s), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();
                volleyError.printStackTrace();
                Log.e("RUEERROR", "" + volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> values = new HashMap();
                values.put("username", username);
                values.put("password", password);


                return values;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
    }
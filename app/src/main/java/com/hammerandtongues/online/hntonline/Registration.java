package com.hammerandtongues.online.hntonline;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import preferences.MyPref;

public class Registration extends AppCompatActivity {

    // JSON parser class
    JSONParser jsonParser ;
    //testing on Emulator:
    //  private static final String LOGIN_URL = "https://hammerandtongues.com/webservice/register.php";

    private static final String LOGIN_URL = "https://shopping.hammerandtongues.com/webservice/register.php";
    private static final String CODE_URL = "https://shopping.hammerandtongues.com/webservice/sms-api.php";
    private static final String VERIFY_URL = "https://shopping.hammerandtongues.com/webservice/verify.php";
    private static final String EDIT_URL = "https://shopping.hammerandtongues.com/webservice/DeleteNum.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_POSTS = "posts";
    private ProgressDialog pDialog;

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Name = "nameKey";
    SharedPreferences sharedpreferences;
    LinearLayout pdetails, adetails, cdetails;
    Button submit,signin, editnum, resendcode;
    TextView textnocode;
    EditText txtusername, txtemail, txtpassword, txtfname, txtsname, txtpasswordcon, txttelno;
    EditText txtcode;
    String username, email, password, fname, sname, idno, telno, add1,add2,suburb, city, region, userid, code, mobilenum, selectcode, Pconfirm;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        System.gc();
        try {
            //View view = inflater.inflate(R.layout.register, container, false);

            Runtime.getRuntime().gc();

            super.onCreate(savedInstanceState);
            setContentView(R.layout.register);


            jsonParser = new JSONParser();
            System.gc();
            final SharedPreferences sharedpreferences = Registration.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

            pdetails = (LinearLayout) findViewById(R.id.personaldetails);
            adetails = (LinearLayout) findViewById(R.id.addressdetails);
            cdetails= (LinearLayout) findViewById(R.id.codedetails);



            txtfname = (EditText) findViewById(R.id.first_name);
            txtsname = (EditText) findViewById(R.id.surname);
            txtusername = (EditText) findViewById(R.id.username);
            txtemail = (EditText) findViewById(R.id.useremail);
            txtpasswordcon =(EditText) findViewById(R.id.password_confrim);
            txtpassword = (EditText) findViewById(R.id.password_login);
            txttelno = (EditText) findViewById(R.id.telephone);
            txtcode = (EditText) findViewById(R.id.code);
            editnum = (Button) findViewById(R.id.editnum);
            resendcode = (Button) findViewById(R.id.resendcode);
            textnocode = (TextView) findViewById(R.id.txtnocode);
            final Spinner spinner = (Spinner) findViewById(R.id.codespinner);


            Log.e("On Create: ", "laststatus is: " + sharedpreferences.getString("laststatus", "") );




            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    selectcode =spinner.getSelectedItem().toString();

                    //Toast.makeText(getContext(), spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                }


                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });



            //final  String selectcode = spinner.getSelectedItem().toString();
            mobilenum = txttelno.getText().toString();

            //final String mobile = (selectcode + txttelno.getText().toString());

            pDialog = new ProgressDialog(Registration.this);
            pDialog.setMessage("Sending code...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);





            signin = (Button) findViewById(R.id.register);





            if (sharedpreferences.getString("laststatus", "").contentEquals("entercode") ){

                Log.e("On Create: enetr code ", "laststatus is: " + sharedpreferences.getString("laststatus", "") );


                adetails.setVisibility(View.GONE);
                cdetails.setVisibility(View.VISIBLE);
                editnum.setVisibility(View.VISIBLE);
                resendcode.setVisibility(View.VISIBLE);
                textnocode.setVisibility(View.VISIBLE);
                signin.setText("SEND CODE");


            }
            else if (sharedpreferences.getString("laststatus", "").contentEquals("enterdetails")){

                Log.e("On Create enetrdtils: ", "laststatus is: " + sharedpreferences.getString("laststatus", "") );

                adetails.setVisibility(View.GONE);
                cdetails.setVisibility(View.GONE);
                editnum.setVisibility(View.GONE);
                resendcode.setVisibility(View.GONE);
                textnocode.setVisibility(View.GONE);
                pdetails.setVisibility(View.VISIBLE);
                signin.setText("SIGN UP");

            }









            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {


                    if (signin.getText().toString().trim().toUpperCase().contentEquals("VERIFY NUMBER")
                            ) {
                        if(txttelno.getText().toString().contentEquals("") ) {
                            //Toast.makeText(getContext(), "Please enter your mobile number", Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(Registration.this,"Please enter your mobile number!",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();

                        }

                        else{

                            telno = (selectcode + txttelno.getText().toString());
                            Log.e("Submit Button Text", signin.getText().toString());

                            new AlertDialog.Builder(Registration.this)
                                    .setTitle("Confirm number")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {






                                            saveTel(telno, "notresend");

                                            Log.e("Success", "" + telno);


                                        }
                                    })
                                    .setNegativeButton("Edit number", null)
                                    .setMessage(Html.fromHtml("Send sms to the number " + telno + "?" ))
                                    .show();


                        }
                    }

                    else if (signin.getText().toString().trim().toUpperCase().contentEquals("SEND CODE")
                            ) {
                        if(txtcode.getText().toString().contentEquals("") ) {
                            //Toast.makeText(getContext(), "Please enter the confirmation code!", Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(Registration.this,"Please enter the confirmation code!",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                        }

                        else{
                            code = txtcode.getText().toString();
                            telno = sharedpreferences.getString("lastnumber", "");

                            saveCode(code, telno);


                        }
                    }

                    else if (signin.getText().toString().trim().toUpperCase().contentEquals("SIGN UP")
                            ) {
                        if(txtusername.getText().toString().contentEquals("")  || txtpassword.getText().toString().contentEquals("") || txtemail.getText().toString().contentEquals("") ||
                                txtfname.getText().toString().contentEquals("")  || txtsname.getText().toString().contentEquals("") ) {
                            //Toast.makeText(getContext(), "All fields are mandatory", Toast.LENGTH_LONG).show();

                            Toast ToastMessage = Toast.makeText(Registration.this,"All fields are mandatory!",Toast.LENGTH_LONG);
                            View toastView = ToastMessage.getView();
                            toastView.setBackgroundResource(R.drawable.toast_background);
                            ToastMessage.show();
                        }

                        else{

                            Log.e("Submit Button Text", signin.getText().toString());
                            username = txtusername.getText().toString();
                            password = txtpassword.getText().toString();
                            Pconfirm = txtpasswordcon.getText().toString();
                            email = txtemail.getText().toString();

                            telno = txttelno.getText().toString();
                            fname = txtfname.getText().toString();
                            sname = txtsname.getText().toString();

                            code = txtcode.getText().toString();

                            if(password.contentEquals(Pconfirm) ) {
                                saveUser(password,sname,email,fname,username);
                            }

                            else{
                                //Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();

                                Toast ToastMessage = Toast.makeText(Registration.this,"Passwords do not match!!",Toast.LENGTH_LONG);
                                View toastView = ToastMessage.getView();
                                toastView.setBackgroundResource(R.drawable.toast_background);
                                ToastMessage.show();

                            }






                            Log.e("Submit Button Text", signin.getText().toString());
                        }
                    }





                }

            });



            //userid = (sharedpreferences.getString("Regid", ""));
            editnum.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    telno = MyPref.getPhone(Registration.this);

                    Log.e("onClick", "num from prefs" + telno);
                    Editnum(telno);   //get user id from lastinsert save code & upload editinum script



                }

            });

            resendcode.setOnClickListener(new View.OnClickListener(){


                                              @Override
                                              public  void onClick(View view){

                                                  telno = MyPref.getPhone(Registration.this);

                                                  Log.e("onClick", "num from prefs" + telno);
                                                  saveTel(telno, "Resend");

                                              }
                                          }
            );





        }
        catch (Exception ex)
        {
            Log.e("Main Thread Exception", "Error: " + ex.toString());
            System.gc();
        }

    }




    class CreateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registration.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            try {
                // Building Parameters
                ContentValues values=new ContentValues();
                values.put("username",username);
                values.put("password",password);
                values.put("surname", sname);
                // values.put("idno", idno);
                values.put("email", email);
                values.put("telno", telno);
                //values.put("add1", add1);
                //values.put("add2", add2);
                //values.put("surburb", suburb);
                //values.put("city", city);
                //values.put("region", region);
                //values.put("country", country);
                values.put("firstname", fname);


                Log.d("request!", "starting");

                //Posting user data to script

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", values);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Registration Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                //Toast.makeText(getContext(), file_url, Toast.LENGTH_LONG).show();

                Toast ToastMessage = Toast.makeText(Registration.this,file_url,Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();

            }

        }


    }


    private void saveUser(final String password, final String surname, final String email,
                          final String firstname,final String username){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(Registration.this);



        Log.e("NUMBER", MyPref.getPhone(Registration.this)+"<<<<<<<<<<<<<<<<<<<<<<<<<");
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success",""+s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject=new JSONObject(s);
                    int success=jsonObject.getInt("success");
                    String message=jsonObject.getString("message");
                    if(success==1){


                        sharedpreferences = Registration.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove("laststatus");
                        editor.commit();

                        Intent intent = new Intent(Registration.this, UserActivity.class);
                        startActivity(intent);



                        //Toast.makeText(getContext(), "Your account was created successful", Toast.LENGTH_SHORT).show();


                        Toast ToastMessage = Toast.makeText(Registration.this,"Your account was created successful",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();



                    }else{
                        //Toast.makeText(getContext(), (s), Toast.LENGTH_SHORT).show();

                        Toast ToastMessage = Toast.makeText(Registration.this,message,Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();

                Toast ToastMessage = Toast.makeText(Registration.this,"Please check your Intenet and try again!",Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();

                Log.e("RUEERROR",""+volleyError);
            }
        }){


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SharedPreferences sharedpreferences = Registration.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                Map<String,String> values=new HashMap();
                values.put("username",username);
                values.put("password",password);
                values.put("surname", surname);

                values.put("email", email);
                values.put("telno", MyPref.getPhone(Registration.this));

                values.put("firstname", firstname);
                values.put("user_id", (sharedpreferences.getString("user_id", "")));

                return values;
            }
        };
        SharedPreferences sharedpreferences = Registration.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        Log.e("RUEERROR",""+(sharedpreferences.getString("user_id", "")));

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);




    }

    private void saveTel(final String telno, final String type){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(Registration.this);


        pDialog = new ProgressDialog(Registration.this);
        pDialog.setMessage("Sending code...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        StringRequest stringRequest=new StringRequest(Request.Method.POST, CODE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success",""+s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject=new JSONObject(s);
                    int success=jsonObject.getInt("success");
                    String message=jsonObject.getString("message");
                    if(success==1){

                        Log.e("Success", "" + telno);

                        sharedpreferences = Registration.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("laststatus", "entercode");
                        editor.putString("lastnumber", telno);
                        editor.commit();

                        pdetails.setVisibility(View.GONE);
                        adetails.setVisibility(View.GONE);
                        cdetails.setVisibility(View.VISIBLE);
                        editnum.setVisibility(View.VISIBLE);
                        resendcode.setVisibility(View.VISIBLE);
                        textnocode.setVisibility(View.VISIBLE);

                        signin.setText("SEND CODE");


                        MyPref.savePhoneNumber(Registration.this,telno);


                        //Toast.makeText(getContext(),"Code sent!", Toast.LENGTH_SHORT).show();

                        Toast ToastMessage = Toast.makeText(Registration.this,"Code sent!",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();


                        JSONArray array=jsonObject.getJSONArray("posts");

                        JSONObject object=array.getJSONObject(0);



                    }else{
                        //Toast.makeText(getContext(), ("Number already in use!"), Toast.LENGTH_SHORT).show();


                        new AlertDialog.Builder(Registration.this)
                                .setTitle("Info")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                })
                                .setNegativeButton("", null)
                                .setMessage(Html.fromHtml(message ))
                                .show();

                    }



                } catch (JSONException e) {

                    //Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                    Log.e("Exception","  "+e);

                    Toast ToastMessage = Toast.makeText(Registration.this,"Check your internet connection!",Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();



                Log.e("RUEERROR",""+volleyError);


                //Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();

                Toast ToastMessage = Toast.makeText(Registration.this,"Check your internet connection!",Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                values.put("telno", telno);
                values.put("type", type);

                return values;
            }
        };
        requestQueue.add(stringRequest);




    }

    private void saveCode(final String code, final String telno){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(Registration.this);
        pDialog.setMessage("Verifying user...");
        pDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, VERIFY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();

                Log.e("Success",""+s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject=new JSONObject(s);
                    int success=jsonObject.getInt("success");
                    if(success==1){

                        String codeDB=jsonObject.getString("code");



                        if(!code.equals(codeDB)){

                            //Toast.makeText(getContext(), "Invalid code", Toast.LENGTH_LONG).show();

                            new AlertDialog.Builder(Registration.this)
                                    .setTitle("Info")
                                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {


                                        }
                                    })
                                    .setNegativeButton("", null)
                                    .setMessage(Html.fromHtml("Code incorrect, try again..." ))
                                    .show();

                            return;
                        }
                        sharedpreferences = Registration.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("laststatus", "enterdetails");
                        editor.commit();

                        Log.e("Submit Button Text", signin.getText().toString());
                        pdetails.setVisibility(View.VISIBLE);
                        editnum.setVisibility(View.GONE);
                        resendcode.setVisibility(View.GONE);
                        textnocode.setVisibility(View.GONE);
                        adetails.setVisibility(View.GONE);
                        cdetails.setVisibility(View.GONE);
                        signin.setText("SIGN UP");




                        //Toast.makeText(getContext(), "Verification successful", Toast.LENGTH_SHORT).show();



                        new AlertDialog.Builder(Registration.this)
                                .setTitle("Info")
                                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                })
                                .setNegativeButton("", null)
                                .setMessage(Html.fromHtml("Number Verification successful. Register your basic details" ))
                                .show();



                        String UserId=jsonObject.getString("user_id");

                        SharedPreferences sharedpreferences = Registration.this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);



                        editor.putString("user_id", UserId);
                        editor.commit();


                    }else{
                        //Toast.makeText(getContext(), ("Verification failed"), Toast.LENGTH_SHORT).show();


                        Toast ToastMessage = Toast.makeText(Registration.this,"Verification failed!",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();
                    }



                } catch (JSONException e) {

                    Toast ToastMessage = Toast.makeText(Registration.this,"Please check your Intenet and try again!",Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();

                Toast ToastMessage = Toast.makeText(Registration.this,"Please check your Intenet and try again!",Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();

                Log.e("RUEERROR",""+volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                values.put("code", code);
                values.put("telno", telno);

                return values;
            }
        };
        requestQueue.add(stringRequest);




    }




    private void Editnum(final String TElNo){
        com.android.volley.RequestQueue requestQueue= Volley.newRequestQueue(Registration.this);


        StringRequest stringRequest=new StringRequest(Request.Method.POST, EDIT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

                Log.e("Success",""+s);
                //{"success":1,"message":"Username Successfully Added!"}

                try {
                    JSONObject jsonObject=new JSONObject(s);
                    int success=jsonObject.getInt("success");
                    if(success==1){




                        //Toast.makeText(getContext(), "Number edit", Toast.LENGTH_SHORT).show();

                        Toast ToastMessage = Toast.makeText(Registration.this,"Edit your Number ",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();

                        pdetails.setVisibility(View.GONE);
                        cdetails.setVisibility(View.GONE);
                        editnum.setVisibility(View.GONE);
                        resendcode.setVisibility(View.GONE);
                        textnocode.setVisibility(View.GONE);
                        adetails.setVisibility(View.VISIBLE);
                        signin.setText("VERIFY NUMBER");


                    }else{
                        //Toast.makeText(getContext(), ("Cannot edit number"), Toast.LENGTH_SHORT).show();

                        Toast ToastMessage = Toast.makeText(Registration.this,"Cannot edit number",Toast.LENGTH_LONG);
                        View toastView = ToastMessage.getView();
                        toastView.setBackgroundResource(R.drawable.toast_background);
                        ToastMessage.show();
                    }



                } catch (JSONException e) {

                    Toast ToastMessage = Toast.makeText(Registration.this,"Please check your Intenet and try again!",Toast.LENGTH_LONG);
                    View toastView = ToastMessage.getView();
                    toastView.setBackgroundResource(R.drawable.toast_background);
                    ToastMessage.show();

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();

                Toast ToastMessage = Toast.makeText(Registration.this,"Please check your Intenet and try again!",Toast.LENGTH_LONG);
                View toastView = ToastMessage.getView();
                toastView.setBackgroundResource(R.drawable.toast_background);
                ToastMessage.show();
                Log.e("RUEERROR",""+volleyError);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> values=new HashMap();
                values.put("telno",TElNo);

                return values;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);




    }


}

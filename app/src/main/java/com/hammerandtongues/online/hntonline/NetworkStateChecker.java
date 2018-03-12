

package com.hammerandtongues.online.hntonline;

/**
 * Created by Ruvimbo on 28/11/2017.
 */


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetworkStateChecker extends BroadcastReceiver {



    //context and database helper object
    private Context context;
    private DatabaseHelper db;
    private ProgressDialog pDialog;


    @Override
    public void onReceive(Context context, Intent intent) {


        this.context = context;
        new GetConnectionStatus().execute();


    }

    /*
    * method taking two arguments
    * name that is to be saved and id of the name from SQLite
    * if the name is successfully sent
    * we will update the status as synced in SQLite
    * */

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context. getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    class GetConnectionStatus extends AsyncTask<String, Void, Boolean> {

        private Context mContext;

        protected void onPreExecute() {
            super.onPreExecute();



        }

        protected Boolean doInBackground(String... urls) {
            // TODO: Connect

            //pDialog.dismiss();
            if (isNetworkAvailable() == true) {


                try {
                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.getConnectTimeout();
                    urlc.connect();

                    return (urlc.getResponseCode() == 200);

                } catch (IOException e) {
                    Log.e("Network Check", "Error checking internet connection", e);
                }
            } else {
                Log.e("Network Check", "No network available!");
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {

            try {


                if (result == true)

                {




                    db = new DatabaseHelper(context);

                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                    //if there is a network
                    if (activeNetwork != null) {
                        //if connected to wifi or mobile data plan
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {





                            db.clearProducts();
                            db.clearStores();
                            db.clearCategories();
                            //db.fillcart();
                            db.fill_products(context);
                            db.fill_categories(context);
                            db.fill_stores(context);


                            //Cursor cursor = db.getUnsyncedNames();
                            // if (cursor.moveToFirst()) {
                            // do {
                            //calling the method to save the unsynced name to MySQL
                            // saveName(
                            //        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                            //       cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME))
                            //  );
                            //} while (cursor.moveToNext());
                            //}
                        }

                        else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                            db.fill_products(context);
                            db.fill_categories(context);
                            db.fill_stores(context);


                        }
                    }

                    else{
//do nothing

                    }


                    //pDialog.show();




                }
                else

                {


                }
            }
            catch (Exception ex) {
//                Toast.makeText(getContext(), "Weak / No Network Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }



}

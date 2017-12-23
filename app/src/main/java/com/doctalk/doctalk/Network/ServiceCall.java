package com.doctalk.doctalk.Network;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.doctalk.doctalk.MyAppController;
import com.doctalk.doctalk.R;


import org.json.JSONObject;

import java.util.Calendar;


/**
 * Created by jogendra
 * Helper class to manage call of all Volly requests in a Single place
 */

public class ServiceCall {
    private final String TAG = "ServiceCall";
    private final int CACHE_EXP_MIN = 60; //Cache will le last for 1 hour
    final int socketTimeout = 60000;//60 seconds - change to what you want
    private final int MAX_TRIES = 5; //Cache will le last for 1 hour
    private Request request;
    private Response.ErrorListener errorListener;
    public static final String USER_FETCH_URL = "https://api.github.com/search/users?q=%s&sort=followers&order=asc";
    Context context;

    public ServiceCall() {
    }

    public ServiceCall(Request request, Context context) {
        Calendar calendar = Calendar.getInstance();
        //Just to check a initial API call cache
        Cache.Entry entry = MyAppController.getInstance().getRequestQueue().getCache().get(USER_FETCH_URL);

        if (entry != null) {
            //Cache is expired clear cache
            if (getMinutesDifference(entry.serverDate, calendar.getTimeInMillis()) >= CACHE_EXP_MIN) {
                clearCache();
            }
        }

        //Set request
        this.request = request;

//        Set context
        this.context = context;
    }

    public Response.ErrorListener getErrorListener() {
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleError(error);
            }
        };
        return errorListener;
    }

    /**
     * Method to check time difference for response caching
     */
    private static long getMinutesDifference(long timeStart, long timeStop) {
        long diff = timeStop - timeStart;
        return diff / (60 * 1000);
    }

    public void callAPI() {
        try {
            //no response
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, MAX_TRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            //jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy((DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 100), DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyAppController.getInstance().addToRequestQueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
        }

    }


    private void handleError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {
            String json = new String(response.data);
            try {
                JSONObject object = new JSONObject(json);
                String message = object.optString("message");
                if (message == null || TextUtils.isEmpty(message)) {
                    message = object.optString("status");
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "onResponse: " + e.toString());
            }
        } else if (error instanceof NoConnectionError) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.checkNetwork),
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                Toast.makeText(context,
                        context.getResources().getString(R.string.tryLater),
                        Toast.LENGTH_SHORT).show();
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "onResponse: " + e.toString());
            }
        }
    }

    //Method to clear cache of Volly
    private void clearCache() {
        MyAppController.getInstance().getRequestQueue().getCache().clear();
    }


}



package com.app.newsagni.fcm;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private static Context context;
    private RequestQueue requestQueue;

    private MySingleton(Context context)
    {
        this.context=context;
        requestQueue=getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if (requestQueue==null)
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public static synchronized MySingleton getmInstance(Context context)
    {
        if (mInstance==null)
            mInstance=new MySingleton((context));
        return mInstance;
    }

    public <T>void addToRequestQue(Request<T> request)
    {
        getRequestQueue().add(request);
    }

}

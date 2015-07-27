package com.macmoim.pang.multipart;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yyb
 * on 15-2-4.
 */
public class GsonRequest<T> extends Request<T> {


    protected final Gson gson = new Gson();
    protected final Class<T> clazz;
    protected final Listener<T> listener;
    protected final Map<String, String> headers;
    protected final Map<String, String> params;


    public GsonRequest(String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        this(method, url, null, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Map<String, String> params, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        this(method, url, null, params, clazz, listener, errorListener);
    }

    public GsonRequest(int method, String url, Map<String, String> headers, Map<String, String> params, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.params = params;
        this.headers = headers;
        Set<String> keyset = params.keySet();
        for (String key : keyset) {
            Log.d("GsonRequest", "params " +  " key : " + key + " value "+ params.get(key) );
            
        }
        
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        Set<String> keyset = response.headers.keySet();
        for (String key : keyset) {
            Log.d("GsonRequest", "parseNetworkResponse " +  " key : " + key + " value "+ response.headers.get(key) );
            
        }
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("GsonRequest", "parseNetworkResponse json "+json );
            JSONObject json_response=null;
            try {
                json_response = new JSONObject(json);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Response.success(/*gson.fromJson(json, clazz)*/json_response, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            volleyError = new VolleyError(new String(volleyError.networkResponse.data));
        }
        return volleyError;
    }

    @Override
    protected void deliverResponse(T response) {
        if (null != listener) {
            listener.onResponse(response);
        }

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }


}

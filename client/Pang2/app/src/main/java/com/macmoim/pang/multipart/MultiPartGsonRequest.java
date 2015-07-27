package com.macmoim.pang.multipart;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;


/**
 * Created by yyb
 * on 15-3-26.
 */
public class MultiPartGsonRequest<T> extends GsonRequest<T> implements IMultiPartRequest {


    /* To hold the parameter name and the File to upload */
    private Map<String, File> fileUploads = new HashMap<String, File>();
    private String fileBoyeType = "";

    public static String Gzip = "application/zip";
    public static String Mpeg3 = "application/mpeg3";


    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public MultiPartGsonRequest(int method, String url, Class<T> clazz, Map<String, File> fileUploads, Map<String, String> params, Listener<T> listener,
                                ErrorListener errorListener) {
        super(method, url, params, clazz, listener, errorListener);
        this.fileUploads = fileUploads;
    }


    /**
     * 誤곦툓鴉좂쉪�뉏뻑
     */
    public Map<String, File> getFileUploads() {
        return fileUploads;
    }

    /**
     * 誤곦툓鴉좂쉪�귝빊
     */
    public Map<String, String> getStringUploads() {
        return params;
    }

    @Override
    public void setFileBodyContentType(String contentType) {
        this.fileBoyeType = contentType;
    }

    @Override
    public String getFileBodyContentType() {
        return fileBoyeType;
    }


    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        return super.parseNetworkResponse(response);
    }

    @Override
    protected void deliverResponse(T response) {
        super.deliverResponse(response);
    }

    /**
     * 令븃〃鹽뷰툖訝듾폖
     * 瓦쇾릉瓦붷썮null竊뚦푽�띹쫨�귛쑉multipartstack�꼊reateMultiPartRequest�방퀡訝�
     * if (request.getBodyContentType() != null),倻귝옖瓦붷썮�꾡툖訝븀㈉,弱긴폏曆삣뒥���雅ㅴ틨�꼊ontenttype��
     */
    public String getBodyContentType() {
        return null;
    }
}

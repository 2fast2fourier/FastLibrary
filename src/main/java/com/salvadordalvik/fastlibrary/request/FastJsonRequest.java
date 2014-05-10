package com.salvadordalvik.fastlibrary.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import org.json.JSONObject;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public abstract class FastJsonRequest<T> extends FastRequest<T> {
    public FastJsonRequest(String baseUrl, int method, Response.Listener<T> success, Response.ErrorListener error) {
        super(baseUrl, method, success, error);
    }

    @Override
    public T parseResponse(Request<T> request, NetworkResponse response) throws Exception {
        String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        return parseJSONResponse(new JSONObject(json), response);
    }

    public abstract T parseJSONResponse(JSONObject response, NetworkResponse responseData);

    @Override
    protected String getBodyType() {
        return "application/json; charset=utf-8";
    }
}

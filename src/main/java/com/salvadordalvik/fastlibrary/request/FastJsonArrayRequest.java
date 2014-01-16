package com.salvadordalvik.fastlibrary.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import org.json.JSONArray;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public abstract class FastJsonArrayRequest<T> extends FastRequest<T> {
    public FastJsonArrayRequest(String baseUrl, int method, Response.Listener<T> success, Response.ErrorListener error) {
        super(baseUrl, method, success, error);
    }

    @Override
    public T parseResponse(NetworkResponse response) throws Exception {
        String json = new String(response.data, parseCharset(response.headers, "UTF-8"));
        return parseJSONResponse(new JSONArray(json), response);
    }

    public abstract T parseJSONResponse(JSONArray response, NetworkResponse responseData);
}

package com.salvadordalvik.fastlibrary.request;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by matthewshepard on 2/4/14.
 */
public abstract class FastXMLRequest<Result> extends FastRequest<Result> {
    public FastXMLRequest(String baseUrl, int method, Response.Listener<Result> success, Response.ErrorListener error) {
        super(baseUrl, method, success, error);
    }

    @Override
    public Result parseResponse(Request<Result> request, NetworkResponse response) throws Exception {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new ByteArrayInputStream(response.data), parseCharset(response.headers, "UTF-8"));
        parser.nextTag();
        return processResponseXml(parser);
    }

    public abstract Result processResponseXml(XmlPullParser xml) throws Exception;
    public static interface XmlAttributeCallback{
        public void attributeFound(String tagName, String attributeName, String attributeValue);
    }
    public static interface ChildTagCallback{
        public void childFound(String tagName, XmlPullParser xml) throws Exception;
        public boolean otherTagFound(String otherTag, XmlPullParser xml) throws Exception;
    }

    protected static boolean findNextTag(XmlPullParser xml, String tagName) throws IOException, XmlPullParserException {
        int event = xml.getEventType();
        while(event != XmlPullParser.END_DOCUMENT){
            if(event == XmlPullParser.START_TAG && tagName.equalsIgnoreCase(xml.getName())){
                return true;
            }
            event = xml.next();
        }
        return false;
    }

    protected static String getAllText(XmlPullParser xml) throws IOException, XmlPullParserException {
        int depth = 0, event;
        StringBuilder textContent = new StringBuilder();
        do{
            event = xml.nextToken();
            switch (event){
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.CDSECT:
                    textContent.append(xml.getText());
                    break;
                case XmlPullParser.ENTITY_REF:
                    textContent.append(xml.getText());
                    break;
                case XmlPullParser.TEXT:
                    textContent.append(xml.getText());
                    break;
            }
        }while(depth >= 0 && event != XmlPullParser.END_DOCUMENT);
        return textContent.toString();
    }

    /**
     * Parses a set of tags starting at the current position until the matching closing tag.
     * Stores the string content of the specified tags in the results array.
     * Checks to see if all required elements exist, will return true or false if complete record was found.
     * @param xml
     * @param tags
     * @param results
     * @param required
     * @param callback
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    protected static boolean parseXmlTagset(XmlPullParser xml, String[] tags, String[] results, boolean[] required, XmlAttributeCallback callback) throws IOException, XmlPullParserException{
        int depth = 0, event, attrCount, resultPos = 0;
        StringBuilder textContent = null;
        for(int ix=0;ix<results.length;ix++){
            results[ix] = null;
        }
        do{
            event = xml.nextToken();
            switch (event){
                case XmlPullParser.START_TAG:
                    depth++;
                    String currentTag = xml.getName();
                    attrCount = xml.getAttributeCount();
                    if(callback != null && attrCount > 0){
                        for(int ix=0; ix<attrCount;ix++){
                            callback.attributeFound(currentTag, xml.getAttributeName(ix), xml.getAttributeValue(ix));
                        }
                    }
                    for(int ix=0;ix<tags.length;ix++){
                        if(tags[ix].equalsIgnoreCase(currentTag)){
                            textContent = new StringBuilder();
                            resultPos = ix;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    if(textContent != null){
                        results[resultPos] = textContent.toString();
                    }
                    textContent = null;
                    break;
                case XmlPullParser.CDSECT:
                    if(textContent != null){
                        textContent.append(xml.getText());
                    }
                    break;
                case XmlPullParser.ENTITY_REF:
                    if(textContent != null){
                        textContent.append(xml.getText());
                    }
                    break;
                case XmlPullParser.TEXT:
                    if(textContent != null){
                        textContent.append(xml.getText());
                    }
                    break;
            }
        }while(depth >= 0);
        for(int ix=0;ix<required.length;ix++){
            if(required[ix] && TextUtils.isEmpty(results[ix])){
                Log.e("FastXMLRequest", "parseXmlItems failed at " + tags[ix]);
                return false;
            }
        }
        return true;
    }

    protected static void findChildTags(XmlPullParser xml, String tag, ChildTagCallback callback) throws Exception{
        int depth = 0, event;
        do{
            event = xml.nextToken();
            switch (event){
                case XmlPullParser.START_TAG:
                    String currentTag = xml.getName();
                    if(currentTag.equalsIgnoreCase(tag)){
                        callback.childFound(currentTag, xml);
                    }else if(!callback.otherTagFound(currentTag, xml)){
                        depth++;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }while(depth >= 0);
    }
}

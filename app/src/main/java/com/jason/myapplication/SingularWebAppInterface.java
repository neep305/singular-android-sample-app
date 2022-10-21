package com.jason.myapplication;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.singular.sdk.Singular;
import com.singular.sdk.SingularConfig;
import com.singular.sdk.internal.SingularLog;

import org.json.JSONException;
import org.json.JSONObject;

public class SingularWebAppInterface {
    private static final SingularLog logger = SingularLog.getLogger("SingularJSInterface");

    Context mContext;
    int webViewId;

    SingularWebAppInterface(Context c) {
        mContext = c;
//        singularConfig = new SingularConfig(Constants.API_KEY, Constants.SECRET);
//        Singular.init(mContext, singularConfig);
    }

    @JavascriptInterface
    public void setWebViewId(int id) {
        logger.debug("setWebViewId(id=" + id + ")");
        this.webViewId = id;
    }

    @JavascriptInterface
    public boolean event(String name) {
        logger.debug("event(name=" + name + ")");
        return Singular.event(name);
    }

    @JavascriptInterface
    public void event(String name, String JSONString) throws JSONException {
        logger.debug("event(name=" + name + ", JSONString=" + JSONString + ")");
        JSONObject json = new JSONObject(JSONString);
        Singular.eventJSON(name, json);
    }

    @JavascriptInterface
    public void revenue(String currency, double amount) throws Exception {
        logger.debug("revenue(currency=" + currency + ", amount=" + amount + ")");
        Singular.revenue(currency, amount);
    }

    @JavascriptInterface
    public void revenue(String eventName, String currency, double amount, String JSONString) throws JSONException {
        logger.debug("event(name=" + eventName + ", JSONString=" + JSONString + ")");
        JSONObject json = new JSONObject(JSONString);
//        Singular.customRevenue(eventName, currency, amount, json);
        Singular.customRevenue(eventName, currency, amount, json.getString("sku"), json.getString("productName"), "Shoes", 1, json.getDouble("price"));
    }

//    @JavascriptInterface
//    public void revenue(String eventName, String currency, double amount, String productSKU, String productName, String productCategory, int productQuantity, double productPrice) throws JSONException {
//        logger.debug("event(name=" + eventName + ", productName=" + productName + ", productCategory=" + productCategory + ")");
//
//        Singular.customRevenue(eventName, currency, amount, productSKU, productName, productCategory, productQuantity, productPrice);
//    }

    @JavascriptInterface
    public void setCustomUserId(String customUserId) throws JSONException {
        logger.debug("setCustomUserId(customUserId=" + customUserId + ")");
        Singular.setCustomUserId(customUserId);
    }

    @JavascriptInterface
    public void unsetCustomUserId() throws JSONException {
        logger.debug("unsetCustomUserId()");
        Singular.unsetCustomUserId();
    }
}

package com.jason.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.jason.myapplication.databinding.FragmentFirstBinding;

import com.singular.sdk.*;
import com.singular.sdk.Singular;
import com.singular.sdk.SingularConfig;
import com.singular.sdk.SingularLinkHandler;

import com.android.billingclient.api.Purchase;
import com.singular.sdk.SingularLinkParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private EditText eventNameText;
    private Spinner currencyCodeText;
    private EditText priceText;
    private EditText etDeeplink;
    private EditText etPassthrough;

    private CheckBox cbRevenueWithoutReceipt;

    private String deeplinkValue = "deeplink";
    private String passthroughValue = "passthrough";
    private boolean isDeferred = false;

    public InputMethodManager inputMethodManager;

    public boolean isRevenueChecked = false;

    // create array of Strings
    // and store name of courses
    String[] currencyCode = { "USD", "EUR", "KRW", "JPN", "GBR", "INR" };

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.i("FirstFragment", "onCreateView");
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        binding = FragmentFirstBinding.inflate(inflater, container, false);

//        if (deeplinkData == null) {
//            return binding.getRoot();
//        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        Bundle deeplinkData = ((MainActivity) getActivity()).getDeeplinkData();
//
//        ((EditText) binding.etDeeplink).setText(deeplinkData == null ? Constants.DEEPLINK_KEY : deeplinkData.getString(Constants.DEEPLINK_KEY));
//        ((EditText) binding.etPassthrough).setText(deeplinkData == null ? Constants.DEEPLINK_KEY : deeplinkData.getString(Constants.PASSTHROUGH_KEY));
//        ((EditText) binding.etIsDeferred).setText(deeplinkData == null ? Constants.DEEPLINK_KEY : String.valueOf(deeplinkData.getBoolean(Constants.IS_DEFERRED_KEY)));

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

        binding.buttonPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRevenue(isRevenueChecked    , eventNameText, "USD", priceText);
            }
        });

        binding.buttonInappEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEvent(eventNameText);
            }
        });

        binding.buttonGotoWeb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_WebviewFragment);
            }
        });

        // RevenueWithoutReceipt
        binding.ctvPurchaseWithoutReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()) {
                    isRevenueChecked = true;
                } else {
                    isRevenueChecked = false;
                }
            }
        });

        ArrayAdapter<String> ad = new ArrayAdapter<String>((MainActivity)getActivity(), android.R.layout.simple_spinner_item, currencyCode);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(ad);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        eventNameText = binding.eventNameText;
        priceText = binding.priceText;

        //set deeplink info
        etDeeplink = binding.etDeeplink;
        etPassthrough = binding.etPassthrough;
        etDeeplink.setText(deeplinkValue);
        etPassthrough.setText(passthroughValue);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sendEvent(EditText eventNameText) {
        String eventName = eventNameText.getText().toString().trim();

        try {
            JSONArray contents = new JSONArray();
            JSONObject item1 = new JSONObject();

            item1.put("sku", "UPC-018627610014");
            item1.put("qty", 2);
            item1.put("unit_price", 8.99);
            item1.put("currency", "USD");
            contents.put(item1);

            JSONObject item2 = new JSONObject();
            item2.put("sku", "UPC-070271003758");
            item2.put("qty", 1);
            item2.put("unit_price", 15.99);
            item2.put("currency", "USD");
            contents.put(item2);

            JSONObject args = new JSONObject();
            args.put("contents", contents);
            args.put("total", 63.96);
            args.put("currency", "USD");
            args.put("member_id", "A556740089");

            // Record the event with Singular
            Singular.eventJSON(eventName, args);
            Utils.showToast(getContext(), eventName + " sent");
            eventNameText.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
        catch(JSONException e) {
            android.util.Log.e("Now", "JSON Exception in cart");
        }
    }

    private void sendRevenue(boolean isSimpleRevenue, EditText eventNameText, String currencyCode, EditText priceText) {
        String eventName = eventNameText.getText().toString().trim();

        if (Utils.isNullOrEmpty(eventName)) {
            Utils.showToast(getContext(), "Please enter a valid event name");
            return;
        }

        String currency = currencyCode; //currencyCodeText.getSelectedItem().toString().trim();

        if (Utils.isNullOrEmpty(currency)) {
            Utils.showToast(getContext(), "Please enter a valid currency");
            return;
        }

        String price = priceText.getText().toString().trim();

        if (Utils.isNullOrEmpty(price) || Double.parseDouble(price) == 0) {
            Utils.showToast(getContext(), "Revenue can't be zero or empty");
            return;
        }

        if (isSimpleRevenue) {

            // Reporting a simple revenue event to Singular
            Singular.customRevenue(eventName, currency, Double.parseDouble(price));
            //Singular.revenue(currency, Double.parseDouble(price));

            Utils.showToast(getContext(), "Revenue event sent");
        } else {
            Purchase purchase = buildFakePurchase2();

            if (purchase == null) {
                Utils.showToast(getContext(), "Failed to create a fake purchase");
                return;
            }

            double test_price = 1.99;
            // Instead of sending a real purchase we create a fake one for testing.
            // In your production environment, the Purchase object should be received from the Google Billing API.
            //Singular.customRevenue(eventName, currency, Double.parseDouble(price), purchase);
            //Singular.customRevenue(eventName, currency, Double.parseDouble(price), "112233", "Nike Air Max 95", "Shoes", 1, 99000);
            //Singular.revenue(currency, Double.parseDouble(price), purchase);
            //Singular.eventJSON("__iap__", new JSONObject("{\"orderId\":\"GPA.3365-7705-8199-11502\",\"packageName\":\"com.jason.myapplication\",\"productId\":\"2000_coins\",\"purchaseTime\":1657605203933,\"purchaseState\":0,\"purchaseToken\":\"cjmpmlnnfjmhobkiklogbhje.AO-J1OwmYrfdZFEd4rIMuijhVkA1VpvvUCPsJCgFx8QqukJy8ATqL1cUiI_5FKLr0DqJzcz5X5RX6Y8wGDEzzbbRTYCH-u4Q_ycoGVN8yurQxyQ4QYRhDic\",\"quantity\":1,\"acknowledged\":false}", "test_signature"));
            Singular.event("__iap__", "pcc", currency, "r", test_price, "pk", "2000_coins", "receipt", "{\"orderId\":\"GPA.3365-7705-8199-11502\",\"packageName\":\"com.jason.myapplication\",\"productId\":\"2000_coins\",\"purchaseTime\":1663288782,\"purchaseState\":0,\"purchaseToken\":\"cjmpmlnnfjmhobkiklogbhje.AO-J1OwmYrfdZFEd4rIMuijhVkA1VpvvUCPsJCgFx8QqukJy8ATqL1cUiI_5FKLr0DqJzcz5X5RX6Y8wGDEzzbbRTYCH-u4Q_ycoGVN8yurQxyQ4QYRhDic\",\"quantity\":1,\"acknowledged\":false}", "receipt_signature", "test_signature", "is_revenue_event", true);
//            Singular.customRevenue("__iap__", "KRW", 1100, "{\"orderId\":\"GPA.3365-7705-8199-11502\",\"packageName\":\"com.jason.myapplication\",\"productId\":\"2000_coins\",\"purchaseTime\":1657605203933,\"purchaseState\":0,\"purchaseToken\":\"cjmpmlnnfjmhobkiklogbhje.AO-J1OwmYrfdZFEd4rIMuijhVkA1VpvvUCPsJCgFx8QqukJy8ATqL1cUiI_5FKLr0DqJzcz5X5RX6Y8wGDEzzbbRTYCH-u4Q_ycoGVN8yurQxyQ4QYRhDic\",\"quantity\":1,\"acknowledged\":false}", "test_signature");
            Utils.showToast(getContext(), "IAP sent");
        }
    }

    private Purchase buildFakePurchase2() {
        Purchase fakePurchase;

        try {
            JSONObject json = new JSONObject();
//            json.put("sku", "UPC-070271003758");
//            json.put("qty", 1);
//            json.put("unit_price", 15900);
//            json.put("currency", "KRW");

            json.put("orderId", "GPA.3365-7705-8199-11502");
            json.put("packageName", "com.jason.myapplication");
            json.put("productId", "2000_coins");
            json.put("purchaseTime", System.currentTimeMillis());
            json.put("purchaseState", 0);
            json.put("purchaseToken", "cjmpmlnnfjmhobkiklogbhje.AO-J1OwmYrfdZFEd4rIMuijhVkA1VpvvUCPsJCgFx8QqukJy8ATqL1cUiI_5FKLr0DqJzcz5X5RX6Y8wGDEzzbbRTYCH-u4Q_ycoGVN8yurQxyQ4QYRhDic");
            json.put("quantity", 1);
            json.put("acknowledged", false);
//
            fakePurchase = new Purchase(json.toString(), "test_signature");
//            fakePurchase = new Purchase("{\"orderId\":\"GPA.3365-7705-8199-11502\",\"packageName\":\"com.jason.myapplication\",\"productId\":\"2000_coins\",\"purchaseTime\":1657605203933,\"purchaseState\":0,\"purchaseToken\":\"cjmpmlnnfjmhobkiklogbhje.AO-J1OwmYrfdZFEd4rIMuijhVkA1VpvvUCPsJCgFx8QqukJy8ATqL1cUiI_5FKLr0DqJzcz5X5RX6Y8wGDEzzbbRTYCH-u4Q_ycoGVN8yurQxyQ4QYRhDic\",\"quantity\":1,\"acknowledged\":false}", "test_signature");
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

        return fakePurchase;
    }

    private Purchase buildFakePurchase() {
        Purchase fakePurchase;

        try {
            JSONObject json = new JSONObject();
            json.put("productId", "fake_product_id");

            fakePurchase = new Purchase(json.toString(), "test_signature");
        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

        return fakePurchase;
    }

    public void setDeeplink(SingularLinkParams singularLinkParams) {
        Log.i("DEEPLINK_KEY", singularLinkParams.getDeeplink());
        Log.i("PASSTHROUGH_KEY", singularLinkParams.getPassthrough());
        Log.i("IS_DEFERRED_KEY", String.valueOf(singularLinkParams.isDeferred()));
    }
}
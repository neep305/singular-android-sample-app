package com.jason.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.jason.myapplication.databinding.FragmentFirstBinding;

import com.singular.sdk.Singular;
import com.singular.sdk.SingularConfig;
import com.singular.sdk.SingularLinkHandler;

import com.android.billingclient.api.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    private EditText eventNameText;
    private Spinner currencyCodeText;
    private EditText priceText;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                sendRevenue(false    , eventNameText, "KRW", priceText);
            }
        });

        eventNameText = binding.eventNameText;
        priceText = binding.priceText;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
            Purchase purchase = buildFakePurchase();

            if (purchase == null) {
                Utils.showToast(getContext(), "Failed to create a fake purchase");
                return;
            }

            // Instead of sending a real purchase we create a fake one for testing.
            // In your production environment, the Purchase object should be received from the Google Billing API.
            //Singular.customRevenue(eventName, currency, Double.parseDouble(price), purchase);
            Singular.customRevenue(eventName, currency, Double.parseDouble(price), "112233", "Nike Air Max 95", "Shoes", 1, 99000);

            Utils.showToast(getContext(), "IAP sent");
        }
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
}
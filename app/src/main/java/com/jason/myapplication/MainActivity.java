package com.jason.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jason.myapplication.databinding.ActivityMainBinding;

import com.singular.sdk.Singular;
import com.singular.sdk.SingularConfig;
import com.singular.sdk.SingularLinkHandler;
import com.singular.sdk.SingularLinkParams;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Bundle deeplinkData;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainActivity.class.getName(), "onCreate....");
        // activity_main layout 렌더링
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()) {
                    Log.i(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                token = task.getResult();

                initSingularSDK(token);
            }
        });

//        initSingularSDK();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initSingularSDK(String token) {

        SingularConfig config = new SingularConfig(Constants.API_KEY, Constants.SECRET)
                .withSingularLink(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), new SingularLinkHandler() {
                    @Override
                    public void onResolved(SingularLinkParams singularLinkParams) {
                        Log.i("DEEPLINK_KEY", singularLinkParams.getDeeplink());
                        Log.i("PASSTHROUGH_KEY", singularLinkParams.getPassthrough());
                        Log.i("IS_DEFERRED_KEY", String.valueOf(singularLinkParams.isDeferred()));
                        Toast.makeText(MainActivity.this, singularLinkParams.getDeeplink(), Toast.LENGTH_LONG);
                    }
                })
                .withCustomUserId("test_user_1234")
                .withSessionTimeoutInSec(120)
                .withLoggingEnabled()
                .withDDLTimeoutInSec(300)
                .withFCMDeviceToken(token);

        Singular.init(this, config);
    }

    private void initSingularSDK() {

        SingularConfig config = new SingularConfig(Constants.API_KEY, Constants.SECRET)
                .withSingularLink(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), new SingularLinkHandler() {
                    @Override
                    public void onResolved(SingularLinkParams singularLinkParams) {

                        Log.i("DEEPLINK_KEY", singularLinkParams.getDeeplink());
                        if (singularLinkParams.getPassthrough() != null) Log.i("PASSTHROUGH_KEY", singularLinkParams.getPassthrough());
                        Log.i("IS_DEFERRED_KEY", String.valueOf(singularLinkParams.isDeferred()));

                        deeplinkData = new Bundle();
                        deeplinkData.putString(Constants.DEEPLINK_KEY, singularLinkParams.getDeeplink());
                        if (singularLinkParams.getPassthrough() != null) deeplinkData.putString(Constants.PASSTHROUGH_KEY, singularLinkParams.getPassthrough());
                        deeplinkData.putBoolean(Constants.IS_DEFERRED_KEY, singularLinkParams.isDeferred());

                        // When the is opened using a deeplink, we will open the deeplink tab
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, singularLinkParams.getDeeplink(), Toast.LENGTH_LONG);
                                Log.i("DEEPLINK RUN", "runOnUiThread() called....");
                            }
                        });
                    }
                })
                .withCustomUserId("JasonNam")
                .withSessionTimeoutInSec(120)
                .withLoggingEnabled()
                .withDDLTimeoutInSec(300);

        Singular.init(this, config);
//        Singular.setFCMDeviceToken(token);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause called....");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called....");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String query = intent.getData().getQuery();
        Log.i(TAG, query);
    }

    private void saveSingularClickId(Intent intent) {
        SharedPreferences pref = getSharedPreferences("singular", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        String referrer = intent.getData().getQueryParameter("referrer");
        if (referrer.contains("singular_click_id")) {
            editor.putString("singular_click_id", referrer.split("=")[1]);
            editor.commit();
        }
    }

    public Bundle getDeeplinkData() {
        Bundle bundle = deeplinkData;
        deeplinkData = null;

        return bundle;
    }
}
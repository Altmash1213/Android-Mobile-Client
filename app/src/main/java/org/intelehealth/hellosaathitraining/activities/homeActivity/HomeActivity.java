package org.intelehealth.hellosaathitraining.activities.homeActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.intelehealth.hellosaathitraining.activities.identificationActivity.IdentificationActivity;
import org.intelehealth.hellosaathitraining.activities.ivrCallResponseActivity.IVRCallResponseActivity;
import org.intelehealth.hellosaathitraining.models.dailyPerformance.CallNumResponse;
import org.intelehealth.hellosaathitraining.models.dailyPerformance.CallNums;
import org.intelehealth.hellosaathitraining.models.dailyPerformance.RegistrationResponse;
import org.intelehealth.hellosaathitraining.models.dailyPerformance.Registrations;
import org.intelehealth.hellosaathitraining.models.dailyPerformance.SubscriptionResponse;
import org.intelehealth.hellosaathitraining.models.dailyPerformance.Subscriptions;
import org.intelehealth.hellosaathitraining.utilities.UrlModifiers;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.intelehealth.hellosaathitraining.R;
import org.intelehealth.hellosaathitraining.activities.activePatientsActivity.ActivePatientActivity;
import org.intelehealth.hellosaathitraining.activities.loginActivity.LoginActivity;
import org.intelehealth.hellosaathitraining.activities.searchPatientActivity.SearchPatientActivity;
import org.intelehealth.hellosaathitraining.activities.settingsActivity.SettingsActivity;
import org.intelehealth.hellosaathitraining.activities.todayPatientActivity.TodayPatientActivity;
import org.intelehealth.hellosaathitraining.app.AppConstants;
import org.intelehealth.hellosaathitraining.app.IntelehealthApplication;
import org.intelehealth.hellosaathitraining.models.CheckAppUpdateRes;
import org.intelehealth.hellosaathitraining.models.DownloadMindMapRes;
import org.intelehealth.hellosaathitraining.networkApiCalls.ApiClient;
import org.intelehealth.hellosaathitraining.networkApiCalls.ApiInterface;
import org.intelehealth.hellosaathitraining.syncModule.SyncUtils;
import org.intelehealth.hellosaathitraining.utilities.DownloadMindMaps;
import org.intelehealth.hellosaathitraining.utilities.FileUtils;
import org.intelehealth.hellosaathitraining.utilities.Logger;
import org.intelehealth.hellosaathitraining.utilities.NetworkConnection;
import org.intelehealth.hellosaathitraining.utilities.OfflineLogin;
import org.intelehealth.hellosaathitraining.utilities.SessionManager;
import org.intelehealth.hellosaathitraining.widget.materialprogressbar.CustomProgressDialog;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Home Screen
 */

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    SessionManager sessionManager = null;
    SessionManager sessionManager1 = null;
    private ProgressDialog mSyncProgressDialog;
    CountDownTimer CDT;
    private boolean hasLicense = false;
    int i = 5;

    TextView lastSyncTextView;
    TextView lastSyncAgo;
    Button manualSyncButton;
    //IntentFilter filter;

    SyncUtils syncUtils = new SyncUtils();
    // CardView c1, c2, c3, c4, c5, c6;
    CardView c1_doctor, c1_medadvice, c2, c3, c4, c5, c6;
    private String key = null;
    private String licenseUrl = null;

    Context context;
    CustomProgressDialog customProgressDialog;
    private String mindmapURL = "";
    private DownloadMindMaps mTask;
    ProgressDialog mProgressDialog;

    private int versionCode = 0;
    private CompositeDisposable disposable = new CompositeDisposable();
    TextView newPatient_textview, findPatients_textview, todaysVisits_textview,
            activeVisits_textview, videoLibrary_textview, help_textview, totalRegTV, totalSubsTV, totalCallsTV;
    boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sessionManager = new SessionManager(this);
        sessionManager1 = new SessionManager(HomeActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTheme);
        toolbar.setTitleTextColor(Color.WHITE);

        String language = sessionManager1.getAppLanguage();
        if (!language.equalsIgnoreCase("")) {
            setLocale(language);
        }

        setTitle(R.string.title_activity_login);
        context = HomeActivity.this;
        customProgressDialog = new CustomProgressDialog(context);
        /*syncBroadcastReceiver = new SyncBroadcastReceiver();
        filter = new IntentFilter(AppConstants.SYNC_INTENT_ACTION);*/

        sessionManager.setCurrentLang(getResources().getConfiguration().locale.toString());

        checkAppVer();  //auto-update feature.

        Logger.logD(TAG, "onCreate: " + getFilesDir().toString());
        lastSyncTextView = findViewById(R.id.lastsynctextview);
        lastSyncAgo = findViewById(R.id.lastsyncago);
        manualSyncButton = findViewById(R.id.manualsyncbutton);
//        manualSyncButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        // c1 = findViewById(R.id.cardview_newpat);
        c1_doctor = findViewById(R.id.cardview_newpat);
        c1_medadvice = findViewById(R.id.cardview_newpat_1);
        c2 = findViewById(R.id.cardview_find_patient);
        c3 = findViewById(R.id.cardview_today_patient);
        c4 = findViewById(R.id.cardview_active_patients);
        c5 = findViewById(R.id.cardview_video_libraby);
        c6 = findViewById(R.id.cardview_help_whatsapp);
        totalCallsTV = findViewById(R.id.total_calls);
        totalRegTV = findViewById(R.id.total_reg);
        totalSubsTV = findViewById(R.id.total_subs);
        //card textview referrenced to fix bug of localization not working in some cases...
        getDailyPerformance(getTodayDate(), sessionManager.getChwname(),sessionManager.getProviderPhoneno());
        newPatient_textview = findViewById(R.id.newPatient_textview);
        newPatient_textview.setText(R.string.new_patient);

        findPatients_textview = findViewById(R.id.findPatients_textview);
        findPatients_textview.setText(R.string.find_patient);

        todaysVisits_textview = findViewById(R.id.todaysVisits_textview);
        todaysVisits_textview.setText(R.string.today_visits);

        activeVisits_textview = findViewById(R.id.activeVisits_textview);
        activeVisits_textview.setText(R.string.active_visits);

        videoLibrary_textview = findViewById(R.id.videoLibrary_textview);
        videoLibrary_textview.setText(R.string.video_library);

        help_textview = findViewById(R.id.help_textview);
        help_textview.setText(R.string.Whatsapp_Help_Cardview);

        // manualSyncButton.setText(R.string.sync_now);
        manualSyncButton.setText(R.string.refresh);

        //Help section of watsapp...
        c6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumberWithCountryCode = "+917005308163";
                String message =
                        getString(R.string.hello_my_name_is) + " " + sessionManager.getChwname() + " " +
                                /*" from " + sessionManager.getState() + */getString(R.string.i_need_assistance);

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(
                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                                        phoneNumberWithCountryCode, message))));
            }
        });
/*
        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Loads the config file values and check for the boolean value of privacy key.
                ConfigUtils configUtils = new ConfigUtils(HomeActivity.this);
                if (configUtils.privacy_notice()) {
                    Intent intent = new Intent(HomeActivity.this, PrivacyNotice_Activity.class);
                    startActivity(intent);
                } else {
                    //Clear HouseHold UUID from Session for new registration
                    sessionManager.setHouseholdUuid("");

                    Intent intent = new Intent(HomeActivity.this, IdentificationActivity.class);
                    startActivity(intent);
                }
            }
        });
*/


        c1_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdentificationActivity.start(HomeActivity.this, false);

              /*  //Loads the config file values and check for the boolean value of privacy key.
                ConfigUtils configUtils = new ConfigUtils(HomeActivity.this);
                if (configUtils.privacy_notice()) {
                    Intent intent = new Intent(HomeActivity.this, PrivacyNotice_Activity.class);
                    startActivity(intent);
                } else {
                    //Clear HouseHold UUID from Session for new registration
                    sessionManager.setHouseholdUuid("");

                    Intent intent = new Intent(HomeActivity.this, IdentificationActivity.class);
                    startActivity(intent);
                }*/
            }
        });

        c1_medadvice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentificationActivity.start(HomeActivity.this, true);
            }
        });


        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchPatientActivity.class);
                startActivity(intent);
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, TodayPatientActivity.class);
                startActivity(intent);
            }
        });
        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ActivePatientActivity.class);
                startActivity(intent);
            }
        });
        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoLibrary();
            }
        });


        lastSyncTextView.setText(getString(R.string.last_synced) + " \n" + sessionManager.getLastSyncDateTime());

//        if (!sessionManager.getLastSyncDateTime().equalsIgnoreCase("- - - -")
//                && Locale.getDefault().toString().equalsIgnoreCase("en")) {
////            lastSyncAgo.setText(CalculateAgoTime());
//        }

        manualSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppConstants.notificationUtils.showNotifications(getString(R.string.sync), getString(R.string.syncInProgress), 1, context);

                if (isNetworkConnected()) {
                    getDailyPerformance(getTodayDate(), sessionManager.getChwname(),sessionManager.getProviderPhoneno());
                    Toast.makeText(context, getString(R.string.syncInProgress), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.failed_synced), Toast.LENGTH_LONG).show();
                }

                syncUtils.syncForeground("home");
//                if (!sessionManager.getLastSyncDateTime().equalsIgnoreCase("- - - -")
//                        && Locale.getDefault().toString().equalsIgnoreCase("en")) {
//                    lastSyncAgo.setText(sessionManager.getLastTimeAgo());
//                }
            }
        });
        if (sessionManager.isFirstTimeLaunched()) {
            mSyncProgressDialog = new ProgressDialog(HomeActivity.this, R.style.AlertDialogStyle); //thats how to add a style!
            mSyncProgressDialog.setTitle(R.string.syncInProgress);
            mSyncProgressDialog.setCancelable(false);
            mSyncProgressDialog.setProgress(i);

            mSyncProgressDialog.show();

            syncUtils.initialSync("home");
        } else {
            // if initial setup done then we can directly set the periodic background sync job
            WorkManager.getInstance().enqueueUniquePeriodicWork(AppConstants.UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, AppConstants.PERIODIC_WORK_REQUEST);
        }


        showProgressbar();
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        String date_string = "";
        SimpleDateFormat todaydateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        date_string = todaydateFormat.format(calendar.getTime());
        return  date_string;
    }

    private void getDailyPerformance(String today_date, String chwName, String chwNum) {
        if (!NetworkConnection.isOnline(this)) {
            totalRegTV.setText(getString(R.string.total_reg) + " NA");
            totalSubsTV.setText(getString(R.string.total_sub) + " NA");
            totalCallsTV.setText(getString(R.string.total_called_helpline) + " NA");
            return;
        }

        UrlModifiers urlModifiers = new UrlModifiers();
        ApiInterface apiInterface = AppConstants.apiInterface;
        String encoded = "Basic bnVyc2UxOk51cnNlMTIz";
        check = false;
        apiInterface.getRegistrationNum(urlModifiers.getRegistrationNumUrl(chwName), encoded).enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.body() != null && response.body().data != null && response.body().data.size() > 0) {
                    for (Registrations registrations : response.body().data) {
                            if(!TextUtils.isEmpty(registrations.registered_date) && registrations.registered_date.equals(today_date)) {
                                totalRegTV.setText(getString(R.string.total_reg) + " " + registrations.total_count);
                                check = true;
                                return;
                            }
                            if(check == false)
                                totalRegTV.setText(getString(R.string.total_reg) + " 0");
                    }
                } else {
                    totalRegTV.setText(getString(R.string.total_reg) + " 0");
                }
            }
            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                totalRegTV.setText(getString(R.string.total_reg) + " NA");
                System.out.println(t);
            }
        });

        check = false;
        apiInterface.getSubscriptionNum(urlModifiers.getSubscriptionNumUrl(chwName), encoded).enqueue(new Callback<SubscriptionResponse>() {
            @Override
            public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {
                if (response.body() != null && response.body().data != null && response.body().data.size() > 0) {
                    for (Subscriptions subscriptions : response.body().data) {
                        if(!TextUtils.isEmpty(subscriptions.subscribed_date) && subscriptions.subscribed_date.equals(today_date)) {
                            totalSubsTV.setText(getString(R.string.total_sub) + " " + subscriptions.total_count);
                            check = true;
                            return;
                        }
                        if(check == false)
                            totalSubsTV.setText(getString(R.string.total_sub) + " 0");
                    }
                } else {
                    totalSubsTV.setText(getString(R.string.total_sub) + " 0");
                }
            }

            @Override
            public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                totalSubsTV.setText(getString(R.string.total_sub) + " NA");
                System.out.println(t);
            }
        });

        check = false;
        apiInterface.getCallsNum(urlModifiers.getCallNumUrl(chwNum), encoded).enqueue(new Callback<CallNumResponse>() {
            @Override
            public void onResponse(Call<CallNumResponse> call, Response<CallNumResponse> response) {
                if (response.body() != null && response.body().data != null && response.body().data.size() > 0) {
                    for (CallNums callNums : response.body().data) {
                        if(!TextUtils.isEmpty(callNums.called_date) && callNums.called_date.equals(today_date)) {
                            totalCallsTV.setText(getString(R.string.total_called_helpline) + " " + callNums.total_count);
                            check = true;
                            return;
                        }
                        if(check == false)
                            totalCallsTV.setText(getString(R.string.total_called_helpline) + " 0");
                    }
                } else {
                    totalCallsTV.setText(getString(R.string.total_called_helpline) + " 0");
                }
            }
            @Override
            public void onFailure(Call<CallNumResponse> call, Throwable t) {
                totalCallsTV.setText(getString(R.string.total_called_helpline) + " NA");
                System.out.println(t);
            }
        });
    }

    //function for handling the video library feature...
    private void videoLibrary() {
        if (!sessionManager.getLicenseKey().isEmpty())
            hasLicense = true;
        //Check for license key and load the correct config file
        try {
            JSONObject obj = null;
            if (hasLicense) {
                obj = new JSONObject(Objects.requireNonNullElse
                        (FileUtils.readFileRoot(AppConstants.CONFIG_FILE_NAME, context),
                                String.valueOf(FileUtils.encodeJSON(context, AppConstants.CONFIG_FILE_NAME)))); //Load the config file
            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(this, AppConstants.CONFIG_FILE_NAME)));
            }

            if (obj.has("video_library")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(obj.getString("video_library"));
                intent.setData(uri);
                startActivity(intent);
            } else {
                Toast.makeText(context, "No config attribute found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(getApplicationContext(), "JsonException" + e, Toast.LENGTH_LONG).show();
        }
    }

    private void showProgressbar() {


// instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(HomeActivity.this);
        mProgressDialog.setMessage(getString(R.string.download_protocols));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
    }


    private String CalculateAgoTime() {
        String finalTime = "";

        String syncTime = sessionManager.getLastSyncDateTime();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        long then = formatter.parse(syncTime, pos).getTime();
        long now = new Date().getTime();

        long seconds = (now - then) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String time = "";
        long num = 0;
        if (days > 0) {
            num = days;
            time = days + " " + context.getString(R.string.day);
        } else if (hours > 0) {
            num = hours;
            time = hours + " " + context.getString(R.string.hour);
        } else if (minutes >= 0) {
            num = minutes;
            time = minutes + " " + context.getString(R.string.minute);
        }
//      <For Seconds>
//      else {
//            num = seconds;
//            time = seconds + " second";
//      }
        if (num > 1) {
            time += context.getString(R.string.s);
        }
        finalTime = time + " " + context.getString(R.string.ago);

        sessionManager.setLastTimeAgo(finalTime);

        return finalTime;
    }

    public void setLocale(String appLanguage) {
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        Locale locale = new Locale(appLanguage);
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(locale);
            HomeActivity.this.createConfigurationContext(conf);
        }
        DisplayMetrics dm = res.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            conf.setLocales(new LocaleList(locale));
        } else {
            conf.locale = locale;
        }
        res.updateConfiguration(conf, dm);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.syncOption:
//                refreshDatabases();
//                return true;
            case R.id.settingsOption:
                settings();
                return true;

//            case R.id.performanceOption:
//                Intent intent = new Intent(HomeActivity.this, IVRCallResponseActivity.class);
//                startActivity(intent);
//                return true;
//
//            case R.id.updateProtocolsOption: {
//
//
//                if (NetworkConnection.isOnline(this)) {
//
//                    if (!sessionManager.getLicenseKey().isEmpty()) {
//
//                        String licenseUrl = sessionManager.getMindMapServerUrl();
//                        String licenseKey = sessionManager.getLicenseKey();
//                        getMindmapDownloadURL("https://" + licenseUrl + ":3004/", licenseKey);
//
//                    } else {
////                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
////                    // AlertDialog.Builder dialog = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
////                    LayoutInflater li = LayoutInflater.from(this);
////                    View promptsView = li.inflate(R.layout.dialog_mindmap_cred, null);
////                    dialog.setTitle(getString(R.string.enter_license_key))
////                            .setView(promptsView)
////                            .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////
////                                    Dialog d = (Dialog) dialog;
////
////                                    EditText etURL = d.findViewById(R.id.licenseurl);
////                                    EditText etKey = d.findViewById(R.id.licensekey);
////                                    String url = etURL.getText().toString().replace(" ", "");
////                                    String key = etKey.getText().toString().trim();
////
////                                    if (url.isEmpty()) {
////                                        etURL.setError(getResources().getString(R.string.enter_server_url));
////                                        etURL.requestFocus();
////                                        return;
////                                    }
////                                    if (url.contains(":")) {
////                                        etURL.setError(getResources().getString(R.string.invalid_url));
////                                        etURL.requestFocus();
////                                        return;
////                                    }
////                                    if (key.isEmpty()) {
////                                        etKey.setError(getResources().getString(R.string.enter_license_key));
////                                        etKey.requestFocus();
////                                        return;
////                                    }
////
////                                    sessionManager.setMindMapServerUrl(url); //trim
////                                    getMindmapDownloadURL("https://" + url + ":3004/", key);
////
////                                }
////                            })
////                            .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialog, int which) {
////                                    dialog.dismiss();
////                                }
////                            });
////                    Dialog builderDialog = dialog.show();
////                    IntelehealthApplication.setAlertDialogCustomTheme(this, builderDialog);
////
////                    // }
//
//                        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(this);
//                        LayoutInflater li = LayoutInflater.from(this);
//                        View promptsView = li.inflate(R.layout.dialog_mindmap_cred, null);
//
//                        dialog.setTitle(getString(R.string.enter_license_key))
//                                .setView(promptsView)
//                                .setPositiveButton(getString(R.string.button_ok), null)
//                                .setNegativeButton(getString(R.string.button_cancel), null);
//
//                        AlertDialog alertDialog = dialog.create();
//                        alertDialog.setView(promptsView, 20, 0, 20, 0);
//                        alertDialog.show();
//                        alertDialog.setCanceledOnTouchOutside(false); //dialog wont close when clicked outside...
//
//                        // Get the alert dialog buttons reference
//                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//
//                        // Change the alert dialog buttons text and background color
//                        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
//
//                        positiveButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                EditText text = promptsView.findViewById(R.id.licensekey);
//                                EditText url = promptsView.findViewById(R.id.licenseurl);
//
//                                url.setError(null);
//                                text.setError(null);
//
//                                //If both are not entered...
//                                if (url.getText().toString().trim().isEmpty() && text.getText().toString().trim().isEmpty()) {
//                                    url.requestFocus();
//                                    url.setError(getResources().getString(R.string.enter_server_url));
//                                    text.setError(getResources().getString(R.string.enter_license_key));
//                                    return;
//                                }
//
//                                //If Url is empty...key is not empty...
//                                if (url.getText().toString().trim().isEmpty() && !text.getText().toString().trim().isEmpty()) {
//                                    url.requestFocus();
//                                    url.setError(getResources().getString(R.string.enter_server_url));
//                                    return;
//                                }
//
//                                //If Url is not empty...key is empty...
//                                if (!url.getText().toString().trim().isEmpty() && text.getText().toString().trim().isEmpty()) {
//                                    text.requestFocus();
//                                    text.setError(getResources().getString(R.string.enter_license_key));
//                                    return;
//                                }
//
//                                //If Url has : in it...
//                                if (url.getText().toString().trim().contains(":")) {
//                                    url.requestFocus();
//                                    url.setError(getResources().getString(R.string.invalid_url));
//                                    return;
//                                }
//
//                                //If url entered is Invalid...
//                                if (!url.getText().toString().trim().isEmpty()) {
//                                    if (Patterns.WEB_URL.matcher(url.getText().toString().trim()).matches()) {
//                                        String url_field = "https://" + url.getText().toString() + ":3004/";
//                                        if (URLUtil.isValidUrl(url_field)) {
//                                            key = text.getText().toString().trim();
//                                            licenseUrl = url.getText().toString().trim();
//
//                                            sessionManager.setMindMapServerUrl(licenseUrl);
//
//                                            if (keyVerified(key)) {
//                                                getMindmapDownloadURL("https://" + licenseUrl + ":3004/", key);
//                                                alertDialog.dismiss();
//                                            }
//                                        } else {
//                                            Toast.makeText(HomeActivity.this, getString(R.string.url_invalid), Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    } else {
//                                        //invalid url || invalid url and key.
//                                        Toast.makeText(HomeActivity.this, R.string.invalid_url, Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    Toast.makeText(HomeActivity.this, R.string.please_enter_url_and_key, Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                        negativeButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss();
//                            }
//                        });
//
//
//                        IntelehealthApplication.setAlertDialogCustomTheme(this, alertDialog);
//
//                    }
//
//
//                } else {
//                    Toast.makeText(context, getString(R.string.mindmap_internect_connection), Toast.LENGTH_SHORT).show();
//                }
//
//                return true;
//            }

         /*   case R.id.sync:
//                pullDataDAO.pullData(this);
//                pullDataDAO.pushDataApi();
//                AppConstants.notificationUtils.showNotifications(getString(R.string.sync), getString(R.string.syncInProgress), 1, this);
                boolean isSynced = syncUtils.syncForeground();
//                boolean i = imagesPushDAO.patientProfileImagesPush();
//                boolean o = imagesPushDAO.obsImagesPush();
//                if (isSynced)
//                    AppConstants.notificationUtils.showNotifications_noProgress(getString(R.string.sync_not_available), getString(R.string.please_connect_to_internet), getApplicationContext());
//                else
//                    AppConstants.notificationUtils.showNotifications(getString(R.string.image_upload), getString(R.string.image_upload_failed), 4, this);
                return true;
                */
//            case R.id.backupOption:
//                manageBackup(true, false);  // to backup app data at any time of the day
//                return true;
//
//            case R.id.restoreOption:
//                manageBackup(false, false); // to restore app data if db is empty
//                return true;

            case R.id.logoutOption:
//                manageBackup(true, false);

                MaterialAlertDialogBuilder alertdialogBuilder = new MaterialAlertDialogBuilder(this);
                alertdialogBuilder.setMessage(R.string.sure_to_logout);
                alertdialogBuilder.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                });
                alertdialogBuilder.setNegativeButton(R.string.generic_no, null);
                AlertDialog alertDialog = alertdialogBuilder.create();
                alertDialog.show();
                Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                IntelehealthApplication.setAlertDialogCustomTheme(this, alertDialog);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method starts intent to another activity to change settings
     *
     * @return void
     */
    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Logs out the user. It removes user account using AccountManager.
     *
     * @return void
     */
    public void logout() {

        OfflineLogin.getOfflineLogin().setOfflineLoginStatus(false);

//        parseLogOut();

       /* AccountManager manager = AccountManager.get(HomeActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
     /*   Account[] accountList = manager.getAccountsByType("io.intelehealth.openmrs");
        if (accountList.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                manager.removeAccount(accountList[0], HomeActivity.this, null, null);
            } else {
                manager.removeAccount(accountList[0], null, null); // Legacy implementation
            }
        }
*/
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        SyncUtils syncUtils = new SyncUtils();
        syncUtils.syncBackground();
        sessionManager.setReturningUser(false);
        sessionManager.setLogout(true);
    }


    @Override
    protected void onResume() {
        //IntentFilter filter = new IntentFilter(AppConstants.SYNC_INTENT_ACTION);
        //registerReceiver(syncBroadcastReceiver, filter);
        checkAppVer();  //auto-update feature.
//        lastSyncTextView.setText(getString(R.string.last_synced) + " \n" + sessionManager.getLastSyncDateTime());
        if (!sessionManager.getLastSyncDateTime().equalsIgnoreCase("- - - -")
                && Locale.getDefault().toString().equals("en")) {
//            lastSyncAgo.setText(CalculateAgoTime());
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(AppConstants.SYNC_INTENT_ACTION);
        registerReceiver(syncBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(syncBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private boolean keyVerified(String key) {
        //TODO: Verify License Key
        return true;
    }

    @Override
    public void onBackPressed() {
        /*new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to EXIT ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                        finish();

                    }

                })
                .setNegativeButton("No", null)
                .show();
*/
        MaterialAlertDialogBuilder alertdialogBuilder = new MaterialAlertDialogBuilder(this);

        // AlertDialog.Builder alertdialogBuilder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alertdialogBuilder.setMessage(getResources().getString(R.string.sure_to_exit));
        alertdialogBuilder.setPositiveButton(getResources().getString(R.string.generic_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveTaskToBack(true);
                // finish();
            }
        });
        alertdialogBuilder.setNegativeButton(getResources().getString(R.string.generic_no), null);

        AlertDialog alertDialog = alertdialogBuilder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        //positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        //negativeButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        IntelehealthApplication.setAlertDialogCustomTheme(this, alertDialog);

    }

    private List<Integer> mTempSyncHelperList = new ArrayList<Integer>();
    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.logD("syncBroadcastReceiver", "onReceive! " + intent);

            if (intent != null && intent.hasExtra(AppConstants.SYNC_INTENT_DATA_KEY)) {
                int flagType = intent.getIntExtra(AppConstants.SYNC_INTENT_DATA_KEY, AppConstants.SYNC_FAILED);
                if (sessionManager.isFirstTimeLaunched()) {
                    if (flagType == AppConstants.SYNC_FAILED) {
                        hideSyncProgressBar(false);
                        /*Toast.makeText(context, R.string.failed_synced, Toast.LENGTH_SHORT).show();
                        finish();*/
                        new AlertDialog.Builder(HomeActivity.this)
                                .setMessage(R.string.failed_initial_synced)
                                .setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }

                                }).setCancelable(false)

                                .show();
                    } else {
                        mTempSyncHelperList.add(flagType);
                        if (mTempSyncHelperList.contains(AppConstants.SYNC_PULL_DATA_DONE)
//                                && mTempSyncHelperList.contains(AppConstants.SYNC_PUSH_DATA_DONE)
                                /*&& mTempSyncHelperList.contains(AppConstants.SYNC_PATIENT_PROFILE_IMAGE_PUSH_DONE)
                                && mTempSyncHelperList.contains(AppConstants.SYNC_OBS_IMAGE_PUSH_DONE)*/) {
                            hideSyncProgressBar(true);
                        }
                    }
                }
            }
            lastSyncTextView.setText(getString(R.string.last_synced) + " \n" + sessionManager.getLastSyncDateTime());
        }
    };

    private void hideSyncProgressBar(boolean isSuccess) {
        if (mTempSyncHelperList != null) mTempSyncHelperList.clear();
        if (mSyncProgressDialog != null && mSyncProgressDialog.isShowing()) {
            mSyncProgressDialog.dismiss();
            if (isSuccess) {

                sessionManager.setFirstTimeLaunched(false);
                sessionManager.setMigration(true);
                // initial setup/sync done and now we can set the periodic background sync job
                // given some delay after initial sync
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WorkManager.getInstance().enqueueUniquePeriodicWork(AppConstants.UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, AppConstants.PERIODIC_WORK_REQUEST);
                    }
                }, 10000);
            }
        }
    }

    private void getMindmapDownloadURL(String url, String key) {
        customProgressDialog.show();
        ApiClient.changeApiBaseUrl(url); //trim
        ApiInterface apiService = ApiClient.createService(ApiInterface.class);
        try {
            Observable<DownloadMindMapRes> resultsObservable = apiService.DOWNLOAD_MIND_MAP_RES_OBSERVABLE(key);
            resultsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<DownloadMindMapRes>() {
                        @Override
                        public void onNext(DownloadMindMapRes res) {
                            customProgressDialog.dismiss();
                            if (res.getMessage() != null && res.getMessage().equalsIgnoreCase("Success")) {

                                Log.e("MindMapURL", "Successfully get MindMap URL");
                                mTask = new DownloadMindMaps(context, mProgressDialog);
                                mindmapURL = res.getMindmap().trim();
                                sessionManager.setLicenseKey(key);
                                checkExistingMindMaps();

                            } else {
                                Toast.makeText(context, getResources().getString(R.string.no_protocols_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            customProgressDialog.dismiss();
                            Toast.makeText(context, getResources().getString(R.string.unable_to_get_proper_response), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "changeApiBaseUrl: " + e.getMessage());
            Log.e(TAG, "changeApiBaseUrl: " + e.getStackTrace());
        }
    }

    private void checkExistingMindMaps() {

        //Check is there any existing mindmaps are present, if yes then delete.

        File engines = new File(context.getFilesDir().getAbsolutePath(), "/Engines");
        Log.e(TAG, "Engines folder=" + engines.exists());
        if (engines.exists()) {
            engines.delete();
        }
        File logo = new File(context.getFilesDir().getAbsolutePath(), "/logo");
        Log.e(TAG, "Logo folder=" + logo.exists());
        if (logo.exists()) {
            logo.delete();
        }
        File physicalExam = new File(context.getFilesDir().getAbsolutePath() + "/physExam.json");
        Log.e(TAG, "physExam.json=" + physicalExam.exists());
        if (physicalExam.exists()) {
            physicalExam.delete();
        }
        File familyHistory = new File(context.getFilesDir().getAbsolutePath() + "/famHist.json");
        Log.e(TAG, "famHist.json=" + familyHistory.exists());
        if (familyHistory.exists()) {
            familyHistory.delete();
        }
        File pastMedicalHistory = new File(context.getFilesDir().getAbsolutePath() + "/patHist.json");
        Log.e(TAG, "patHist.json=" + pastMedicalHistory.exists());
        if (pastMedicalHistory.exists()) {
            pastMedicalHistory.delete();
        }
        File config = new File(context.getFilesDir().getAbsolutePath() + "/config.json");
        Log.e(TAG, "config.json=" + config.exists());
        if (config.exists()) {
            config.delete();
        }

        //Start downloading mindmaps
        mTask.execute(mindmapURL, context.getFilesDir().getAbsolutePath() + "/mindmaps.zip");
        Log.e("DOWNLOAD", "isSTARTED");

    }

    private void checkAppVer() {

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        disposable.add((Disposable) AppConstants.apiInterface.checkAppUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<CheckAppUpdateRes>() {
                    @Override
                    public void onSuccess(CheckAppUpdateRes res) {
                        int latestVersionCode = 0;
                        if (!res.getLatestVersionCode().isEmpty()) {
                            latestVersionCode = Integer.parseInt(res.getLatestVersionCode());
                        }

                        if (latestVersionCode > versionCode) {
                            android.app.AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new android.app.AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                            }


                            builder.setTitle(getResources().getString(R.string.new_update_available))
                                    .setCancelable(false)
                                    .setMessage(getResources().getString(R.string.update_app_note))
                                    .setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }

                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setCancelable(false);

                            Dialog dialog = builder.show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                int textViewId = dialog.getContext().getResources().getIdentifier("android:id/alertTitle", null, null);
                                TextView tv = (TextView) dialog.findViewById(textViewId);
                                tv.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error", "" + e);
                    }
                })
        );

    }


}
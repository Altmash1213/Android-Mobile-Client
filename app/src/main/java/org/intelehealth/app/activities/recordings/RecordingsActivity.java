package org.intelehealth.app.activities.recordings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.intelehealth.app.R;
import org.intelehealth.app.app.AppConstants;
import org.intelehealth.app.networkApiCalls.ApiInterface;
import org.intelehealth.app.utilities.SessionManager;
import org.intelehealth.app.utilities.UrlModifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordingsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SessionManager sessionManager = null;
    TextView msg;
    private String TAG = RecordingsActivity.class.getSimpleName();

    public static void start(Context context) {
        Intent starter = new Intent(context, RecordingsActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTheme);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        // Get the intent, verify the action and get the query
        sessionManager = new SessionManager(this);
        String language = sessionManager.getAppLanguage();
        //In case of crash still the app should hold the current lang fix.
        if (!language.equalsIgnoreCase("")) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
        sessionManager.setCurrentLang(getResources().getConfiguration().locale.toString());


        msg = findViewById(R.id.textviewmessage);
        recyclerView = findViewById(R.id.recycle);
        LinearLayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);

        UrlModifiers urlModifiers = new UrlModifiers();
        ApiInterface apiInterface = AppConstants.apiInterface;

        String encoded = "Basic " + sessionManager.getEncoded();
//        apiInterface.getRecordings(urlModifiers.getRecordingListUrl(sessionManager.getProviderPhoneno()), encoded).enqueue(new Callback<RecordingResponse>() {
//            @Override
//            public void onResponse(Call<RecordingResponse> call, Response<RecordingResponse> response) {
//                if (response.body() != null && response.body().data != null && response.body().data.size() > 0) {
//                    List<Recording> recordingList = new ArrayList<>();
//                    for (Recording recording : response.body().data) {
//                        if (!TextUtils.isEmpty(recording.RecordingURL)) {
//                            recordingList.add(recording);
//                        }
//                    }
//                    if (recordingList.size() > 0) {
//                        msg.setVisibility(View.GONE);
//                    } else {
//                        msg.setText(R.string.no_records_found);
//                    }
//                    recyclerView.setAdapter(new RecordingsAdapter(recordingList, RecordingsActivity.this));
//                } else {
//                    msg.setText(R.string.no_records_found);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RecordingResponse> call, Throwable t) {
//                System.out.println(t);
//            }
//        });
//
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.clearOnScrollListeners();
    }
}





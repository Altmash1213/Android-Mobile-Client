package org.intelehealth.app.activities.ivrCallResponseActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.app.R;
import org.intelehealth.app.networkApiCalls.ApiClient;
import org.intelehealth.app.utilities.NetworkConnection;
import org.intelehealth.app.utilities.SessionManager;
import org.intelehealth.app.utilities.UrlModifiers;
import org.intelehealth.app.widget.materialprogressbar.CustomProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
public class IVRCallResponseActivity extends AppCompatActivity {
    Context context;
    SessionManager sessionManager;
    RecyclerView recyclerView;
    IVRCallResponse_Adapter adapter;
    Call_Details_Response response;
    TextView total_count_textview;
    CustomProgressDialog customProgressDialog;
    String todayDate_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ivrcall_response);
        setTitle(getResources().getString(R.string.Daily_Performance_Activity_Title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = IVRCallResponseActivity.this;
        sessionManager = new SessionManager(context);
        customProgressDialog = new CustomProgressDialog(context);

        Log.v("main", "provider_no: " + sessionManager.getProviderPhoneno());

        recyclerView = findViewById(R.id.ivr_response_recyclerview);
        total_count_textview = findViewById(R.id.total_count_textview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        SimpleDateFormat todaydateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
        Calendar today = Calendar.getInstance();
        Date todayDate = today.getTime();
        todayDate_string = todaydateFormat.format(todayDate);
        getIVR_Call_Response(sessionManager.getProviderPhoneno(), todayDate_string);

    }

    private void getIVR_Call_Response(String providerNo, String fromDate) {
        if (!NetworkConnection.isOnline(this)) {
            customProgressDialog.dismiss();
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_LONG).show();
            return;
        }

        customProgressDialog.show();
        response = new Call_Details_Response();
        ApiClient.changeApiBaseUrl("https://api-voice.kaleyra.com");
        UrlModifiers urlModifiers = new UrlModifiers();

//        String url = urlModifiers.getIvrCall_ResponseUrl(providerNo, fromDate);
//        Logger.logD("main", "ivr call response url" + url);
//        Observable<Call_Details_Response> patientIvrCall_response =
//                ApiClient.createService(ApiInterface.class).IVR_CALL_RESPONSE(url);
//        patientIvrCall_response
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Call_Details_Response>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onNext(Call_Details_Response call_details_response) {
//                        response = call_details_response;
//                        total_count_textview.setText(getResources().getString(R.string.total_calls) + call_details_response.getData().size());
//                        adapter = new IVRCallResponse_Adapter(context, response);
//
//                        if (response.getData() != null) {
//                            customProgressDialog.dismiss();
//                            recyclerView.setAdapter(adapter);
//                        } else {
//                            customProgressDialog.dismiss();
//                            Toast.makeText(context, getResources().getString(R.string.something_wrong_refresh_again), Toast.LENGTH_SHORT).show();
//                        }
//
//                        Log.v("main", "call_ivr_response: " + call_details_response);
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        customProgressDialog.dismiss();
//                        Log.v("main", "call_ivr_response_error: " + e.getLocalizedMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        customProgressDialog.dismiss();
//                        Log.v("main", "call_ivr_response_onComplete(): ");
//                    }
//                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_sync, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_sync:
                getIVR_Call_Response(sessionManager.getProviderPhoneno(), todayDate_string);
                return true;

            case R.id.action_calendar:
                getCalendarPicker();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCalendarPicker() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date_string = "";
                SimpleDateFormat todaydateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                date_string = todaydateFormat.format(calendar.getTime());

                getIVR_Call_Response(sessionManager.getProviderPhoneno(), date_string);
            }
        }, year, month, day);

        datePickerDialog.show();

        Button positiveButton = datePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = datePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

}
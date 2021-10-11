package org.intelehealth.ekalhelpline.activities.todayPatientActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.intelehealth.ekalhelpline.R;
import org.intelehealth.ekalhelpline.activities.activePatientsActivity.ActivePatientActivity;
import org.intelehealth.ekalhelpline.app.AppConstants;
import org.intelehealth.ekalhelpline.app.IntelehealthApplication;
import org.intelehealth.ekalhelpline.database.InteleHealthDatabaseHelper;
import org.intelehealth.ekalhelpline.database.dao.EncounterDAO;
import org.intelehealth.ekalhelpline.database.dao.ProviderDAO;
import org.intelehealth.ekalhelpline.database.dao.VisitsDAO;
import org.intelehealth.ekalhelpline.models.ActivePatientModel;
import org.intelehealth.ekalhelpline.models.TodayPatientModel;
import org.intelehealth.ekalhelpline.models.dto.EncounterDTO;
import org.intelehealth.ekalhelpline.models.dto.VisitDTO;
import org.intelehealth.ekalhelpline.utilities.Logger;
import org.intelehealth.ekalhelpline.utilities.SessionManager;
import org.intelehealth.ekalhelpline.activities.homeActivity.HomeActivity;
import org.intelehealth.ekalhelpline.utilities.StringUtils;
import org.intelehealth.ekalhelpline.utilities.exception.DAOException;

public class TodayPatientActivity extends AppCompatActivity {
    private static final String TAG = TodayPatientActivity.class.getSimpleName();
    InteleHealthDatabaseHelper mDbHelper;
    private SQLiteDatabase db;
    SessionManager sessionManager = null;
    RecyclerView mTodayPatientList;
    MaterialAlertDialogBuilder dialogBuilder;
    TextView no_records_found_textview;
    String date_string = "";

    private ArrayList<String> listPatientUUID = new ArrayList<String>();
    int limit = 20, offset = 0;
    boolean fullyLoaded = false;
    private TodayPatientAdapter mActivePatientAdapter;
    String user_data = "", chw_name = "";
    ProviderDAO providerDAO = new ProviderDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_patient);
        setTitle(getString(R.string.title_activity_today_patient));
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_today_patient);

        Toolbar toolbar = findViewById(R.id.toolbar);

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.ic_sort_white_24dp);
//        toolbar.setOverflowIcon(drawable);


        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTheme);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mTodayPatientList = findViewById(R.id.today_patient_recycler_view);
        no_records_found_textview = findViewById(R.id.no_records_found_textview);

        LinearLayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        mTodayPatientList.setLayoutManager(reLayoutManager);
        chw_name = sessionManager.getProviderID();

        mTodayPatientList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mActivePatientAdapter.todayPatientModelList != null && mActivePatientAdapter.todayPatientModelList.size() < limit) {
                    return;
                }
                if (!fullyLoaded && newState == RecyclerView.SCROLL_STATE_IDLE &&
                        reLayoutManager.findLastVisibleItemPosition() == mActivePatientAdapter.getItemCount() - 1) {
                    Toast.makeText(TodayPatientActivity.this, R.string.loading_more, Toast.LENGTH_SHORT).show();
                    offset += limit;

                    List<TodayPatientModel> allPatientsFromDB = doQuery(offset, chw_name);
                    List<TodayPatientModel> todayvisit_speciality = todayVisit_speciality(offset, chw_name);
                    List<TodayPatientModel> todayvisit_exitsurveycomments = getExitSurvey_Comments(offset);

                    if (allPatientsFromDB.size() < limit) {
                        fullyLoaded = true;
                    }

                    mActivePatientAdapter.todayPatientModelList.addAll(allPatientsFromDB);
                    mActivePatientAdapter.todayPatient_Speciality.addAll(todayvisit_speciality);
                    mActivePatientAdapter.todayPatient_exitsurvey_commentsList.addAll(todayvisit_exitsurveycomments);
                    mActivePatientAdapter.notifyDataSetChanged();
                }
            }
        });

        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        if (sessionManager.isPullSyncFinished()) {

            List<TodayPatientModel> todayPatientModels = doQuery(offset, chw_name);
            List<TodayPatientModel> todayVisit_Speciality = todayVisit_speciality(offset, chw_name); //get the speciality.
            List<TodayPatientModel> todayModel_ExitSurveyComments = getExitSurvey_Comments(offset); //fetch the value of the COMMENTS of ExitSurvey screen
            //to check for TLD Closed or TLD Resolved... This will only come in Todays Visits and not in Active Visits.

            mActivePatientAdapter = new TodayPatientAdapter(todayPatientModels, this,
                    listPatientUUID, todayModel_ExitSurveyComments, todayVisit_Speciality);
            mTodayPatientList.setAdapter(mActivePatientAdapter);
        }

        getVisits();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTodayPatientList.clearOnScrollListeners();
    }

    private void getVisits() {

        ArrayList<String> encounterVisitUUID = new ArrayList<String>();
        HashSet<String> hsPatientUUID = new HashSet<String>();

        //Get all Visits
        VisitsDAO visitsDAO = new VisitsDAO();
        List<VisitDTO> visitsDTOList = visitsDAO.getAllVisits();

        //Get all Encounters
        EncounterDAO encounterDAO = new EncounterDAO();
        List<EncounterDTO> encounterDTOList = encounterDAO.getAllEncounters();

        //Get Visit Complete Encounters only, visit complete encounter id - bd1fbfaa-f5fb-4ebd-b75c-564506fc309e
        if (encounterDTOList.size() > 0) {
            for (int i = 0; i < encounterDTOList.size(); i++) {
                if (encounterDTOList.get(i).getEncounterTypeUuid().equalsIgnoreCase("bd1fbfaa-f5fb-4ebd-b75c-564506fc309e")) {
                    encounterVisitUUID.add(encounterDTOList.get(i).getVisituuid()); //all ended visits are added in this list...
                }
            }
        }

        //Get patientUUID from visitList
        for (int i = 0; i < encounterVisitUUID.size(); i++) {

            for (int j = 0; j < visitsDTOList.size(); j++) {

                if (encounterVisitUUID.get(i).equalsIgnoreCase(visitsDTOList.get(j).getUuid())) {
                    listPatientUUID.add(visitsDTOList.get(j).getPatientuuid());
                }
            }
        }

        if (listPatientUUID.size() > 0) {

            hsPatientUUID.addAll(listPatientUUID);
            listPatientUUID.clear();
            listPatientUUID.addAll(hsPatientUUID);

        }
    }

    private List<TodayPatientModel> todayVisit_speciality(int offset, String user_data_) {
        List<TodayPatientModel> todayPatientList = new ArrayList<>();
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cDate);
        String query = "SELECT DISTINCT a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.first_name, b.middle_name, b.last_name, b.date_of_birth,b.openmrs_id, d.value " +
                "FROM tbl_visit a, tbl_patient b, tbl_visit_attribute d, tbl_encounter x, tbl_provider y " +
                "WHERE a.patientuuid = b.uuid AND a.uuid = d.visit_uuid AND d.visit_uuid = x.visituuid AND x.provider_uuid = y.uuid " +
                "AND a.startdate LIKE '" + currentDate + "T%' AND d.visit_attribute_type_uuid = '3f296939-c6d3-4d2e-b8ca-d7f4bfd42c2d' AND y.uuid = ? " +
                "limit ? offset ?";
        Logger.logD(TAG, "\n today_specilaity: "+query);

        final Cursor cursor = db.rawQuery(query, new String[]{user_data_, String.valueOf(limit), String.valueOf(offset)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        todayPatientList.add(new TodayPatientModel(
                                cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("startdate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("openmrs_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("middle_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                                StringUtils.mobileNumberEmpty(phoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")))),
                                cursor.getString(cursor.getColumnIndexOrThrow("sync")),
                                cursor.getString(cursor.getColumnIndexOrThrow("value")))
                        );
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        for (TodayPatientModel todayPatientModel : todayPatientList) {
            Log.v("main", "todaysPatient: " + todayPatientModel.getFirst_name() + " " +
                    todayPatientModel.getLast_name() + " " + todayPatientModel.getVisit_speciality() + "\n");
        }
        return todayPatientList;
    }


    private List<TodayPatientModel> doQuery(int offset, String user_uuid) {
        List<TodayPatientModel> todayPatientList = new ArrayList<>();
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cDate);
        String query = "SELECT a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.first_name, b.middle_name, b.last_name, b.date_of_birth,b.openmrs_id " +
                "FROM tbl_visit a, tbl_patient b, tbl_encounter c, tbl_provider d " +
                "WHERE b.uuid = a.patientuuid AND a.uuid = c.visituuid AND c.provider_uuid = d.uuid " +
                "AND a.startdate LIKE '" + currentDate + "T%' AND d.uuid = ? " +
                "GROUP BY a.uuid ORDER BY a.patientuuid ASC limit ? offset ?";
        Logger.logD(TAG, "today_doquery: " + query);

        final Cursor cursor = db.rawQuery(query, new String[]{user_uuid, String.valueOf(limit), String.valueOf(offset)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        todayPatientList.add(new TodayPatientModel(
                                cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("startdate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("openmrs_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("middle_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                                StringUtils.mobileNumberEmpty(phoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")))),
                                cursor.getString(cursor.getColumnIndexOrThrow("sync")))
                        );
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

//        if (!todayPatientList.isEmpty()) {
//            for (TodayPatientModel todayPatientModel : todayPatientList)
//                Log.i(TAG, todayPatientModel.getFirst_name() + " " + todayPatientModel.getLast_name());
//
//            TodayPatientAdapter mTodayPatientAdapter = new TodayPatientAdapter(todayPatientList, TodayPatientActivity.this, listPatientUUID);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TodayPatientActivity.this);
//            mTodayPatientList.setLayoutManager(linearLayoutManager);
//           /* mTodayPatientList.addItemDecoration(new
//                    DividerItemDecoration(TodayPatientActivity.this,
//                    DividerItemDecoration.VERTICAL));*/
//            mTodayPatientList.setAdapter(mTodayPatientAdapter);
//        }

        for (TodayPatientModel todayPatientModel : todayPatientList) {
            Log.v("main", "todaysPatient: " + todayPatientModel.getFirst_name() + " " +
                    todayPatientModel.getLast_name() + " " + todayPatientModel.getVisit_speciality() + "\n");
        }
        return todayPatientList;
    }

    public static long getTodayVisitsCount(SQLiteDatabase db, String chwUser) {
        int count =0;
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cDate);
        String query = "SELECT DISTINCT a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.first_name, b.middle_name, b.last_name, b.date_of_birth,b.openmrs_id " +
                "FROM tbl_visit a, tbl_patient b, tbl_encounter c, tbl_provider d " +
                "WHERE a.patientuuid = b.uuid AND a.uuid = c.visituuid AND c.provider_uuid = d.uuid " +
                "AND a.startdate LIKE '" + currentDate + "T%' AND d.uuid = ? ";
        Logger.logD(TAG, "count_hi: " +query + "chw" + chwUser);
        final Cursor cursor = db.rawQuery(query, new String[]{chwUser});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    count++;
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return count;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
       // inflater.inflate(R.menu.menu_today_patient, menu);
        inflater.inflate(R.menu.today_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.summary_endAllVisit:
                endAllVisit();

            case R.id.action_filter:
                //alert box.
                displaySingleSelectionDialog();    //function call


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void displaySingleSelectionDialog() {
        ArrayList selectedItems = new ArrayList<>();
        String[] creator_names = null;
        String[] creator_uuid = null;
        try {
            creator_names = providerDAO.getProvidersList().toArray(new String[0]);
            creator_uuid = providerDAO.getProvidersUuidList().toArray(new String[0]);
        } catch (DAOException e) {
            e.printStackTrace();
        }
//        boolean[] checkedItems = {false, false, false, false};
        // ngo_numbers = getResources().getStringArray(R.array.ngo_numbers);
        dialogBuilder = new MaterialAlertDialogBuilder(TodayPatientActivity.this);
        dialogBuilder.setTitle(R.string.filter_by_creator);

        String[] finalCreator_names = creator_names;
        String[] finalCreator_uuid = creator_uuid;
        dialogBuilder.setMultiChoiceItems(creator_names, null, new DialogInterface.OnMultiChoiceClickListener() {


            @Override
            public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
                Logger.logD(TAG, "multichoice" + which + isChecked);
                if (isChecked) {
                    // If the user checked the item, add it to the selected items
                    selectedItems.add(finalCreator_uuid[which]);
                    Logger.logD(TAG, finalCreator_names[which] + finalCreator_uuid[which]);

                } else if (selectedItems.contains(which)) {
                    // Else, if the item is already in the array, remove it
                    selectedItems.remove(finalCreator_uuid[which]);
                    Logger.logD(TAG, finalCreator_names[which] + finalCreator_uuid[which]);
                }
            }
        });

        dialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //display filter query code on list menu
                Logger.logD(TAG, "onclick" + i);
                getCalendarPicker(selectedItems);
            }
        });

        dialogBuilder.setNegativeButton(R.string.generic_cancel, null);
//        dialogBuilder.show();

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

        Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));

      //  IntelehealthApplication.setAlertDialogCustomTheme(TodayPatientActivity.this, alertDialog);
    }

    private void doQueryWithProviders(List<String> providersuuids, String date) {
        List<TodayPatientModel> todayPatientList = new ArrayList<>();
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cDate);
        String query = "SELECT  distinct a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.first_name, b.middle_name, b.last_name, b.date_of_birth,b.openmrs_id " +
                "FROM tbl_visit a, tbl_patient b, tbl_encounter c " +
                "WHERE a.patientuuid = b.uuid " +
                "AND c.visituuid=a.uuid and c.provider_uuid in ('" + StringUtils.convertUsingStringBuilder(providersuuids) + "') " +
                "AND a.startdate LIKE '" + date + "%' " +
                "ORDER BY a.patientuuid ASC ";
        Logger.logV("main", query);
        final Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        todayPatientList.add(new TodayPatientModel(
                                cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("startdate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("openmrs_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("middle_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                                StringUtils.mobileNumberEmpty(phoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")))),
                                cursor.getString(cursor.getColumnIndexOrThrow("sync"))));
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        TodayPatientAdapter mTodayPatientAdapter;
        LinearLayoutManager linearLayoutManager;
        List<TodayPatientModel> speciality_list = getSpeciality_Filter(providersuuids, date);
        List<TodayPatientModel> exitsurvey_comments_list = getExitSurvey_Filter(providersuuids, date);

        if (!todayPatientList.isEmpty()) {
            for (TodayPatientModel todayPatientModel : todayPatientList)
                Log.i(TAG, todayPatientModel.getFirst_name() + " " + todayPatientModel.getLast_name() + " " +
                        todayPatientModel.getVisit_speciality());

             mTodayPatientAdapter = new TodayPatientAdapter(todayPatientList, TodayPatientActivity.this,
                     listPatientUUID, exitsurvey_comments_list, speciality_list);
            no_records_found_textview.setVisibility(View.GONE);
        }
        else {
             mTodayPatientAdapter = new TodayPatientAdapter(todayPatientList, TodayPatientActivity.this, listPatientUUID,
                     exitsurvey_comments_list, speciality_list);
             no_records_found_textview.setVisibility(View.VISIBLE);
             no_records_found_textview.setHint(R.string.no_records_found);
        }

        linearLayoutManager = new LinearLayoutManager(TodayPatientActivity.this);
        mTodayPatientList.setLayoutManager(linearLayoutManager);
       /* mTodayPatientList.addItemDecoration(new
                DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));*/
        mTodayPatientList.setAdapter(mTodayPatientAdapter);
        mTodayPatientAdapter.notifyDataSetChanged(); //since, again we are updating...

    }

    private List<TodayPatientModel> getExitSurvey_Filter(List<String> providersuuids, String date) {
        List<TodayPatientModel> todayPatientList = new ArrayList<>();
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cDate);
        String query = "SELECT distinct a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.first_name, b.middle_name, b.last_name, b.date_of_birth,b.openmrs_id, d.value, f.value as obsvalue " +
                "FROM tbl_visit a, tbl_patient b, tbl_encounter c, tbl_visit_attribute d, tbl_obs f " +
                "WHERE a.patientuuid = b.uuid AND a.uuid = d.visit_uuid AND f.conceptuuid = '36d207d6-bee7-4b3e-9196-7d053c6eddce' AND c.uuid = f.encounteruuid " +
                "AND c.visituuid=a.uuid and c.provider_uuid in ('" + StringUtils.convertUsingStringBuilder(providersuuids) + "')  " +
                "AND a.startdate LIKE '" + date + "%' " +
                "ORDER BY a.patientuuid ASC ";
        Logger.logD(TAG, query);
        final Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        todayPatientList.add(new TodayPatientModel(
                                cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("startdate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("openmrs_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("middle_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                                StringUtils.mobileNumberEmpty(phoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")))),
                                cursor.getString(cursor.getColumnIndexOrThrow("sync")),
                                cursor.getString(cursor.getColumnIndexOrThrow("value")),
                                cursor.getString(cursor.getColumnIndexOrThrow("obsvalue"))));
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return todayPatientList;
    }

    private List<TodayPatientModel> getSpeciality_Filter(List<String> providersuuids, String date) {
        List<TodayPatientModel> todayPatientList = new ArrayList<>();
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cDate);
        String query = "SELECT distinct a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.first_name, b.middle_name, b.last_name, b.date_of_birth,b.openmrs_id, d.value " +
                "FROM tbl_visit a, tbl_patient b, tbl_encounter c, tbl_visit_attribute d " +
                "WHERE a.patientuuid = b.uuid AND a.uuid = d.visit_uuid " +
                "AND c.visituuid=a.uuid and c.provider_uuid in ('" + StringUtils.convertUsingStringBuilder(providersuuids) + "')  " +
                "AND a.startdate LIKE '" + date + "%' " +
                "ORDER BY a.patientuuid ASC ";
        Logger.logD(TAG, query);
        final Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        todayPatientList.add(new TodayPatientModel(
                                cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("startdate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("openmrs_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("middle_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                                StringUtils.mobileNumberEmpty(phoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")))),
                                cursor.getString(cursor.getColumnIndexOrThrow("sync")),
                                cursor.getString(cursor.getColumnIndexOrThrow("value"))));
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return todayPatientList;
    }


    private void endAllVisit() {

        int failedUploads = 0;

        String query = "SELECT tbl_visit.patientuuid, tbl_visit.enddate, tbl_visit.uuid," +
                "tbl_patient.first_name, tbl_patient.middle_name, tbl_patient.last_name FROM tbl_visit, tbl_patient WHERE" +
                " tbl_visit.patientuuid = tbl_patient.uuid AND tbl_visit.enddate IS NULL OR tbl_visit.enddate = ''";

        final Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    boolean result = endVisit(
                            cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")),
                            cursor.getString(cursor.getColumnIndexOrThrow("first_name")) + " " +
                                    cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("uuid"))
                    );
                    if (!result) failedUploads++;
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        if (failedUploads == 0) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
            alertDialogBuilder.setMessage(getString(R.string.unable_to_end) + failedUploads +
                    getString(R.string.upload_before_end_visit_active));
            alertDialogBuilder.setNeutralButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            IntelehealthApplication.setAlertDialogCustomTheme(TodayPatientActivity.this, alertDialog);
        }

    }

    private boolean endVisit(String patientID, String patientName, String visitUUID) {

        return visitUUID != null;

    }

    private String phoneNumber(String patientuuid) throws DAOException {
        String phone = null;
        Cursor idCursor = db.rawQuery("SELECT value  FROM tbl_patient_attribute where patientuuid = ? AND person_attribute_type_uuid='14d4f066-15f5-102d-96e4-000c29c2a5d7' ", new String[]{patientuuid});
        try {
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {

                    phone = idCursor.getString(idCursor.getColumnIndexOrThrow("value"));

                }
            }
        } catch (SQLException s) {
            FirebaseCrashlytics.getInstance().recordException(s);
        }
        idCursor.close();

        return phone;
    }

    private List<TodayPatientModel> getExitSurvey_Comments(int offset) {
        List<TodayPatientModel> todayPatientList = new ArrayList<>();
        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(cDate);
        String query = "SELECT a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.first_name, b.middle_name, b.last_name, b.date_of_birth,b.openmrs_id, d.value, f.value as obsvalue " +
                "FROM tbl_visit a, tbl_patient b, tbl_visit_attribute d, tbl_encounter e, tbl_obs f " +
                "WHERE a.patientuuid = b.uuid AND a.uuid = d.visit_uuid AND f.conceptuuid = '36d207d6-bee7-4b3e-9196-7d053c6eddce' AND a.uuid = e.visituuid AND e.uuid = f.encounteruuid " +
                "AND a.startdate LIKE '" + currentDate + "T%'   " +
                "limit ? offset ?";
        Logger.logD(TAG, "\n today_exit: " +query);

        final Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit), String.valueOf(offset)});

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        todayPatientList.add(new TodayPatientModel(
                                cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")),
                                cursor.getString(cursor.getColumnIndexOrThrow("startdate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("enddate")),
                                cursor.getString(cursor.getColumnIndexOrThrow("openmrs_id")),
                                cursor.getString(cursor.getColumnIndexOrThrow("first_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("middle_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("last_name")),
                                cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                                StringUtils.mobileNumberEmpty(phoneNumber(cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")))),
                                cursor.getString(cursor.getColumnIndexOrThrow("sync")),
                                cursor.getString(cursor.getColumnIndexOrThrow("value")),
                                cursor.getString(cursor.getColumnIndexOrThrow("obsvalue")))
                        );
                    } catch (DAOException e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        for (TodayPatientModel todayPatientModel : todayPatientList) {
            Log.v("main", "todaysPatient: " + todayPatientModel.getFirst_name() + " " +
                    todayPatientModel.getLast_name() + " " + todayPatientModel.getVisit_speciality() + "\n");
        }

        return todayPatientList;
    }

    private void getCalendarPicker(ArrayList selectedItems_data) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TodayPatientActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        SimpleDateFormat todaydateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        date_string = todaydateFormat.format(calendar.getTime());
                        Log.v("date", "picker_active: "+ date_string);

                        doQueryWithProviders(selectedItems_data, date_string);

                    }
                }, year, month, day);

        datePickerDialog.show();

        Button positiveButton = datePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = datePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }



}





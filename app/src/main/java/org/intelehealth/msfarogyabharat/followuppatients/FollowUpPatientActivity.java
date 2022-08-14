package org.intelehealth.msfarogyabharat.followuppatients;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.intelehealth.msfarogyabharat.R;
import org.intelehealth.msfarogyabharat.app.AppConstants;
import org.intelehealth.msfarogyabharat.models.FollowUpModel;
import org.intelehealth.msfarogyabharat.models.dto.PatientDTO;
import org.intelehealth.msfarogyabharat.utilities.Logger;
import org.intelehealth.msfarogyabharat.utilities.SessionManager;
import org.intelehealth.msfarogyabharat.utilities.StringUtils;
import org.intelehealth.msfarogyabharat.utilities.UuidDictionary;
import org.intelehealth.msfarogyabharat.utilities.exception.DAOException;
import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.intelehealth.msfarogyabharat.utilities.DateAndTimeUtils.getCurrentDate;
import static org.intelehealth.msfarogyabharat.utilities.DateAndTimeUtils.mGetDaysAccording;
import static org.intelehealth.msfarogyabharat.utilities.DateAndTimeUtils.olderThan10Days;

public class FollowUpPatientActivity extends AppCompatActivity {
    //    SearchView searchView;
    String query;
    private FollowUpPatientAdapter recycler;
    RecyclerView recyclerView;
    SessionManager sessionManager = null;
    TextView msg;
    MaterialAlertDialogBuilder dialogBuilder;
    private String TAG = FollowUpPatientActivity.class.getSimpleName();
    private SQLiteDatabase db;
    //    FloatingActionButton new_patient;
    int limit = Integer.MAX_VALUE, offset = 0;
//    boolean fullyLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_patient);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTheme);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        msg = findViewById(R.id.textviewmessage);
        recyclerView = findViewById(R.id.recycle);
        LinearLayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(reLayoutManager);

        if (sessionManager.isPullSyncFinished()) {
            msg.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            firstQuery();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void firstQuery() {
        try {
            recycler = new FollowUpPatientAdapter(getAllPatientsFromDB(offset), FollowUpPatientActivity.this);
            recyclerView.setAdapter(recycler);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Logger.logE("firstquery", "exception", e);
        }
    }

    public List<FollowUpModel> getAllPatientsFromDB(int offset) {
        String visitType = "General";
        List<FollowUpModel> modelList = new ArrayList<FollowUpModel>();
        String table = "tbl_patient";
        Date cDate = new Date();
        FollowUpModel model = new FollowUpModel();
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(cDate);
//        String newQuery = "SELECT v.enddate FROM tbl_patient a, tbl_visit b where a.uuid = b.patientuuid";
//        String query = "SELECT a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.uuid, b.first_name, b.middle_name, b.last_name, b.date_of_birth, b.openmrs_id, c.value AS speciality, o.value FROM tbl_visit a, tbl_patient b, tbl_encounter d, tbl_obs o, tbl_visit_attribute c WHERE a.uuid = c.visit_uuid AND  a.enddate is NOT NULL AND a.patientuuid = b.uuid AND a.uuid = d.visituuid AND d.uuid = o.encounteruuid AND o.conceptuuid in ('e8caffd6-5d22-41c4-8d6a-bc31a44d0c86', 'e1761e85-9b50-48ae-8c4d-e6b7eeeba084') AND o.value is NOT NULL ORDER BY startdate DESC";
        String query = "SELECT * from (SELECT a.uuid, a.sync, a.patientuuid, a.startdate, a.enddate, b.uuid, b.first_name, b.middle_name, b.last_name, b.date_of_birth, b.openmrs_id, c.value AS speciality, o.value FROM tbl_visit a, tbl_patient b, tbl_encounter d, tbl_obs o, tbl_visit_attribute c WHERE a.uuid = c.visit_uuid AND  a.enddate is NOT NULL AND a.patientuuid = b.uuid AND a.uuid = d.visituuid AND d.uuid = o.encounteruuid AND o.conceptuuid in ('e8caffd6-5d22-41c4-8d6a-bc31a44d0c86', (Select conceptuuid from tbl_obs Where d.uuid = encounteruuid AND value like '%Do you want us to follow-up? - Yes%')) AND o.value is NOT NULL ORDER BY startdate DESC) as sub GROUP BY patientuuid";
//        String query = "SELECT * FROM tbl_patient as p where p.uuid in (select v.patientuuid from tbl_visit as v where v.enddate like '%Sep 12, 2021%' or v.uuid in (select e.visituuid from tbl_encounter as e where e.uuid in (select o.encounteruuid from tbl_obs as o where o.conceptuuid = ? and o.value like '%"+ currentDate +"%')))";
        final Cursor searchCursor = db.rawQuery(query, null);
        if (searchCursor.moveToFirst()) {
            do {
                try {
                    String visitStartDateFollowup = searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate"));
                    String visitFollowup = "";
                    if(searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")).contains(" Do you want us to follow-up? - Yes")){
                        visitType = "Diabetes Follow-up";
                        visitFollowup = searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")).substring(68, 79);
                        visitFollowup = visitFollowup.replaceAll("/","-");
                        Date requiredFormat =  new SimpleDateFormat("dd-MMM-yyyy").parse(visitFollowup);
                        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        visitFollowup = outputDateFormat.format(requiredFormat);
                    }
                    else {
                        visitFollowup = searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")).substring(0, 10);
                    }


                    SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    Date startDate = sd1.parse(visitStartDateFollowup);
                    Date followUp = new SimpleDateFormat("dd-MM-yyyy").parse(visitFollowup);
                    String newStartDate = new SimpleDateFormat("dd-MM-yyyy").format(startDate);
                    Date currentD = new SimpleDateFormat("dd-MM-yyyy").parse(currentDate);
                    int value = followUp.compareTo(currentD);

                    if(visitType.equalsIgnoreCase("Diabetes Follow-up"))
                    {
                        if (value == -1 || value == 0) {
                            modelList.add(new FollowUpModel(
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                    StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                    visitFollowup,
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                    "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid"))),
                                    "" + searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate")), value));
                        }
                    }
                    else
                    {
                        String mSeverityValue = getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")));
                        int days = mGetDaysAccording(newStartDate);
                        String mValue = "";
                        if(!mSeverityValue.contains("Do you want us to follow-up?"))
                        {
                            String[] arrSplit_2 = mSeverityValue.split("-");
                            mValue = arrSplit_2[arrSplit_2.length - 1];
                        }
                        if (value == -1) {

                            if (days > 0 && days < 11) {
                                Log.d("mSeverityValue", "mSeverityValue++ " + mSeverityValue);
                                Log.d("days", "days++ " + days);

                                if (days % 2 == 0) {
                                    if (mValue.trim().equalsIgnoreCase("Mild.") || mValue.trim().equalsIgnoreCase("Moderate.") || mValue.trim().contains("Moderate.") || mValue.trim().contains("Mild.")) {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid"))),
                                                "" + searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate")), value
                                        ));

                                    }
                                    else if (mValue.trim().contains("Severe.")||mValue.trim().equalsIgnoreCase("Severe.")) {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")))
                                                , "" + searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate")), value));


                                    }
                                    else {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                                visitFollowup,
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid"))),
                                                "" + searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate")), value));

                                    }
                                }
                                else {
                                    if (mValue.trim().contains("Severe.")||mValue.trim().equalsIgnoreCase("Severe.")) {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")))
                                                , "" + searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate")), value));

                                    }
                                    else {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                                visitFollowup,
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid"))),
                                                "" + searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate")), value));

                                    }
                                }

                            }
                            else {
                                modelList.add(new FollowUpModel(
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                        StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                        visitFollowup,
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                        searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")),
                                        "" + searchCursor.getString(searchCursor.getColumnIndexOrThrow("startdate")), value));

                            }
                        }
                        else if (value > 0) {

                            if (days > 0 && days < 11 && days != 0) {
                                Log.d("mSeverityValue", "mSeverityValue++ " + mSeverityValue);
                                Log.d("days", "days++ " + days);

                                if (days % 2 == 0) {
                                    if (mValue.trim().equalsIgnoreCase("Mild.") || mValue.trim().equalsIgnoreCase("Moderate.") || mValue.trim().contains("Moderate.") || mValue.trim().contains("Mild.")) {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
//                                          searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")),
                                                "null",
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid"))),
                                                startDate.toString(), value));


                                    }
                                    else if (mValue.trim().contains("Severe.") || mValue.trim().equalsIgnoreCase("Severe.")) {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
//                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")),
                                                "null",
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid"))),
                                                startDate.toString(), value));

                                    }
                                    else {
// todo No need to added
                                    }
                                }
                                else {
                                    if (mValue.trim().contains("Severe.") || mValue.trim().equalsIgnoreCase("Severe.")) {
                                        modelList.add(new FollowUpModel(
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                                StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
//                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("value")),
                                                "null",
                                                searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                                "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid"))),
                                                startDate.toString(), value));

                                    }

                                }

                            }
                        }
                        else {
                            modelList.add(new FollowUpModel(
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")),
                                    StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))),
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("speciality")),
                                    visitFollowup,
                                    searchCursor.getString(searchCursor.getColumnIndexOrThrow("sync")),
                                    "" + getSeverity(searchCursor.getString(searchCursor.getColumnIndexOrThrow("patientuuid"))),
                                    startDate.toString(), value));

                        }
                    }

                }

                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            while (searchCursor.moveToNext());
        }
        searchCursor.close();
//        try {
//            if (searchCursor.moveToFirst()) {
//                do {
//                    model.setOpenmrs_id(searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")));
//                    model.setFirst_name(searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")));
//                    model.setLast_name(searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")));
//
//                    model.setUuid(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")));
//                    model.setDate_of_birth(searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")));
//                    model.setPatientuuid(StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))));
//                    model.comment = getSeverity(model.getUuid());
//                    if (model.comment == null)
//                        continue;
//                    modelList.add(model);
//                } while (searchCursor.moveToNext());
//            }
//            searchCursor.close();
//
//            Collections.sort(modelList, new Comparator<FollowUpModel>() {
//                @Override
//                public int compare(FollowUpModel p1, FollowUpModel p2) {
//                    return p2.getSeverity() - p1.getSeverity();
//                }
//            });
//        } catch (DAOException e) {
//            e.printStackTrace();
//        }
        return modelList;

    }

    public List<PatientDTO> getQueryPatients(String query) {
        String search = query.trim().replaceAll("\\s", "");
        // search = StringUtils.mobileNumberEmpty(phoneNumber());
        List<PatientDTO> modelList = new ArrayList<PatientDTO>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWritableDatabase();
        String table = "tbl_patient";

        List<String> patientUUID_List = new ArrayList<>();

        final Cursor search_mobile_cursor = db.rawQuery("SELECT DISTINCT patientuuid FROM tbl_patient_attribute WHERE value = ?",
                new String[]{search});
        /* DISTINCT will get remove the duplicate values. The duplicate value will come when you have created
         * a patient with mobile no. 12345 and patient is pushed than later you edit the mobile no to
         * 12344 or something. In this case, the local db maintains two separate rows both with value: 12344 */

        //if no data is present against that corresponding cursor than cursor count returns = 0 ... i.e cursor_count = 0 ...
        try {
            if (search_mobile_cursor.moveToFirst()) {
                do {
                    patientUUID_List.add(search_mobile_cursor.getString
                            (search_mobile_cursor.getColumnIndexOrThrow("patientuuid")));
                }
                while (search_mobile_cursor.moveToNext());
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        Log.d("patientUUID_list", "list: " + patientUUID_List.toString());

        if (patientUUID_List.size() != 0) {
            for (int i = 0; i < patientUUID_List.size(); i++) {

                final Cursor searchCursor = db.rawQuery("SELECT * FROM " + table + " WHERE first_name LIKE " + "'%" + search + "%' OR middle_name LIKE '%" + search + "%' OR uuid = ? OR last_name LIKE '%" + search + "%' OR (first_name || middle_name) LIKE '%" + search + "%' OR (middle_name || last_name) LIKE '%" + search + "%' OR (first_name || last_name) LIKE '%" + search + "%' OR openmrs_id LIKE '%" + search + "%' " + "ORDER BY first_name ASC",
                        new String[]{patientUUID_List.get(i)});
                //  if(searchCursor.getCount() != -1) { //all values are present as per the search text entered...
                try {
                    if (searchCursor.moveToFirst()) {
                        do {
                            PatientDTO model = new PatientDTO();
                            model.setOpenmrsId(searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")));
                            model.setFirstname(searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")));
                            model.setLastname(searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")));
                            model.setOpenmrsId(searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")));
                            model.setMiddlename(searchCursor.getString(searchCursor.getColumnIndexOrThrow("middle_name")));
                            model.setUuid(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")));
                            model.setDateofbirth(searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")));
                            model.setPhonenumber(StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))));
                            model.comment = getSeverity(model.getUuid());
                            modelList.add(model);
                        } while (searchCursor.moveToNext());
                    }
                } catch (DAOException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

            }

        } else {
            final Cursor searchCursor = db.rawQuery("SELECT * FROM " + table + " WHERE first_name LIKE " + "'%" + search + "%' OR middle_name LIKE '%" + search + "%' OR last_name LIKE '%" + search + "%' OR (first_name || middle_name) LIKE '%" + search + "%' OR (middle_name || last_name) LIKE '%" + search + "%' OR (first_name || last_name) LIKE '%" + search + "%' OR openmrs_id LIKE '%" + search + "%' " + "ORDER BY first_name ASC",
                    null);
            //  if(searchCursor.getCount() != -1) { //all values are present as per the search text entered...
            try {
                if (searchCursor.moveToFirst()) {
                    do {
                        PatientDTO model = new PatientDTO();
                        model.setOpenmrsId(searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")));
                        model.setFirstname(searchCursor.getString(searchCursor.getColumnIndexOrThrow("first_name")));
                        model.setLastname(searchCursor.getString(searchCursor.getColumnIndexOrThrow("last_name")));
                        model.setOpenmrsId(searchCursor.getString(searchCursor.getColumnIndexOrThrow("openmrs_id")));
                        model.setMiddlename(searchCursor.getString(searchCursor.getColumnIndexOrThrow("middle_name")));
                        model.setUuid(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")));
                        model.setDateofbirth(searchCursor.getString(searchCursor.getColumnIndexOrThrow("date_of_birth")));
                        model.setPhonenumber(StringUtils.mobileNumberEmpty(phoneNumber(searchCursor.getString(searchCursor.getColumnIndexOrThrow("uuid")))));
                        model.comment = getSeverity(model.getUuid());
                        modelList.add(model);
                    } while (searchCursor.moveToNext());
                }
            } catch (DAOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }

        }

        return modelList;

    }

    private String getSeverity(String patientUid) {
        String severity = null;
        final Cursor obsCursor = db.rawQuery("select o.value from tbl_obs as o where o.conceptuuid = ? and encounteruuid in (select e.uuid from tbl_encounter as e where e.visituuid in (select v.uuid from tbl_visit as v where v.patientuuid = ?))", new String[]{UuidDictionary.PHYSICAL_EXAMINATION, patientUid});
        if (obsCursor.moveToFirst()) {
            do {
                severity = obsCursor.getString(obsCursor.getColumnIndexOrThrow("value"));
            } while (obsCursor.moveToNext());
            obsCursor.close();
        }
        return severity;
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

    @Override
    protected void onStop() {
        super.onStop();
    }
}





package org.intelehealth.app.activities.patientDetailActivity;

import static org.intelehealth.app.utilities.DialogUtils.patientRegistrationDialog;
import static org.intelehealth.app.utilities.StringUtils.en__as_dob;
import static org.intelehealth.app.utilities.StringUtils.en__bn_dob;
import static org.intelehealth.app.utilities.StringUtils.en__gu_dob;
import static org.intelehealth.app.utilities.StringUtils.en__hi_dob;
import static org.intelehealth.app.utilities.StringUtils.en__kn_dob;
import static org.intelehealth.app.utilities.StringUtils.en__ml_dob;
import static org.intelehealth.app.utilities.StringUtils.en__mr_dob;
import static org.intelehealth.app.utilities.StringUtils.en__or_dob;
import static org.intelehealth.app.utilities.StringUtils.en__ru_dob;
import static org.intelehealth.app.utilities.StringUtils.en__ta_dob;
import static org.intelehealth.app.utilities.StringUtils.en__te_dob;
import static org.intelehealth.app.utilities.StringUtils.switch_as_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_as_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_as_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_bn_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_bn_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_bn_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_gu_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_gu_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_gu_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_hi_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_hi_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_hi_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_kn_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_kn_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_kn_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ml_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ml_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ml_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_mr_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_mr_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_mr_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_or_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_or_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_or_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ru_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ru_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ru_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ta_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ta_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_ta_education_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_te_caste_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_te_economic_edit;
import static org.intelehealth.app.utilities.StringUtils.switch_te_education_edit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.intelehealth.app.R;
import org.intelehealth.app.activities.homeActivity.HomeScreenActivity_New;
import org.intelehealth.app.activities.identificationActivity.IdentificationActivity;
import org.intelehealth.app.activities.identificationActivity.IdentificationActivity_New;
import org.intelehealth.app.activities.vitalActivity.VitalsActivity;
import org.intelehealth.app.app.AppConstants;
import org.intelehealth.app.database.InteleHealthDatabaseHelper;
import org.intelehealth.app.database.dao.EncounterDAO;
import org.intelehealth.app.database.dao.ImagesDAO;
import org.intelehealth.app.database.dao.PatientsDAO;
import org.intelehealth.app.database.dao.VisitsDAO;
import org.intelehealth.app.models.Patient;
import org.intelehealth.app.models.dto.EncounterDTO;
import org.intelehealth.app.models.dto.PatientDTO;
import org.intelehealth.app.models.dto.VisitDTO;
import org.intelehealth.app.utilities.DateAndTimeUtils;
import org.intelehealth.app.utilities.DownloadFilesUtils;
import org.intelehealth.app.utilities.FileUtils;
import org.intelehealth.app.utilities.Logger;
import org.intelehealth.app.utilities.NetworkConnection;
import org.intelehealth.app.utilities.SessionManager;
import org.intelehealth.app.utilities.UrlModifiers;
import org.intelehealth.app.utilities.UuidDictionary;
import org.intelehealth.app.utilities.exception.DAOException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PatientDetailActivity2 extends AppCompatActivity {
    private static final String TAG = PatientDetailActivity2.class.getSimpleName();
    TextView name_txtview, openmrsID_txt, patientname, gender, patientdob, patientage, phone, 
            postalcode, patientcountry, patientstate, patientdistrict, village, address1,
            son_daughter_wife, patientoccupation, patientcaste, patienteducation, patienteconomicstatus;
    SessionManager sessionManager = null;
    Patient patient_new = new Patient();
    PatientsDAO patientsDAO = new PatientsDAO();
    private boolean hasLicense = false;
    SQLiteDatabase db = null;
    private PatientDTO patientDTO;
    String profileImage = "";
    String profileImage1 = "";
    Context context;
    String patientName, mGender;
    ImagesDAO imagesDAO = new ImagesDAO();
    float float_ageYear_Month;
    ImageView profile_image, personal_edit, address_edit, others_edit;
    Myreceiver reMyreceive;
    IntentFilter filter;
    Button startVisitBtn;
    EncounterDTO encounterDTO;
    private boolean returning;
    private String encounterAdultIntials = "";
    String phistory = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail2);
        sessionManager = new SessionManager(this);
        String language = sessionManager.getAppLanguage();
        context = PatientDetailActivity2.this;

        //In case of crash still the org should hold the current lang fix.
        if (!language.equalsIgnoreCase("")) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        filter = new IntentFilter("OpenmrsID");
        reMyreceive = new Myreceiver();


        // changing status bar color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle args = intent.getBundleExtra("BUNDLE");
            patientDTO = (PatientDTO) args.getSerializable("patientDTO");
        }

        initUI();
        setDisplay(patientDTO.getUuid());

        personal_edit.setOnClickListener(v -> {
            Intent intent2 = new Intent(PatientDetailActivity2.this, IdentificationActivity_New.class);
            intent2.putExtra("patientUuid", patientDTO.getUuid());
            intent2.putExtra("patient_detail", "personal_edit");

            Bundle args = new Bundle();
            args.putSerializable("patientDTO", (Serializable) patientDTO);
            intent2.putExtra("BUNDLE",args);
            startActivity(intent2);
        });

        address_edit.setOnClickListener(v -> {
            Intent intent2 = new Intent(PatientDetailActivity2.this, IdentificationActivity_New.class);
            intent2.putExtra("patientUuid", patientDTO.getUuid());
            intent2.putExtra("patient_detail", "address_edit");

            Bundle args = new Bundle();
            args.putSerializable("patientDTO", (Serializable) patientDTO);
            intent2.putExtra("BUNDLE",args);
            startActivity(intent2);
        });

        others_edit.setOnClickListener(v -> {
            Intent intent2 = new Intent(PatientDetailActivity2.this, IdentificationActivity_New.class);
            intent2.putExtra("patientUuid", patientDTO.getUuid());
            intent2.putExtra("patient_detail", "others_edit");

            Bundle args = new Bundle();
            args.putSerializable("patientDTO", (Serializable) patientDTO);
            intent2.putExtra("BUNDLE",args);
            startActivity(intent2);
        });



        startVisitBtn.setOnClickListener(v -> {
            startVisitDialog(context,
                    getResources().getDrawable(R.drawable.dialog_icon_complete),
                    "Patient Registered!",
                    "Does the patient want to start the visit now?",
                    "Continue",
                    "Cancel");
        });
    }

    private void initUI() {
        profile_image = findViewById(R.id.profile_image);
        name_txtview = findViewById(R.id.name_txtview);
        openmrsID_txt = findViewById(R.id.openmrsID_txt);

        patientname = findViewById(R.id.name);
        gender = findViewById(R.id.gender);
        patientdob = findViewById(R.id.dob);
        patientage = findViewById(R.id.age);
        phone = findViewById(R.id.phone);

        postalcode = findViewById(R.id.postalcode);
        patientcountry = findViewById(R.id.country);
        patientstate = findViewById(R.id.state);
        patientdistrict = findViewById(R.id.district);
        village = findViewById(R.id.village);
        address1 = findViewById(R.id.address1);

        son_daughter_wife = findViewById(R.id.son_daughter_wife);
        patientoccupation = findViewById(R.id.occupation);
        patientcaste = findViewById(R.id.caste);
        patienteducation = findViewById(R.id.education);
        patienteconomicstatus = findViewById(R.id.economicstatus);

        personal_edit = findViewById(R.id.personal_edit);
        address_edit = findViewById(R.id.address_edit);
        others_edit = findViewById(R.id.others_edit);

        startVisitBtn = findViewById(R.id.startVisitBtn);
    }

    public void setDisplay(String dataString) {

        String patientSelection = "uuid = ?";
        String[] patientArgs = {dataString};
        String[] patientColumns = {"uuid", "openmrs_id", "first_name", "middle_name", "last_name", "gender",
                "date_of_birth", "address1", "address2", "city_village", "state_province",
                "postal_code", "country", "phone_number", "gender", "sdw",
                "patient_photo"};
        Cursor idCursor = db.query("tbl_patient", patientColumns, patientSelection, patientArgs, null, null, null);
        if (idCursor.moveToFirst()) {
            do {
                patient_new.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                patient_new.setOpenmrs_id(idCursor.getString(idCursor.getColumnIndexOrThrow("openmrs_id")));
                patient_new.setFirst_name(idCursor.getString(idCursor.getColumnIndexOrThrow("first_name")));
                patient_new.setMiddle_name(idCursor.getString(idCursor.getColumnIndexOrThrow("middle_name")));
                patient_new.setLast_name(idCursor.getString(idCursor.getColumnIndexOrThrow("last_name")));
                patient_new.setGender(idCursor.getString(idCursor.getColumnIndexOrThrow("gender")));
                patient_new.setDate_of_birth(idCursor.getString(idCursor.getColumnIndexOrThrow("date_of_birth")));
                patient_new.setAddress1(idCursor.getString(idCursor.getColumnIndexOrThrow("address1")));
                patient_new.setAddress2(idCursor.getString(idCursor.getColumnIndexOrThrow("address2")));
                patient_new.setCity_village(idCursor.getString(idCursor.getColumnIndexOrThrow("city_village")));
                patient_new.setState_province(idCursor.getString(idCursor.getColumnIndexOrThrow("state_province")));
                patient_new.setPostal_code(idCursor.getString(idCursor.getColumnIndexOrThrow("postal_code")));
                patient_new.setCountry(idCursor.getString(idCursor.getColumnIndexOrThrow("country")));
                patient_new.setPhone_number(idCursor.getString(idCursor.getColumnIndexOrThrow("phone_number")));
                patient_new.setGender(idCursor.getString(idCursor.getColumnIndexOrThrow("gender")));
                patient_new.setPatient_photo(idCursor.getString(idCursor.getColumnIndexOrThrow("patient_photo")));
            } while (idCursor.moveToNext());
        }
        idCursor.close();

        String patientSelection1 = "patientuuid = ?";
        String[] patientArgs1 = {dataString};
        String[] patientColumns1 = {"value", "person_attribute_type_uuid"};
        Cursor idCursor1 = db.query("tbl_patient_attribute", patientColumns1, patientSelection1, patientArgs1, null, null, null);
        String name = "";
        if (idCursor1.moveToFirst()) {
            do {
                try {
                    name = patientsDAO.getAttributesName(idCursor1.getString(idCursor1.getColumnIndexOrThrow("person_attribute_type_uuid")));
                } catch (DAOException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

                if (name.equalsIgnoreCase("caste")) {
                    patient_new.setCaste(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
                }
                if (name.equalsIgnoreCase("Telephone Number")) {
                    patient_new.setPhone_number(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
                }
                if (name.equalsIgnoreCase("Education Level")) {
                    patient_new.setEducation_level(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
                }
                if (name.equalsIgnoreCase("Economic Status")) {
                    patient_new.setEconomic_status(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
                }
                if (name.equalsIgnoreCase("occupation")) {
                    patient_new.setOccupation(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
                }
                if (name.equalsIgnoreCase("Son/wife/daughter")) {
                    patient_new.setSdw(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
                }
                if (name.equalsIgnoreCase("ProfileImageTimestamp")) {
                    profileImage1 = idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"));
                }

            } while (idCursor1.moveToNext());
        }
        idCursor1.close();
        
        if (!sessionManager.getLicenseKey().isEmpty()) {
            hasLicense = true;
        }

        try {
            JSONObject obj = null;
            if (hasLicense) {
                obj = new JSONObject(Objects.requireNonNullElse
                        (FileUtils.readFileRoot(AppConstants.CONFIG_FILE_NAME, context),
                                String.valueOf(FileUtils.encodeJSON(context, AppConstants.CONFIG_FILE_NAME)))); //Load the config file
            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(this, AppConstants.CONFIG_FILE_NAME)));
            }

            //Display the fields on the Add Patient screen as per the config file
            // todo: uncomment later and hadnle this case.
         /*   if (obj.getBoolean("casteLayout")) {
                casteRow.setVisibility(View.VISIBLE);
            } else {
                casteRow.setVisibility(View.GONE);
            }
            if (obj.getBoolean("educationLayout")) {
                educationRow.setVisibility(View.VISIBLE);
            } else {
                educationRow.setVisibility(View.GONE);
            }
            if (obj.getBoolean("economicLayout")) {
                economicRow.setVisibility(View.VISIBLE);
            } else {
                economicRow.setVisibility(View.GONE);
            }
*/
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
//            Issue #627
//            added the catch exception to check the config and throwing back to setup activity
            Toast.makeText(getApplicationContext(), "JsonException" + e, Toast.LENGTH_LONG).show();
        }

        //changing patient to patient_new object
        if (patient_new.getMiddle_name() == null) {
            patientName = patient_new.getFirst_name() + " " + patient_new.getLast_name();
        } else {
            patientName = patient_new.getFirst_name() + " " + patient_new.getMiddle_name() + " " + patient_new.getLast_name();
        }
        
        // setting patient name to the name textviews.
        name_txtview.setText(patientName);
        patientname.setText(patientName);
        
       
        // setting profile image of patient
        try {
            profileImage = imagesDAO.getPatientProfileChangeTime(patientDTO.getUuid());
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

            if (patient_new.getPatient_photo() == null || patient_new.getPatient_photo().equalsIgnoreCase("")) {
                if (NetworkConnection.isOnline(getApplication())) {
                    profilePicDownloaded();
                }
            }
            if (!profileImage.equalsIgnoreCase(profileImage1)) {
                if (NetworkConnection.isOnline(getApplication())) {
                    profilePicDownloaded();
                }
            }
            Glide.with(this)
                    .load(patient_new.getPatient_photo())
                    .thumbnail(0.3f)
                    .centerCrop()
                    .error(R.drawable.avatar1)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(profile_image);

        // setting openmrs id
        if (patient_new.getOpenmrs_id() != null && !patient_new.getOpenmrs_id().isEmpty()) {
            openmrsID_txt.setText(patient_new.getOpenmrs_id());
        } else {
            openmrsID_txt.setText(getString(R.string.patient_not_registered));
        }

       // setTitle(patient_new.getOpenmrs_id());
        
        // setting age
        String age = DateAndTimeUtils.getAgeInYearMonth(patient_new.getDate_of_birth(), context);
        patientage.setText(age);
        float_ageYear_Month = DateAndTimeUtils.getFloat_Age_Year_Month(patient_new.getDate_of_birth());

        // setting date of birth
        String dob = DateAndTimeUtils.getFormatedDateOfBirthAsView(patient_new.getDate_of_birth());
        if (sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
            String dob_text = en__hi_dob(dob); //to show text of English into Hindi...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
            String dob_text = en__or_dob(dob); //to show text of English into Odiya...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
            String dob_text = en__bn_dob(dob); //to show text of English into Odiya...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
            String dob_text = en__gu_dob(dob); //to show text of English into Gujarati...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
            String dob_text = en__te_dob(dob); //to show text of English into telugu...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
            String dob_text = en__mr_dob(dob); //to show text of English into telugu...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
            String dob_text = en__as_dob(dob); //to show text of English into telugu...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
            String dob_text = en__ml_dob(dob); //to show text of English into telugu...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
            String dob_text = en__kn_dob(dob); //to show text of English into telugu...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
            String dob_text = en__ru_dob(dob); //to show text of English into Russian...
            patientdob.setText(dob_text);
        } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
            String dob_text = en__ta_dob(dob); //to show text of English into Tamil...
            patientdob.setText(dob_text);
        } else {
            patientdob.setText(dob);
        }
        
        // setting gender
        mGender = patient_new.getGender();
        if (patient_new.getGender() == null || patient_new.getGender().equals("")) {
            gender.setVisibility(View.GONE);
        } else {
            if (sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                } else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                } else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                } else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                } else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                } else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                }else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                } else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                } else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                }else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }  else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                }else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                } else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                }else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                } else {
                    gender.setText(patient_new.getGender());
                }
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
                if (patient_new.getGender().equalsIgnoreCase("M")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_male));
                } else if (patient_new.getGender().equalsIgnoreCase("F")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_female));
                } else if (patient_new.getGender().equalsIgnoreCase("Other")) {
                    gender.setText(getResources().getString(R.string.identification_screen_checkbox_other));
                }else {
                    gender.setText(patient_new.getGender());
                }
            } else {
                gender.setText(patient_new.getGender());
            }
        }
        
        // setting address1
        if (patient_new.getAddress1() == null || patient_new.getAddress1().equals("")) {
          //  address1.setVisibility(View.GONE);
            address1.setText("No address added");
        } else {
            address1.setText(patient_new.getAddress1());
        }
       /* if (patient_new.getAddress2() == null || patient_new.getAddress2().equals("")) { // todo: as per figma not needed.
            addr2Row.setVisibility(View.GONE);
        } else {
            addr2View.setText(patient_new.getAddress2());
        }*/

        // setting country
        String country;
        if (patient_new.getCountry() != null) {
            country = patient_new.getCountry().trim();
        } else {
            country = "No country added";
        }
        patientcountry.setText(country);
        
        // setting state
        String state;
        if (patient_new.getState_province() != null) {
            state = patient_new.getState_province().trim();
        } else {
            state = "No state added";
        }
        patientstate.setText(state);

        // setting district and city
        String[] district_city = patient_new.getCity_village().trim().split(":");
        String district = district_city[0];
        String city_village = district_city[1];
        
        if (district != null) {
            patientdistrict.setText(district);
        } else {
            patientdistrict.setText("No district added");
        }
        
        if (city_village != null) {
            village.setText(city_village);
        } else {
            village.setText("No city added");
        }
        // end - city and district
        
        // setting postal code
        if (patient_new.getPostal_code() != null) {
            postalcode.setText(patient_new.getPostal_code());
        } else {
            postalcode.setText("No postal code added");
        }

        // setting phone number
        phone.setText(patient_new.getPhone_number());

        // setting education status
        if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
            patienteducation.setText("नहीं दिया गया");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
            patienteducation.setText("ଦିଅ ଯାଇ ନାହିଁ");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
            patienteducation.setText("પૂરી પાડવામાં આવેલ નથી");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
            patienteducation.setText("సమకూర్చబడలేదు");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
            patienteducation.setText("झाले नाही");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
            patienteducation.setText("প্ৰদান কৰা হোৱা নাই");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
            patienteducation.setText("നൽകിയിട്ടില്ല");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
            patienteducation.setText("ಒದಗಿಸಲಾಗಿಲ್ಲ");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
            patienteducation.setText("Не предоставлен");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
            patienteducation.setText("সরবরাহ করা হয়নি");
        } else if (patient_new.getEducation_level().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
            patienteducation.setText("வழங்கப்படவில்லை");
        } else {
            if (sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
                String education = switch_hi_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
                String education = switch_or_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
                String education = switch_ta_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
                String education = switch_te_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
                String education = switch_mr_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
                String education = switch_as_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
                String education = switch_ml_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
                String education = switch_kn_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
                String education = switch_ru_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
                String education = switch_gu_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
                String education = switch_bn_education_edit(patient_new.getEducation_level());
                patienteducation.setText(education);
            } else {
                patienteducation.setText(patient_new.getEducation_level());
            }
        }

        // setting economic status
        if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
            patienteconomicstatus.setText("नहीं दिया गया");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
            patienteconomicstatus.setText("ଦିଅ ଯାଇ ନାହିଁ");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
            patienteconomicstatus.setText("வழங்கப்படவில்லை");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
            patienteconomicstatus.setText("પૂરી પાડવામાં આવેલ નથી");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
            patienteconomicstatus.setText("సమకూర్చబడలేదు");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
            patienteconomicstatus.setText("झाले नाही");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
            patienteconomicstatus.setText("প্ৰদান কৰা হোৱা নাই");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
            patienteconomicstatus.setText("നൽകിയിട്ടില്ല");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
            patienteconomicstatus.setText("ಒದಗಿಸಲಾಗಿಲ್ಲ");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
            patienteconomicstatus.setText("Не предоставлен");
        } else if (patient_new.getEconomic_status().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
            patienteconomicstatus.setText("সরবরাহ করা হয়নি");
        } else {
            patienteconomicstatus.setText(patient_new.getEconomic_status());
            if (sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
                String economic = switch_hi_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
                String economic = switch_or_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
                String economic = switch_ta_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
                String economic = switch_bn_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
                String economic = switch_gu_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
                String economic = switch_te_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
                String economic = switch_mr_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
                String economic = switch_as_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
                String economic = switch_ml_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
                String economic = switch_kn_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
                String economic = switch_ru_economic_edit(patient_new.getEconomic_status());
                patienteconomicstatus.setText(economic);
            } else {
                patienteconomicstatus.setText(patient_new.getEconomic_status());
            }
        }

        // setting caste value
        if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
            patientcaste.setText("नहीं दिया गया");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
            patientcaste.setText("ଦିଅ ଯାଇ ନାହିଁ");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
            patientcaste.setText("సమకూర్చబడలేదు");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
            patientcaste.setText("झाले नाही");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
            patientcaste.setText("প্ৰদান কৰা হোৱা নাই");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
            patientcaste.setText("നൽകിയിട്ടില്ല");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
            patientcaste.setText("ಒದಗಿಸಲಾಗಿಲ್ಲ");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
            patientcaste.setText("Не предоставлен");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
            patientcaste.setText("પૂરી પાડવામાં આવેલ નથી");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
            patientcaste.setText("সরবরাহ করা হয়নি");
        } else if (patient_new.getCaste().equalsIgnoreCase("Not provided") &&
                sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
            patientcaste.setText("வழங்கப்படவில்லை");
        } else {
            patientcaste.setText(patient_new.getCaste());
            if (sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
                String caste = switch_hi_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("or")) {
                String caste = switch_or_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("gu")) {
                String caste = switch_gu_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("te")) {
                String caste = switch_te_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("mr")) {
                String caste = switch_mr_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("as")) {
                String caste = switch_as_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ml")) {
                String caste = switch_ml_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("kn")) {
                String caste = switch_kn_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ru")) {
                String caste = switch_ru_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("bn")) {
                String caste = switch_bn_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else if (sessionManager.getAppLanguage().equalsIgnoreCase("ta")) {
                String caste = switch_ta_caste_edit(patient_new.getCaste());
                patientcaste.setText(caste);
            } else {
                patientcaste.setText(patient_new.getCaste());
            }
        }

        // setting son/daughet_wife value
        if (patient_new.getSdw() != null && !patient_new.getSdw().equals("")) {
            son_daughter_wife.setText(patient_new.getSdw());
        } else {
            son_daughter_wife.setVisibility(View.GONE);
        }

        // setting occupation value
        if (patient_new.getOccupation() != null && !patient_new.getOccupation().equals("")) {
            patientoccupation.setText(patient_new.getOccupation());
        } else {
            patientoccupation.setText("");
        }
    }

    // profile pic download
    public void profilePicDownloaded() {
        UrlModifiers urlModifiers = new UrlModifiers();
        String url = urlModifiers.patientProfileImageUrl(patientDTO.getUuid());
        Logger.logD(TAG, "profileimage url" + url);
        Observable<ResponseBody> profilePicDownload = AppConstants.apiInterface.PERSON_PROFILE_PIC_DOWNLOAD(url, "Basic " + sessionManager.getEncoded());
        profilePicDownload.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody file) {
                        DownloadFilesUtils downloadFilesUtils = new DownloadFilesUtils();
                        downloadFilesUtils.saveToDisk(file, patientDTO.getUuid());
                        Logger.logD(TAG, file.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.logD(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Logger.logD(TAG, "complete" + patient_new.getPatient_photo());
                        PatientsDAO patientsDAO = new PatientsDAO();
                        boolean updated = false;
                        try {
                            updated = patientsDAO.updatePatientPhoto(patientDTO.getUuid(), AppConstants.IMAGE_PATH + patientDTO.getUuid() + ".jpg");
                        } catch (DAOException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                        if (updated) {
                            Glide.with(PatientDetailActivity2.this)
                                    .load(AppConstants.IMAGE_PATH + patientDTO.getUuid() + ".jpg")
                                    .thumbnail(0.3f)
                                    .centerCrop()
                                    .error(R.drawable.avatar1)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(profile_image);
                        }
                        ImagesDAO imagesDAO = new ImagesDAO();
                        boolean isImageDownloaded = false;
                        try {
                            isImageDownloaded = imagesDAO.insertPatientProfileImages(AppConstants.IMAGE_PATH +
                                    patientDTO.getUuid() + ".jpg", patientDTO.getUuid());
                        } catch (DAOException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    }
                });
    }

    // Receiver class for Openmrs ID
    public class Myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                openmrsID_txt.setText(patientsDAO.getOpenmrsId(patientDTO.getUuid()));

            } catch (DAOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
            setTitle(openmrsID_txt.getText());
        }
    }

    @Override
    protected void onStart() {
        registerReceiver(reMyreceive, filter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(reMyreceive);
        super.onDestroy();
    }

    // Dialog show
    public void startVisitDialog(Context context, Drawable drawable, String title, String subTitle,
                                 String positiveBtnTxt, String negativeBtnTxt) {
        MaterialAlertDialogBuilder alertdialogBuilder = new MaterialAlertDialogBuilder(context);
        final LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.dialog_patient_registration, null);
        alertdialogBuilder.setView(convertView);
        ImageView icon = convertView.findViewById(R.id.dialog_icon);
        TextView dialog_title = convertView.findViewById(R.id.dialog_title);
        TextView dialog_subtitle = convertView.findViewById(R.id.dialog_subtitle);
        Button positive_btn = convertView.findViewById(R.id.positive_btn);
        Button negative_btn = convertView.findViewById(R.id.negative_btn);

        icon.setImageDrawable(drawable);
        dialog_title.setText(title);
        dialog_subtitle.setText(subTitle);
        positive_btn.setText(positiveBtnTxt);
        negative_btn.setText(negativeBtnTxt);


        AlertDialog alertDialog = alertdialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.ui2_rounded_corners_dialog_bg); // show rounded corner for the dialog
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);   // dim backgroun
        int width = context.getResources().getDimensionPixelSize(R.dimen.internet_dialog_width);    // set width to your dialog.
        alertDialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);

        negative_btn.setOnClickListener(v -> {
            alertDialog.dismiss();

        });

        positive_btn.setOnClickListener(v -> {
            checkVisitOrStartNewVisit();
        });

        alertDialog.show();
    }

    private void checkVisitOrStartNewVisit() {
        // before starting, we determine if it is new visit for a returning patient
        // extract both FH and PMH
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);


        String uuid = UUID.randomUUID().toString();
        EncounterDAO encounterDAO = new EncounterDAO();
        encounterDTO = new EncounterDTO();
        encounterDTO.setUuid(UUID.randomUUID().toString());
        encounterDTO.setEncounterTypeUuid(encounterDAO.getEncounterTypeUuid("ENCOUNTER_VITALS"));
        encounterDTO.setEncounterTime(thisDate);
        encounterDTO.setVisituuid(uuid);
        encounterDTO.setSyncd(false);
        encounterDTO.setProvideruuid(sessionManager.getProviderID());
        Log.d("DTO", "DTO:detail " + encounterDTO.getProvideruuid());
        encounterDTO.setVoided(0);
     //   encounterDTO.setPrivacynotice_value(privacy_value_selected);//privacy value added. // TODO: handle later.

        try {
            encounterDAO.createEncountersToDB(encounterDTO);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        InteleHealthDatabaseHelper mDatabaseHelper = new InteleHealthDatabaseHelper(PatientDetailActivity2.this);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        String CREATOR_ID = sessionManager.getCreatorID();
        returning = false;
        sessionManager.setReturning(returning);

        String[] cols = {"value"};
        // querying for PMH (Past Medical History)
        Cursor cursor = sqLiteDatabase.query("tbl_obs", cols, "encounteruuid=? and conceptuuid=?",
                new String[]{encounterAdultIntials, UuidDictionary.RHK_MEDICAL_HISTORY_BLURB},
                null, null, null);

        if (cursor.moveToFirst()) {
            // rows present
            do {
                // so that null data is not appended
                phistory = phistory + cursor.getString(0);

            }
            while (cursor.moveToNext());
            returning = true;
            sessionManager.setReturning(returning);
        }
        cursor.close();

//                Cursor cursor1 = sqLiteDatabase.query("tbl_obs", cols, "encounteruuid=? and conceptuuid=?",// querying for FH (Family History)
//                        new String[]{encounterAdultIntials, UuidDictionary.RHK_FAMILY_HISTORY_BLURB},
//                        null, null, null);
//                if (cursor1.moveToFirst()) {
//                    // rows present
//                    do {
//                        fhistory = fhistory + cursor1.getString(0);
//                    }
//                    while (cursor1.moveToNext());
//                    returning = true;
//                    sessionManager.setReturning(returning);
//                }
//                cursor1.close();

        // Will display data for patient as it is present in database
        // Toast.makeText(PatientDetailActivity.this,"PMH: "+phistory,Toast.LENGTH_SHORT).sƒhow();
        // Toast.makeText(PatientDetailActivity.this,"FH: "+fhistory,Toast.LENGTH_SHORT).show();

        Intent intent2 = new Intent(PatientDetailActivity2.this, VitalsActivity.class);
        String fullName = patient_new.getFirst_name() + " " + patient_new.getLast_name();
        intent2.putExtra("patientUuid", patientDTO.getUuid());

        VisitDTO visitDTO = new VisitDTO();

        visitDTO.setUuid(uuid);
        visitDTO.setPatientuuid(patient_new.getUuid());
        visitDTO.setStartdate(thisDate);
        visitDTO.setVisitTypeUuid(UuidDictionary.VISIT_TELEMEDICINE);
        visitDTO.setLocationuuid(sessionManager.getLocationUuid());
        visitDTO.setSyncd(false);
        visitDTO.setCreatoruuid(sessionManager.getCreatorID());//static
        VisitsDAO visitsDAO = new VisitsDAO();

        try {
            visitsDAO.insertPatientToDB(visitDTO);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        // visitUuid = String.valueOf(visitLong);
//                localdb.close();
        intent2.putExtra("patientUuid", patientDTO.getUuid());
        intent2.putExtra("visitUuid", uuid);
        intent2.putExtra("encounterUuidVitals", encounterDTO.getUuid());
        intent2.putExtra("encounterUuidAdultIntial", "");
        intent2.putExtra("EncounterAdultInitial_LatestVisit", encounterAdultIntials);
        intent2.putExtra("name", fullName);
        intent2.putExtra("gender", mGender);
        intent2.putExtra("tag", "new");
        intent2.putExtra("float_ageYear_Month", float_ageYear_Month);
        startActivity(intent2);
    }


}
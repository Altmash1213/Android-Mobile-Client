package org.intelehealth.app.activities.householdSurvey.Fragments;
/**
 * Created by Prajwal Maruti Waingankar on 14-02-2022
 * Copyright (c) 2021 . All rights reserved.
 * Email: prajwalwaingankar@gmail.com
 * Github: prajwalmw
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.intelehealth.app.R;
import org.intelehealth.app.app.AppConstants;
import org.intelehealth.app.database.dao.PatientsDAO;
import org.intelehealth.app.database.dao.SyncDAO;
import org.intelehealth.app.databinding.FragmentFifthScreenBinding;
import org.intelehealth.app.databinding.FragmentFourthScreenBinding;
import org.intelehealth.app.models.dto.PatientAttributesDTO;
import org.intelehealth.app.utilities.NetworkConnection;
import org.intelehealth.app.utilities.SessionManager;
import org.intelehealth.app.utilities.StringUtils;
import org.intelehealth.app.utilities.exception.DAOException;
import org.json.JSONArray;

import java.util.Locale;
import java.util.UUID;

import static org.intelehealth.app.activities.householdSurvey.HouseholdSurveyActivity.patientAttributesDTOList;

public class FifthScreenFragment extends Fragment {

    private FragmentFifthScreenBinding binding;
    private String patientUuid;
    private SessionManager sessionManager;
    PatientsDAO patientsDAO = new PatientsDAO();

    public FifthScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent(); // The intent was passed to the activity
        if (intent != null) {
            patientUuid = intent.getStringExtra("patientUuid");
        }

        sessionManager = new SessionManager(getActivity());
        String language = sessionManager.getAppLanguage();
        Log.d("lang", "lang: " + language);
        if (!language.equalsIgnoreCase("")) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getActivity().getBaseContext().getResources().updateConfiguration(config,
                    getActivity().getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View rootView =  inflater.inflate(R.layout.fragment_fifth_screen, container, false);
        binding = FragmentFifthScreenBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    insertData();
                } catch (DAOException e) {
                    e.printStackTrace();
                }
            }
        });

        binding.otherCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.otherSourcesOfFuelLayout.setVisibility(View.VISIBLE);
            } else {
                binding.otherSourcesOfFuelLayout.setVisibility(View.GONE);
            }
        });

        binding.otherSourceOfLightingCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.otherSourcesOfLightingLayout.setVisibility(View.VISIBLE);
            } else {
                binding.otherSourcesOfLightingLayout.setVisibility(View.GONE);
            }
        });

        binding.otherSourceOfWaterCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.otherSourcesOfDrinkingWaterLayout.setVisibility(View.VISIBLE);
            } else {
                binding.otherSourcesOfDrinkingWaterLayout.setVisibility(View.GONE);
            }
        });

        binding.otherWaysOfPurifyingWaterCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.otherWaysOfPurifyingWaterEditText.setVisibility(View.VISIBLE);
            } else {
                binding.otherWaysOfPurifyingWaterEditText.setVisibility(View.GONE);
            }
        });
        setData(patientUuid);
        return rootView;
    }

    private void insertData() throws DAOException {
        PatientsDAO patientsDAO = new PatientsDAO();
        PatientAttributesDTO patientAttributesDTO = new PatientAttributesDTO();
        // List<PatientAttributesDTO> patientAttributesDTOList = new ArrayList<>();

        //cookingFuelType
        patientAttributesDTO = new PatientAttributesDTO();
        patientAttributesDTO.setUuid(UUID.randomUUID().toString());
        patientAttributesDTO.setPatientuuid(patientUuid); // Intent from PatientDetail screen...
        patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("cookingFuelType"));
        patientAttributesDTO.setValue(StringUtils.getSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout));
        patientAttributesDTOList.add(patientAttributesDTO);


        //mainLightingSource
        patientAttributesDTO = new PatientAttributesDTO();
        patientAttributesDTO.setUuid(UUID.randomUUID().toString());
        patientAttributesDTO.setPatientuuid(patientUuid); // Intent from PatientDetail screen...
        patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("mainLightingSource"));
        patientAttributesDTO.setValue(StringUtils.getSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout));
        patientAttributesDTOList.add(patientAttributesDTO);

        //mainDrinkingWaterSource
        patientAttributesDTO = new PatientAttributesDTO();
        patientAttributesDTO.setUuid(UUID.randomUUID().toString());
        patientAttributesDTO.setPatientuuid(patientUuid); // Intent from PatientDetail screen...
        patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("mainDrinkingWaterSource"));
        patientAttributesDTO.setValue(StringUtils.getSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout));
        patientAttributesDTOList.add(patientAttributesDTO);

        //saferWaterProcess
        patientAttributesDTO = new PatientAttributesDTO();
        patientAttributesDTO.setUuid(UUID.randomUUID().toString());
        patientAttributesDTO.setPatientuuid(patientUuid); // Intent from PatientDetail screen...
        patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("saferWaterProcess"));
        patientAttributesDTO.setValue(StringUtils.getSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout));
        patientAttributesDTOList.add(patientAttributesDTO);

        //householdToiletFacility
        patientAttributesDTO = new PatientAttributesDTO();
        patientAttributesDTO.setUuid(UUID.randomUUID().toString());
        patientAttributesDTO.setPatientuuid(patientUuid); // Intent from PatientDetail screen...
        patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("householdToiletFacility"));
        patientAttributesDTO.setValue(StringUtils.getSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout));
        patientAttributesDTOList.add(patientAttributesDTO);

        Gson gson = new Gson();
        gson.toJson(patientAttributesDTOList);
        Log.v("screen", "secondscreen: \n"+ gson.toJson(patientAttributesDTOList));

        // TODO: this logic just for testing purpose have added here. Once all screens is done than at the end of 7th screen
        //  by clicking on SUBMIT button add this code on that button clicklistener...
//        boolean isPatientUpdated = patientsDAO.SurveyupdatePatientToDB(patientUuid, patientAttributesDTOList);
//        if (NetworkConnection.isOnline(getActivity().getApplication())) {
//            SyncDAO syncDAO = new SyncDAO();
//            boolean ispush = syncDAO.pushDataApi();
//
//        }
//        // Upto here so that data is stored in localdb and pushed by clicking on FAB...
//
//        if (isPatientUpdated) {
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.framelayout_container, new SixthScreenFragment())
//                    .commit();
//        }
            getFragmentManager().beginTransaction()
                    .replace(R.id.framelayout_container, new SixthScreenFragment())
                    .commit();
    }

    private void setData(String patientUuid)
    {
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();

        String patientSelection1 = "patientuuid = ?";
        String[] patientArgs1 = {patientUuid};
        String[] patientColumns1 = {"value", "person_attribute_type_uuid"};
        final Cursor idCursor1 = db.query("tbl_patient_attribute", patientColumns1, patientSelection1, patientArgs1, null, null, null);
        String name = "";
        if (idCursor1.moveToFirst()) {
            do {
                try {
                    name = patientsDAO.getAttributesName(idCursor1.getString(idCursor1.getColumnIndexOrThrow("person_attribute_type_uuid")));
                } catch (DAOException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
                if (name.equalsIgnoreCase("cookingFuelType")) {
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.electricity)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.electricity));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.lpg_natural_gas)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.lpg_natural_gas));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.biogas_checkbox)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.biogas_checkbox));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.kerosene)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.kerosene));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.coal_lignite)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.coal_lignite));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.wood)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.wood));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.charcoal)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.charcoal));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.straw_shrubs_grass)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.straw_shrubs_grass));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.agricultural_crop_waste)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.agricultural_crop_waste));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.dung_cakes)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.dung_cakes));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.other_specify)))
                        setSelectedCheckboxes(binding.householdCookingFuelCheckboxLinearLayout,getString(R.string.other_specify));
                }
                if (name.equalsIgnoreCase("mainLightingSource")) {
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.lantern)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.lantern));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.kerosene_lamp)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.kerosene_lamp));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.candle)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.candle));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.electric)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.electric));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.lpg)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.lpg));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.solar_energy)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.solar_energy));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.none)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.none));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.other_specify)))
                        setSelectedCheckboxes(binding.mainSourceOfLightingCheckboxLinearLayout,getString(R.string.other_specify));
                }
                if (name.equalsIgnoreCase("mainDrinkingWaterSource")) {
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.piped_into_dwelling)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.piped_into_dwelling));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.piped_into_yard_plot)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.piped_into_yard_plot));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.public_tap_standpipe)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.public_tap_standpipe));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.tube_well_borehole)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.tube_well_borehole));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.protected_well_checkbox)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.protected_well_checkbox));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.unprotected_well)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.unprotected_well));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.protected_spring)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.protected_spring));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.unprotected_spring)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.unprotected_spring));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.rainwater)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.rainwater));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.tanker_truck)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.tanker_truck));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.cart_with_small_tank)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.cart_with_small_tank));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.surface_water)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.surface_water));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.common_hand_pump)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.common_hand_pump));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.hand_pump_at_home)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.hand_pump_at_home));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.other_specify)))
                        setSelectedCheckboxes(binding.mainSourceOfDrinkingWaterCheckboxLinearLayout,getString(R.string.other_specify));
                }
                if (name.equalsIgnoreCase("saferWaterProcess")) {
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.boil)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.boil));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.use_alum)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.use_alum));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.add_bleach_chlorine_tablets_drops)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.add_bleach_chlorine_tablets_drops));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.strain_through_a_cloth)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.strain_through_a_cloth));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.use_water_filter_ceramic_sand_composite_etc)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.use_water_filter_ceramic_sand_composite_etc));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.use_electronic_purifier)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.use_electronic_purifier));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.let_it_stand_and_settle)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.let_it_stand_and_settle));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.other_specify)))
                        setSelectedCheckboxes(binding.householdMakeSafeWaterCheckboxLinearLayout,getString(R.string.other_specify));
                }
                if (name.equalsIgnoreCase("householdToiletFacility")) {
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.flush_to_piped_sewer_system)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.flush_to_piped_sewer_system));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.flush_to_septic_tank)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.flush_to_septic_tank));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.flush_to_pit_latrine)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.flush_to_pit_latrine));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.flush_to_somewhere_else)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.flush_to_somewhere_else));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.flush_dont_know_where)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.flush_dont_know_where));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.ventilated_improved_pit_biogas_latrine)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.ventilated_improved_pit_biogas_latrine));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.pit_latrine_with_slab)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.pit_latrine_with_slab));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.pit_latrine_without_slab_open_pit)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.pit_latrine_without_slab_open_pit));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.twin_pit_composting_toilet)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.twin_pit_composting_toilet));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.dry_toilet)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.dry_toilet));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.communal_toilet)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.communal_toilet));
                    if (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")) != null && (idCursor1.getString(idCursor1.getColumnIndexOrThrow("value"))).contains(getString(R.string.other_specify)))
                        setSelectedCheckboxes(binding.familyToiletFacilityCheckboxLinearLayout,getString(R.string.other_specify));
                }
            } while (idCursor1.moveToNext());
        }
        idCursor1.close();

    }

    private void setSelectedCheckboxes(ViewGroup viewGroup, String s) {
        if (viewGroup == null)
            return;

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof CheckBox && ((CheckBox) childAt).getText().toString().equalsIgnoreCase(s)) {
                ((CheckBox) childAt).setChecked(true);
            }
        }
    }
}
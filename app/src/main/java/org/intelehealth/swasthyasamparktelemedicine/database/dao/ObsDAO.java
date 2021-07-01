package org.intelehealth.swasthyasamparktelemedicine.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.intelehealth.swasthyasamparktelemedicine.app.AppConstants;
import org.intelehealth.swasthyasamparktelemedicine.app.IntelehealthApplication;
import org.intelehealth.swasthyasamparktelemedicine.models.dto.ObsDTO;
import org.intelehealth.swasthyasamparktelemedicine.utilities.Logger;
import org.intelehealth.swasthyasamparktelemedicine.utilities.SessionManager;
import org.intelehealth.swasthyasamparktelemedicine.utilities.UuidDictionary;
import org.intelehealth.swasthyasamparktelemedicine.utilities.exception.DAOException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ObsDAO {


    private SQLiteDatabase db = null;
    SessionManager sessionManager = null;
    String TAG = ObsDAO.class.getSimpleName();

    public boolean insertObsTemp(List<ObsDTO> obsDTOS) throws DAOException {
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        boolean isInserted = true;
        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        try {
            db.beginTransaction();
            Logger.logD("insert", " insert obs");
            for (ObsDTO obs : obsDTOS) {
                if (sessionManager.isFirstTimeSyncExcuted() && obs.getVoided() == 1)
                    continue;
                createObs(obs);
            }
            db.setTransactionSuccessful();
            Logger.logD("insert obs finished", " insert obs finished");
        } catch (SQLException e) {
            isInserted = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return isInserted;

    }

    private boolean createObs(ObsDTO obsDTOS) throws DAOException {
        boolean isCreated = true;
        long createdRecordsCount = 0;
        ContentValues values = new ContentValues();
        try {
            values.put("uuid", obsDTOS.getUuid());
            values.put("encounteruuid", obsDTOS.getEncounteruuid());
            values.put("creator", obsDTOS.getCreator());
            values.put("conceptuuid", obsDTOS.getConceptuuid());
            values.put("value", obsDTOS.getValue());
            values.put("obsservermodifieddate", obsDTOS.getObsServerModifiedDate());
            values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
            values.put("voided", obsDTOS.getVoided());
            values.put("sync", "TRUE");
            createdRecordsCount = db.insertWithOnConflict("tbl_obs", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLException e) {
            isCreated = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
        }

        return isCreated;

    }

    public boolean insertObs(ObsDTO obsDTO) throws DAOException {
        boolean isUpdated = true;
        long insertedCount = 0;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        try {
            values.put("uuid", UUID.randomUUID().toString());
            values.put("encounteruuid", obsDTO.getEncounteruuid());
            values.put("creator", obsDTO.getCreator());
            values.put("conceptuuid", obsDTO.getConceptuuid());
            values.put("value", obsDTO.getValue());
            values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
            values.put("voided", "0");
            values.put("sync", "false");
            insertedCount = db.insertWithOnConflict("tbl_obs", null, values, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            Logger.logD("updated", "updatedrecords count" + insertedCount);
        } catch (SQLException e) {
            isUpdated = false;
            throw new DAOException(e);
        } finally {
            db.endTransaction();

        }

        return isUpdated;

    }


    public boolean updateObs(ObsDTO obsDTO) {
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        int updatedCount = 0;
        ContentValues values = new ContentValues();
        String selection = "uuid = ?";
        try {

            values.put("encounteruuid", obsDTO.getEncounteruuid());
            values.put("creator", obsDTO.getCreator());
            values.put("conceptuuid", obsDTO.getConceptuuid());
            values.put("value", obsDTO.getValue());
            values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
            values.put("voided", "0");
            values.put("sync", "false");

            updatedCount = db.update("tbl_obs", values, selection, new String[]{obsDTO.getUuid()});

            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Logger.logE(TAG, "exception ", e);

        } finally {
            db.endTransaction();

        }
//        If no value is not found, then update fails so insert instead.
        if (updatedCount == 0) {
            try {
                insertObs(obsDTO);
            } catch (DAOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }


        return true;
    }

    public boolean insertObsToDb(List<ObsDTO> obsDTO) throws DAOException {
        boolean isUpdated = true;
        long insertedCount = 0;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        try {
            for (ObsDTO ob : obsDTO) {
                values.put("uuid", UUID.randomUUID().toString());
                values.put("encounteruuid", ob.getEncounteruuid());
                values.put("creator", ob.getCreator());
                values.put("conceptuuid", ob.getConceptuuid());
                values.put("value", ob.getValue());
                values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
                values.put("voided", "0");
                values.put("sync", "false");    //Earlier was set to FALSE which caused the issue.
                insertedCount = db.insert("tbl_obs", null, values);
            }
            db.setTransactionSuccessful();
            Logger.logD("updated", "updatedrecords count" + insertedCount);
        } catch (SQLException e) {
            isUpdated = false;
            FirebaseCrashlytics.getInstance().recordException(e);
            throw new DAOException(e);
        } finally {
            db.endTransaction();

        }

        return isUpdated;

    }

    public List<ObsDTO> obsDTOList(String encounteruuid) {
        List<ObsDTO> obsDTOList = new ArrayList<>();
        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        //take All obs except image obs
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_obs where encounteruuid = ? AND (conceptuuid != ? AND conceptuuid != ?) AND voided='0' AND sync='false'", new String[]{encounteruuid, UuidDictionary.COMPLEX_IMAGE_AD, UuidDictionary.COMPLEX_IMAGE_PE});
        ObsDTO obsDTO = new ObsDTO();
        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {
                obsDTO = new ObsDTO();
                obsDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                obsDTO.setEncounteruuid(idCursor.getString(idCursor.getColumnIndexOrThrow("encounteruuid")));
                obsDTO.setConceptuuid(idCursor.getString(idCursor.getColumnIndexOrThrow("conceptuuid")));
                obsDTO.setValue(idCursor.getString(idCursor.getColumnIndexOrThrow("value")));
                obsDTOList.add(obsDTO);
            }
        }
        idCursor.close();

        return obsDTOList;
    }

    public List<JSONObject> getAlertList() {
        List<JSONObject> objectList = new ArrayList<JSONObject>();
        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        Cursor idCursor = db.rawQuery("select uuid, openmrs_id, first_name, last_name from tbl_patient where uuid in (select DISTINCT patientuuid  from tbl_visit where uuid in \n" +
                "(select visituuid from tbl_encounter where uuid in (select encounteruuid from tbl_obs where value like '%Alert Message%')));", null);

        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                try {
                    // check the patient already attended by the nurse
                    Date patientEntryLatestDate = null;
                    Date nurseEntryLatestDate = null;
                    Cursor patientEntry = db.rawQuery("select uuid, obsservermodifieddate from tbl_obs where value like '%Alert Message%' and encounteruuid in (select uuid  from tbl_encounter where visituuid in \n" +
                            "(select uuid from tbl_visit where patientuuid=?)) order by created_date DESC limit 1;", new String[]{idCursor.getString(idCursor.getColumnIndexOrThrow("uuid"))});
                    Log.v("LatestDate", "patientEntry - " + patientEntry.getCount());
                    if (patientEntry.getCount() > 0) {
                        patientEntry.moveToFirst();
                        patientEntryLatestDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(patientEntry.getString(patientEntry.getColumnIndexOrThrow("obsservermodifieddate")));

                    }
                    Cursor nurseEntry = db.rawQuery("select uuid, obsservermodifieddate from tbl_obs where value like '%Medical History%' and encounteruuid in (select uuid  from tbl_encounter where visituuid in \n" +
                            "(select uuid from tbl_visit where patientuuid=?)) order by created_date DESC limit 1;", new String[]{idCursor.getString(idCursor.getColumnIndexOrThrow("uuid"))});

                    Log.v("LatestDate", "nurseEntry - " + nurseEntry.getCount());
                    if (nurseEntry.getCount() > 0) {
                        nurseEntry.moveToFirst();
                        nurseEntryLatestDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(nurseEntry.getString(nurseEntry.getColumnIndexOrThrow("obsservermodifieddate")));

                    }
                    Log.v("LatestDate", "patientEntryLatestDate - " + patientEntryLatestDate);
                    Log.v("LatestDate", "nurseEntryLatestDate - " + nurseEntryLatestDate);

                    if (nurseEntryLatestDate == null || patientEntryLatestDate.after(nurseEntryLatestDate)) {
                        jsonObject.put("uuid", idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                        jsonObject.put("openmrs_id", idCursor.getString(idCursor.getColumnIndexOrThrow("openmrs_id")));
                        jsonObject.put("first_name", idCursor.getString(idCursor.getColumnIndexOrThrow("first_name")));
                        jsonObject.put("last_name", idCursor.getString(idCursor.getColumnIndexOrThrow("last_name")));
                        objectList.add(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        idCursor.close();

        return objectList;
    }

    public List<String> getImageStrings(String conceptuuid, String encounterUuidAdultIntials) {
        List<String> rawStrings = new ArrayList<>();
        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        Cursor idCursor = db.rawQuery("SELECT uuid FROM tbl_obs where conceptuuid = ? AND encounteruuid = ? AND voided='0'", new String[]{conceptuuid, encounterUuidAdultIntials});
        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {
                rawStrings.add(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
            }
        }
        idCursor.close();


        return rawStrings;
    }

    public String getObsuuid(String encounterUuid, String conceptUuid) throws DAOException {
        String obsuuid = null;
        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        Cursor obsCursoursor = db.rawQuery("Select uuid from tbl_obs where conceptuuid=? and encounteruuid=? and voided='0' order by created_date,obsservermodifieddate desc limit 1 ", new String[]{conceptUuid, encounterUuid});
        try {
            if (obsCursoursor.getCount() != 0) {
                while (obsCursoursor.moveToNext()) {
                    obsuuid = obsCursoursor.getString(obsCursoursor.getColumnIndexOrThrow("uuid"));
                }

            }
        } catch (SQLException sql) {
            FirebaseCrashlytics.getInstance().recordException(sql);
            throw new DAOException(sql);
        } finally {
            obsCursoursor.close();
        }


        return obsuuid;
    }


}

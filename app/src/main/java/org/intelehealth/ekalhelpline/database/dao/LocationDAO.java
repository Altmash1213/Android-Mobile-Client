package org.intelehealth.ekalhelpline.database.dao;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import org.intelehealth.ekalhelpline.app.AppConstants;
import org.intelehealth.ekalhelpline.models.dto.LocationDTO;
import org.intelehealth.ekalhelpline.utilities.exception.DAOException;

public class LocationDAO {


    long createdRecordsCount = 0;

    public boolean insertLocations(List<LocationDTO> locationDTOS) throws DAOException {

        boolean isInserted = true;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransactionNonExclusive();
        try {
            for (LocationDTO location : locationDTOS) {
                createLocation(location, db);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isInserted = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return isInserted;
    }

    private boolean createLocation(LocationDTO location, SQLiteDatabase db) throws DAOException {
        boolean isCreated = true;
        ContentValues values = new ContentValues();
        try {
            values.put("name", location.getName());
            values.put("locationuuid", location.getLocationuuid());
            values.put("retired", location.getRetired());
            values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
            values.put("sync", "TRUE");
            createdRecordsCount = db.insertWithOnConflict("tbl_location", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLException e) {
            isCreated = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
        }
        return isCreated;
    }

}

package app.intelehealth.client.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.intelehealth.client.app.AppConstants;
import app.intelehealth.client.models.dto.ProviderAttributeListDTO;
import app.intelehealth.client.models.dto.ProviderDTO;
import app.intelehealth.client.models.dto.VisitDTO;
import app.intelehealth.client.utilities.exception.DAOException;

/**
 * Created by Prajwal Waingankar
 * on 14-Jul-20.
 * Github: prajwalmw
 */


public class ProviderAttributeLIstDAO {
    private long createdRecordsCount = 0;

    public boolean insertProvidersAttributeList(List<ProviderAttributeListDTO> providerAttributeListDTOS)
            throws DAOException {

        boolean isInserted = true;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        try {
            for (ProviderAttributeListDTO providerAttributeListDTO : providerAttributeListDTOS) {
                createProvidersAttributeList(providerAttributeListDTO, db);
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

    private boolean createProvidersAttributeList(ProviderAttributeListDTO attributeListDTO, SQLiteDatabase db) throws DAOException {
        boolean isCreated = true;
        ContentValues values = new ContentValues();

        try{
            values.put("uuid", attributeListDTO.getUuid());
            values.put("provideruuid", attributeListDTO.getProvideruuid());
            values.put("attributetypeuuid", attributeListDTO.getAttributetypeuuid());
            values.put("value", attributeListDTO.getValue());
            values.put("voided", attributeListDTO.getVoided());

            if(attributeListDTO.getVoided() == 0 &&
            attributeListDTO.getAttributetypeuuid().equals("ed1715f5-93e2-404e-b3c9-2a2d9600f062"))
            {
                createdRecordsCount = db.insertWithOnConflict("tbl_dr_speciality", null, values, SQLiteDatabase.CONFLICT_REPLACE);

                if(createdRecordsCount != -1)
                {
                    Log.d("SPECI", "SIZEXXX: " + createdRecordsCount);
                }
                else
                {
                    Log.d("SPECI", "SIZEXXX: " + createdRecordsCount);
                }

            }


        }
        catch (SQLException e)
        {
            isCreated = false;
            throw new DAOException(e.getMessage(), e);
        }
        finally
        {

        }

        return isCreated;
    }

    public List<ProviderAttributeListDTO> getAllValues() {
        List<ProviderAttributeListDTO> listDTOArrayList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        String selectionArgs[] = {"ed1715f5-93e2-404e-b3c9-2a2d9600f062", "0"};
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_dr_speciality WHERE " +
                "attributetypeuuid = ? AND voided = ?", null);

        ProviderAttributeListDTO dto = new ProviderAttributeListDTO();
        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {
                dto = new ProviderAttributeListDTO();
                dto.setValue(idCursor.getString(idCursor.getColumnIndexOrThrow("value")));
                listDTOArrayList.add(dto);
            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return listDTOArrayList;
    }



}

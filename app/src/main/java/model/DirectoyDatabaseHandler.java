package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import util.DbBitmapUtility;

/**
 * Created by Guerino on 28/09/2016.
 */

public class DirectoyDatabaseHandler extends DatabaseHandler {
    private String TABLE_NAME = "Directory";

    public DirectoyDatabaseHandler(Context context) {
        super(context);

    }

    public long insert(Directory d) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("id", d.getId());
        values.put("photo_primary_id", d.getPrimaryId());
        values.put("farm_id", d.getFarmId());
        values.put("server_id", d.getServerId());
        values.put("photo_secret", d.getPhotoSecret());
        values.put("bitmap_image", DbBitmapUtility.getBytes(d.getBitmap()));
        values.put("name", d.getName());
        values.put("count_photos", d.getPhotos());
        values.put("count_views", d.getViews());
        values.put("count_comments", d.getComments());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return  newRowId;
    }

    public Boolean isDirectoryInsideLocalDB(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + TABLE_NAME + " where id=" + id, null );
        //Si existe el elemento buscado
        if(c.getCount() > 0){
            return true;
        }

        return false;
    }

    public Directory getDirectory(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + TABLE_NAME + " where id=" + id, null );
        Directory d = null;
        //Si existe el elemento buscado
        if(c.getCount() > 0){
            c.moveToFirst();
            d = new Directory();
            d.setId(c.getString(c.getColumnIndex("id")));
            d.setFarmId(c.getString(c.getColumnIndex("farm_id")));
            d.setServerId(c.getString(c.getColumnIndex("server_id")));
            d.setPhotoSecret(c.getString(c.getColumnIndex("photo_secret")));
            d.setBitmap(DbBitmapUtility.getImage(c.getBlob(c.getColumnIndex("bitmap_image"))));
            d.setName(c.getString(c.getColumnIndex("name")));
            d.setViews(c.getString(c.getColumnIndex("count_views")));
            d.setPhotos(c.getString(c.getColumnIndex("count_photos")));
            d.setComments(c.getString(c.getColumnIndex("count_comments")));
        }

        return d;
    }

    public ArrayList<Directory> getAllDirectory(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + TABLE_NAME, null );
        //lista de directorios=Albumes
        ArrayList<Directory> list = null;
        //Si existe el elemento buscado
        if(c.getCount() > 0){
            c.moveToFirst();
            list = new ArrayList<>();
            do {
                Directory d = new Directory();
                d.setId(c.getString(c.getColumnIndex("id")));
                d.setFarmId(c.getString(c.getColumnIndex("farm_id")));
                d.setServerId(c.getString(c.getColumnIndex("server_id")));
                d.setPhotoSecret(c.getString(c.getColumnIndex("photo_secret")));
                d.setBitmap(DbBitmapUtility.getImage(c.getBlob(c.getColumnIndex("bitmap_image"))));
                d.setName(c.getString(c.getColumnIndex("name")));
                d.setViews(c.getString(c.getColumnIndex("count_views")));
                d.setPhotos(c.getString(c.getColumnIndex("count_photos")));
                d.setComments(c.getString(c.getColumnIndex("count_comments")));

                //Lo agregamos a la lista
                list.add(d);
               // Log.d("LoadFromDB", d.toString());
            } while(c.moveToNext());

        }

        return list;
    }
}

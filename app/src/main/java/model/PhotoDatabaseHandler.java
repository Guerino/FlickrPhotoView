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

public class PhotoDatabaseHandler extends DatabaseHandler {
    private String TABLE_NAME = "Photo";

    public PhotoDatabaseHandler(Context context) {
        super(context);
    }

    public long insert(Photo p) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("id", p.getId());
        values.put("title", p.getTitle());
        values.put("bitmap_image", DbBitmapUtility.getBytes(p.getBitmap()));
        values.put("count_views", p.getViews());
        values.put("date_taken", p.getDateTaken());
        values.put("description", p.getDescription());
        values.put("farm_id", p.getFarmId());
        values.put("server_id", p.getServerId());
        values.put("photo_secret", p.getPhotoSecret());
        values.put("album_id", p.getAlbumId());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        return  newRowId;
    }

    public Boolean isPhotoInsideLocalDB(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + TABLE_NAME + " where id=" + id, null );
        //Si existe el elemento buscado
        if(c.getCount() > 0){
            return true;
        }
        return false;
    }

    public Photo getPhoto(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + TABLE_NAME + " where id=" + id, null );
        Photo p = null;
        //Si existe el elemento buscado
        if(c.getCount() > 0){
            c.moveToFirst();
            p = new Photo();
            p.setId(c.getString(c.getColumnIndex("id")));
            p.setTitle(c.getString(c.getColumnIndex("title")));
            p.setBitmap(DbBitmapUtility.getImage(c.getBlob(c.getColumnIndex("bitmap_image"))));
            p.setViews(c.getString(c.getColumnIndex("count_views")));
            p.setFarmId(c.getString(c.getColumnIndex("farm_id")));
            p.setServerId(c.getString(c.getColumnIndex("server_id")));
            p.setPhotoSecret(c.getString(c.getColumnIndex("photo_secret")));
            p.setDescription(c.getString(c.getColumnIndex("description")));
            p.setAlbumId(c.getString(c.getColumnIndex("album_id")));
        }
        return p;
    }

    public ArrayList<Photo> getPhotosByAlbum(String albumId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + TABLE_NAME + " where album_id=" + albumId, null );
        //lista de directorios=Albumes
        ArrayList<Photo> list = null;
        //Si existe el elemento buscado
        if(c.getCount() > 0){
            c.moveToFirst();
            list = new ArrayList<>();
            do {
                Photo p = new Photo();
                p.setId(c.getString(c.getColumnIndex("id")));
                p.setTitle(c.getString(c.getColumnIndex("title")));
                p.setBitmap(DbBitmapUtility.getImage(c.getBlob(c.getColumnIndex("bitmap_image"))));
                p.setViews(c.getString(c.getColumnIndex("count_views")));
                p.setDateTaken(c.getString(c.getColumnIndex("date_taken")));
                p.setFarmId(c.getString(c.getColumnIndex("farm_id")));
                p.setServerId(c.getString(c.getColumnIndex("server_id")));
                p.setPhotoSecret(c.getString(c.getColumnIndex("photo_secret")));
                p.setDescription(c.getString(c.getColumnIndex("description")));
                p.setAlbumId(c.getString(c.getColumnIndex("album_id")));
                //Lo agregamos a la lista
                list.add(p);
                // Log.d("LoadFromDB", d.toString());
            } while(c.moveToNext());

        }

        return list;
    }

    public ArrayList<Photo> getAllPhotos(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "select * from " + TABLE_NAME, null );
        //lista de directorios=Albumes
        ArrayList<Photo> list = null;
        //Si existe el elemento buscado
        if(c.getCount() > 0){
            c.moveToFirst();
            list = new ArrayList<>();
            do {
                Photo p = new Photo();
                p.setId(c.getString(c.getColumnIndex("id")));
                p.setTitle(c.getString(c.getColumnIndex("title")));
                p.setBitmap(DbBitmapUtility.getImage(c.getBlob(c.getColumnIndex("bitmap_image"))));
                p.setViews(c.getString(c.getColumnIndex("count_views")));
                p.setFarmId(c.getString(c.getColumnIndex("farm_id")));
                p.setServerId(c.getString(c.getColumnIndex("server_id")));
                p.setPhotoSecret(c.getString(c.getColumnIndex("photo_secret")));
                p.setDescription(c.getString(c.getColumnIndex("description")));
                p.setAlbumId(c.getString(c.getColumnIndex("album_id")));
                //Lo agregamos a la lista
                list.add(p);
                // Log.d("LoadFromDB", d.toString());
            } while(c.moveToNext());

        }

        return list;
    }
}

package model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

import util.AppConfiguration;

/**
 * Created by Guerino on 27/09/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "FlickrPhotoView.db";

    public DatabaseHandler(Context context) {
        super(context, AppConfiguration.DATABASE_DIR + DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creamos las tablas correspondientes
        String tableDirectory = "CREATE TABLE Directory (\n" +
                "    id               STRING PRIMARY KEY\n" +
                "                             NOT NULL\n" +
                "                             UNIQUE,\n" +
                "    photo_primary_id STRING,\n" +
                "    farm_id          STRING,\n" +
                "    server_id        STRING,\n" +
                "    photo_secret     STRING,\n" +
                "    bitmap_image     BLOB,\n" +
                "    name             STRING,\n" +
                "    count_photos     STRING,\n" +
                "    count_views      STRING,\n" +
                "    count_comments   STRING\n" +
                ");\n";
        db.execSQL(tableDirectory);

        String tablePhoto = "CREATE TABLE Photo (\n" +
                "    id           STRING PRIMARY KEY\n" +
                "                         UNIQUE\n" +
                "                         NOT NULL,\n" +
                "    title        STRING,\n" +
                "    bitmap_image BLOB,\n" +
                "    count_views  STRING,\n" +
                "    date_taken   STRING,\n" +
                "    description  STRING,\n" +
                "    farm_id      STRING,\n" +
                "    server_id    STRING,\n" +
                "    photo_secret STRING,\n" +
                "    album_id     STRING  REFERENCES Directory (id) \n" +
                "                         NOT NULL\n" +
                ");\n";
        db.execSQL(tablePhoto);

        String tableComments = "CREATE TABLE Comments (\n" +
                "    photo_id    STRING REFERENCES Photo (id) \n" +
                "                       NOT NULL\n" +
                "                       PRIMARY KEY,\n" +
                "    autor       STRING NOT NULL,\n" +
                "    real_name   STRING,\n" +
                "    description STRING\n" +
                ");";
        db.execSQL(tableComments);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + "Directory");
        db.execSQL("DROP TABLE IF EXISTS " + "Photo");
        db.execSQL("DROP TABLE IF EXISTS " + "Comments");

        // Create tables again
        onCreate(db);
    }
}

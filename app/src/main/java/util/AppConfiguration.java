package util;

import android.os.Environment;

import java.io.File;

/**
 * Created by Guerino on 01/10/2016.
 */

public class AppConfiguration {
    public static final String APP_NAME = "FlickrPhotoView";
    public static final String APP_DIR = Environment.getExternalStorageDirectory() + File.separator + APP_NAME + File.separator;
    public static final String DATABASE_DIR = APP_DIR + File.separator + "Database" + File.separator;
    public static final String IMAGES_ALBUM_DIR = APP_DIR + File.separator + "Albums" + File.separator;
    public static final String IMAGES_PHOTO_DIR = APP_DIR + File.separator + "Photos" + File.separator;

    public static void createAppDirectory(){
        //Creamos el directorio
        File fileDir = new File(AppConfiguration.APP_DIR);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        //Creamos el directorio
        fileDir = new File(AppConfiguration.IMAGES_ALBUM_DIR);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        //Creamos el directorio
        fileDir = new File(AppConfiguration.IMAGES_PHOTO_DIR);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        //Creamos el directorio
        fileDir = new File(AppConfiguration.DATABASE_DIR);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }
}

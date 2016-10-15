package util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Guerino on 22/09/2016.
 */

public class DownLoaderUrl {

    /**
     * Metodo para descargar una URL
     * @param strUrl
     * @return string en memoria
     */
    public static String DownloadUrl(String strUrl){
        //Abrir conexion a la Url destino
        StringBuilder builderJson = new StringBuilder();
        try {
            URL conectUrl = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(conectUrl.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null){
                builderJson.append(inputLine);
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builderJson.toString();
    }

    /**
     * Metodo que sirve para descargar una imagen desde internet
     * @param imageUrl
     * @return
     */
    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            //Obtenemos el nombre del archivo
            //String FILE = params[0].substring(params[0].lastIndexOf("/"), params[0].length());

            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            //Obtener el largo del archivo, sirve para poder calcular y mostrar el progreso de carga
            connection.getContentLength();

            InputStream input = connection.getInputStream();

            //input.
            Bitmap myBitmap = BitmapFactory.decodeStream(input);

            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

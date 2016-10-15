package com.tdm.view.photo.flickr.flickrphotoview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Directory;
import model.DirectoryAdapter;
import model.DirectoyDatabaseHandler;
import model.FlickrAPI;
import util.AppConfiguration;
import util.DownLoaderUrl;
import util.NetworkUtils;
import util.ShowMessageUtils;

public class DirectoryListActivity extends AppCompatActivity {
    private FlickrAPI flickrAPI;
    private ListView listViewDirectory;
    private ProgressDialog progDialog;
    private DownloadJSONAsyncTask downloadJsonTask;
    private DirectoyDatabaseHandler dbHandler;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_list);

        //iniciamos las preferecencias para esta aplicacion
        mSettings = getSharedPreferences(AppConfiguration.APP_NAME, MODE_PRIVATE);
        if(mSettings.getBoolean("full_screen", false)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        listViewDirectory = (ListView)findViewById(R.id.directory_list_view);

        //Creamos los directorios de nuestra app
        AppConfiguration.createAppDirectory();

        dbHandler = new DirectoyDatabaseHandler(this);

       //Verificar primero si hay conexion a internet
       if(NetworkUtils.isWifiConnected(this) || NetworkUtils.isMobileConnected(this)) {
           //Notificacion de actualizacion del listado de directorios
            ShowMessageUtils.SendNotification(this, getResources().getString(R.string.app_name),
                   getResources().getString(R.string.text_notification_updating_directory_list),"");

            progDialog = new ProgressDialog(this);
            //Iniciar tarea de descarga de lista de directorios
            flickrAPI = new FlickrAPI();
            downloadJsonTask = new DownloadJSONAsyncTask(this, listViewDirectory);
            //Descargamos el JSON con la lista de albunes y armamos el ListView
            downloadJsonTask.execute(flickrAPI.getPhotosetsList());
        } else {
            //Aqui cargar los datos locales.
            LoadDataFromDB();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.directory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // User chose the "Settings" item, show the app settings UI...
                //Intent explicito
                Intent intent = new Intent(this, PreferencesActivity.class);
                //Iniciar la activity
                startActivity(intent);
                return true;

            case R.id.menu_about:
                // User chose the "Settings" item, show the app settings UI...
                //Intent explicito
                Intent intAbout = new Intent(this, AboutActivity.class);
                //Iniciar la activity
                startActivity(intAbout);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * Clase interna para la descarga del JSON de Flickr y la correspondiente
     * inicializacion del ListView
     */
    private class DownloadJSONAsyncTask extends AsyncTask<String, Void, String> {
        private Context context;
        private ListView listViewDirectory;
        private Bitmap tempBitmap;
        private List listUrlPhotoPrimary;

        public DownloadJSONAsyncTask(Context context, ListView directory) {
            this.context = context;
            this.listViewDirectory = directory;
            this.listUrlPhotoPrimary = new LinkedList<FlickrAPI>();
        }

        @Override
        protected void onPreExecute() {
            //Mostramos el dialogo
            progDialog.setMessage(getResources().getString(R.string.text_loading_data));
            progDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //Cargamos una imagen temporal
            InputStream is = context.getResources().openRawResource(R.raw.icon_gallery);
            tempBitmap = BitmapFactory.decodeStream(is);

            //Descargamos el archivo JSON en memoria
            return DownLoaderUrl.DownloadUrl(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Haca tenesmos el JSON
            //Log.d("Json: ", result);
            //Si se pudo descargar el JSON, actualizar la UI
            if(!result.isEmpty()){
                updateUI(result);
            }else{
                //No se pudo descargar la lista de directorios(No se pudo actualizar)
                /*ShowMessageUtils.SendNotification(this, getResources().getString(R.string.app_name),
                        getResources().getString(R.string.text_notification_updating_directory_list),"");*/
            }
            //Cerramos el dialogo
            progDialog.dismiss();
        }

        protected void updateUI(String result) {
            JSONObject obj = null;
            try {
                //Parsear el Json
                obj = new JSONObject(result);
                String stat = obj.getString("stat");
                //Log.i("Stat", stat);
                //Si se pudo recibir correctamente la solicitud del servidor
                if(stat.equals("ok")){
                    //Obtener el array de albums
                    JSONObject photosets = obj.getJSONObject("photosets");
                    JSONArray arrayPhotoset = photosets.getJSONArray("photoset");
                    //Lista de directorios
                    ArrayList<Directory> arrayListDir = new ArrayList<>();

                    //Getting json objects inside array
                    for(int i=0;i<arrayPhotoset.length();i++){
                        JSONObject album = arrayPhotoset.getJSONObject(i);
                        //Datos para esta activity
                        String id = album.getString("id");
                        String primary = album.getString("primary");
                        String photos = "Fotos: " + album.get("photos");
                        String count_views = "Visitas: " + album.getString("count_views");
                        String count_comments = "Comentarios: " + album.getString("count_comments");
                        //Datos para obtener la url a la foto primary
                        String farm = album.getString("farm");
                        String server = album.getString("server");
                        String secret = album.getString("secret");

                        JSONObject titles = album.getJSONObject("title");
                        String conten = titles.getString("_content");
                        //Cargamos los datos en el array
                        arrayListDir.add(new Directory(id, primary, farm, server, secret,
                                tempBitmap, "", conten, photos, count_views, count_comments));
                    }

                    //Se instancia la clase adaptador personalizado
                    DirectoryAdapter adapter = new DirectoryAdapter(context, arrayListDir);
                    listViewDirectory.setAdapter(adapter);

                    listViewDirectory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Evento de manejo del ListView
                            onClickListView(parent, view, position, id);
                        }
                    });

                    //Iniciar descarga de imagenes primaria de cada album
                    //Un solo hilo de descarga de imagenes
                    DownloadBitmapPrimaryAsyncTask  dBitmap = new DownloadBitmapPrimaryAsyncTask(arrayListDir, listViewDirectory);
                    dBitmap.execute();

                }else{
                    //No se pudo descargar la lista de directorios(No se pudo actualizar)
                    ShowMessageUtils.SendNotification(context, getResources().getString(R.string.app_name),
                            getResources().getString(R.string.text_notification_no_update_directory_list),"");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    } //end class DownloadJSONAsyncTask

    /**
     * Clase interna para la descarga de las imagenes primarias de cada album
     */
    private class DownloadBitmapPrimaryAsyncTask extends AsyncTask<Void, Void, Void> {
        private ArrayList<Directory> arrayListDirectory;
        private ListView listView;

        public DownloadBitmapPrimaryAsyncTask(ArrayList<Directory> list, ListView view) {
            this.arrayListDirectory = list;
            this.listView = view;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Descargar la imagen en segundo plano
            for (int i = 0; i < arrayListDirectory.size(); i++) {
                Directory d = arrayListDirectory.get(i);
                //Descargar la imagen
                int count;
                try {
                    //29818203866_30ec46b976_s.jpg
                    String urlStr = d.getUrlToSmallPhoto();
                    //String FILE = urlStr.substring(urlStr.lastIndexOf("/")+1, urlStr.length());
                    String FILE = d.getId() + ".jpg" ;

                    URL url = new URL(urlStr);
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(AppConfiguration.IMAGES_ALBUM_DIR + FILE);

                    Bitmap bmp = null;
                    byte data[] = new byte[1024];
                    while ((count = input.read(data)) != -1) {
                        // writing data to file
                        output.write(data, 0, count);
                    }

                    //Cargar y mostrar la imagen descargada en el ListView
                    publishProgress();

                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //Le seteamos la imagen correspondiente
            File parentDir = new File(AppConfiguration.IMAGES_ALBUM_DIR);
            File[] files = parentDir.listFiles();
            for (File file : files) {
                String fileAlbum = file.getName();
                String album_id = fileAlbum.substring(0, fileAlbum.indexOf("."));
                //Log.d("fileAlbum", album_id);
                //Le seteamos la imagen correspondiente
                DirectoryAdapter adapter = (DirectoryAdapter) listView.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    Directory d = adapter.getItem(i);
                    //Si el directorio es igual al pasado por parametro, le asignamos la foto
                    if(d.getId().equals(album_id)){
                        //seteo
                        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        adapter.getItem(i).setBitmap(myBitmap);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            //Al finalizar, actualizar la DB Local
            UpdateDatabaseAsyncTask uDB = new UpdateDatabaseAsyncTask(DirectoryListActivity.this, listViewDirectory);
            uDB.execute();
        }

    } //end class DownloadBitmapPrimaryAsyncTask


    /**
     * Clase interna para el manejo de la actualizacion de la DB
     */
    private class UpdateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private ListView listView;
        private Context context;
        private ArrayList<Directory> arrayList;

        public UpdateDatabaseAsyncTask(Context context, ListView list) {
            super();
            this.context = context;
            this.listView = list;
            this.arrayList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            //Cargamos los valores en una lista
            DirectoryAdapter adapter = (DirectoryAdapter) listView.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                Directory d = adapter.getItem(i);
                arrayList.add(d);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Si no esta el directorio en la DB local, actualizar la DB
            for (int i = 0; i < arrayList.size(); i++) {
                if (!dbHandler.isDirectoryInsideLocalDB(arrayList.get(i).getId())) {
                    dbHandler.insert(new Directory(arrayList.get(i).getId(), arrayList.get(i).getPrimaryId(),
                            arrayList.get(i).getFarmId(), arrayList.get(i).getServerId(),
                            arrayList.get(i).getPhotoSecret(), arrayList.get(i).getBitmap(), arrayList.get(i).getPathBitmap(),
                            arrayList.get(i).getName(), arrayList.get(i).getPhotos(),
                            arrayList.get(i).getViews(), arrayList.get(i).getComments()));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //canelamo la notificacion cuando ya termino de actualizarse toding
            ShowMessageUtils.CancelNotification(context);
        }
    }

    /**
     * Metodo que carga los datos al ListView desde la DB Local cuando no hay conexion
     * a internet
     */
    private void LoadDataFromDB() {
        ArrayList<Directory> list = dbHandler.getAllDirectory();
        if(list != null){
            //Se instancia la clase adaptador personalizado
            DirectoryAdapter adapter = new DirectoryAdapter(this, list);
            listViewDirectory.setAdapter(adapter);

            listViewDirectory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Evento de manejo del ListView
                    onClickListView(parent, view, position, id);
                }
            });
        }
    }

    private void onClickListView(AdapterView<?> parent, View view, int position, long id) {
        //Obtener el indice del elemento seleccionado de la lista
        Directory item = (Directory) parent.getItemAtPosition(position);
        //Log.d("Album id", item.getId());
        Intent intent = new Intent(DirectoryListActivity.this, PhotoListActivity.class);
        //Pasamos a la activity de Photos el id del album que queremos ver
        intent.putExtra("photoset_id", item.getId());
        intent.putExtra("album_title", item.getName());
        startActivity(intent);
    }
}

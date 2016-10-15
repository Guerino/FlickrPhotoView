package com.tdm.view.photo.flickr.flickrphotoview;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import model.FlickrAPI;
import model.Photo;
import model.PhotoAdapter;
import model.PhotoDatabaseHandler;
import util.AppConfiguration;
import util.DownLoaderUrl;
import util.NetworkUtils;
import util.ShowMessageUtils;

public class PhotoListActivity extends AppCompatActivity {
    private FlickrAPI flickrAPI;
    private ListView listViewPhotos;
    private ProgressDialog progDialog;
    private DownloadJSONAsyncTask downloadJsonTask;
    private String albumID;
    private String albumTitle;
    private PhotoDatabaseHandler dbHandler;
    private SharedPreferences mSettings;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        //iniciamos las preferecencias para esta aplicacion
        mSettings = getSharedPreferences(AppConfiguration.APP_NAME, MODE_PRIVATE);
        if(mSettings.getBoolean("full_screen", false)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        listViewPhotos = (ListView) findViewById(R.id.list_photo_view);

        progDialog = new ProgressDialog(this);

        //Recibimos el id del album para asi poder cargar y mostrar sus fotos
        Intent intent = getIntent();
        albumID = intent.getStringExtra("photoset_id");
        albumTitle = intent.getStringExtra("album_title");

        actionBar.setTitle(albumTitle);

        //Conexion con la db local
        dbHandler = new PhotoDatabaseHandler(this);

        //Verificar primero si hay conexion a internet
        if(NetworkUtils.isWifiConnected(this) || NetworkUtils.isMobileConnected(this)) {
            //Notificacion de actualizacion del listado de directorios
            ShowMessageUtils.SendNotification(this, getResources().getString(R.string.app_name),
                    getResources().getString(R.string.text_notification_updating_photo_list),"");

            //Iniciar tarea de descarga de lista de directorios
            flickrAPI = new FlickrAPI();
            downloadJsonTask = new DownloadJSONAsyncTask(this, listViewPhotos);
            //Descargamos el JSON con la lista de albunes y armamos el ListView
            downloadJsonTask.execute(flickrAPI.getPhotoList(albumID));
        }else{
            LoadDataFromDB();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, DirectoryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            case R.id.menu_order_by:
                // Opcion ordernar por nombre o fecha
                ShowDialogOrderBy();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void ShowDialogOptionsPhoto(Photo p) {
        final Photo photo = p;
        //Creacion del cuadro de dialogo de opciones
        final Dialog dlg = new Dialog(PhotoListActivity.this);
        dlg.setTitle(R.string.dlg_list_actions_photo_title);
        //dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_list_actions_photo);

        ListView list_view_actions = (ListView) dlg.findViewById(R.id.lstv_dialog_action_photo);

        String names[] = {getResources().getString(R.string.dlg_list_action_photo_browser),
                            getResources().getString(R.string.dlg_list_action_photo_comment),
                                getResources().getString(R.string.dlg_list_action_photo_email)};
        //Cargamos el adapter
        ArrayAdapter<String> ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        list_view_actions.setAdapter(ad);

        list_view_actions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Primero hay que saber que item de la lista selecciono el usuario
                switch (position){
                    case 0:
                        //Abrir la foto en el navegador web
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        //Pasamos la URL de la foto
                        browserIntent.setData(Uri.parse(photo.getUrlToPhoto()));
                        startActivity(browserIntent);
                        break;

                    case 1:
                        // Iniciar la actividad de comentarios
                        Intent intent = new Intent(PhotoListActivity.this, CommentViewActivity.class);
                        //Pasar como parametro la imagen seleccionada
                        intent.putExtra("photo_id", photo.getId());
                        intent.putExtra("album_id", photo.getAlbumId());
                        intent.putExtra("photo_url", photo.getUrlToPhoto());
                        intent.putExtra("photo_url_medium",photo.getUrlToMediumPhoto());
                        intent.putExtra("photo_title", photo.getTitle());
                        intent.putExtra("album_title", albumTitle);

                        startActivity(intent);
                        break;

                    case 2:
                        //Compartir la foto por email
                        sendEmail(photo.getUrlToPhoto());
                        break;
                }
            }
        });

        Button btn_cancelar = (Button) dlg.findViewById(R.id.btn_cancel);
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        dlg.show();
    }

    //Metodo para compartir la URL de la foto por email
    private void sendEmail(String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO)
                .setData(new Uri.Builder().scheme("mailto").build())
                .putExtra(Intent.EXTRA_EMAIL, new String[]{ "Javier Güerino Palacios <pjg@outlook.com.ar>" })
                .putExtra(Intent.EXTRA_SUBJECT, "Mira mi foto en Flickr")
                .putExtra(Intent.EXTRA_TEXT, text);

        ComponentName emailApp = intent.resolveActivity(getPackageManager());
        ComponentName unsupportedAction = ComponentName.unflattenFromString("com.android.fallback/.Fallback");
        //Si existe una aplicacion de correo configurada, lanzamos el intent
        if (emailApp != null && !emailApp.equals(unsupportedAction)) {
            try {
                // Needed to customise the chooser dialog title since it might default to "Share with"
                // Note that the chooser will still be skipped if only one app is matched
                Intent chooser = Intent.createChooser(intent, "Enviar email con");
                startActivity(chooser);
                return;
            } catch (ActivityNotFoundException ignored) {
            }
        }else{
            //Notificamos al usuario en caso de no ser posible enviar el email
            Toast.makeText(this, R.string.text_no_email_app, Toast.LENGTH_LONG).show();
        }
    }

    private void ShowDialogOrderBy() {
        //Creacion del cuadro de dialogo de opciones ordenar por
        final Dialog dlg = new Dialog(PhotoListActivity.this);
        dlg.setTitle(R.string.dlg_order_by_photo_title);
        //dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_orderby_photo);

        Button btnCancelar = (Button) dlg.findViewById(R.id.btn_cancel);
        RadioButton rbtnDate = (RadioButton) dlg.findViewById(R.id.rbtn_date);
        RadioButton rbtnNameFile = (RadioButton) dlg.findViewById(R.id.rbtn_name_file);

        //Seteo de eventos
        rbtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ordenar por fecha de creacion
                //1-Obtenemos el adapter del ListView
                PhotoAdapter adapter = (PhotoAdapter)listViewPhotos.getAdapter();
                //2-Lo ordenamos
                adapter.sort(new Comparator<Photo>() {
                    public int compare(Photo arg0, Photo arg1) {
                        return arg0.getDateTaken().compareTo(arg1.getDateTaken());
                    }
                });
                //3-Se lo seteamos de nuevo
                listViewPhotos.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //Cerramos el dialogo
                dlg.dismiss();
            }
        });

        rbtnNameFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ordenar por nombre de archivo
                //1-Obtenemos el adapter del ListView
                PhotoAdapter adapter = (PhotoAdapter)listViewPhotos.getAdapter();
                //2-Lo ordenamos
                adapter.sort(new Comparator<Photo>() {
                    public int compare(Photo arg0, Photo arg1) {
                        return arg0.getTitle().compareTo(arg1.getTitle());
                    }
                });
                //3-Se lo seteamos de nuevo
                listViewPhotos.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                //Cerramos el dialogo
                dlg.dismiss();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });

        //Mostrar el dialogo
        dlg.show();
    }

    /**
     * Clase interna para la descarga del JSON correspondiente a las imagenes
     * dentro de un album
     */
    private class DownloadJSONAsyncTask extends AsyncTask<String, Void, String> {
        private Context context;
        private ListView listView;
        private Bitmap tempBitmap;

        public DownloadJSONAsyncTask(Context context, ListView listView) {
            this.context = context;
            this.listView = listView;
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
            InputStream is = context.getResources().openRawResource(R.raw.icon_image);
            tempBitmap = BitmapFactory.decodeStream(is);
            //Descargamos el archivo JSON en memoria
            return DownLoaderUrl.DownloadUrl(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Haca tenesmos el JSON
            //Log.d("JSON: ", result);
            JSONObject obj = null;
            //Parsear el Json
            try {
                obj = new JSONObject(result);
                String stat = obj.getString("stat");
                //Si se pudo recibir correctamente la solicitud del servidor
                if (stat.equals("ok")) {
                    //Log.d("JSON-OK ", result);
                    //Obtener el array de albums
                    JSONObject photoset = obj.getJSONObject("photoset");
                    JSONArray arrayPhoto = photoset.getJSONArray("photo");
                    //Adapter personalizado
                    ArrayList<Photo> arrayListPhotos = new ArrayList<>();
                    //Obtenemos vector de imagenes
                    for (int i = 0; i < arrayPhoto.length(); i++) {
                        JSONObject photo = arrayPhoto.getJSONObject(i);
                        String id = photo.getString("id");
                        String title = photo.getString("title");
                        //Datos para obtener la url a la foto primary
                        String farm = photo.getString("farm");
                        String server = photo.getString("server");
                        String secret = photo.getString("secret");

                        long offset = Timestamp.valueOf("2009-01-01 00:00:00").getTime();
                        long end = Timestamp.valueOf("2015-06-01 00:00:00").getTime();
                        long diff = end - offset + 1;
                        Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));

                        String date = rand.toString();
                        date = date.substring(0,date.indexOf("."))+"Hs";
                        //Log.d("Date-rnd", date);

                        //Cargamos los datos en el array
                        arrayListPhotos.add(new Photo(id, tempBitmap, title,
                                "", date, "", farm, server, secret, albumID));
                    }

                    //Se instancia la clase adaptador personalizado
                    PhotoAdapter adapter = new PhotoAdapter(context, arrayListPhotos);
                    listView.setAdapter(adapter);

                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            //Evento de manejo del ListView
                            onClickListView(parent, view, position, id);
                            return false;
                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Abrir la foto en el navegador web
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            PhotoAdapter adapter = (PhotoAdapter)listViewPhotos.getAdapter();
                            //Pasamos la URL de la foto
                            browserIntent.setData(Uri.parse(adapter.getItem(position).getUrlToPhoto()));
                            startActivity(browserIntent);

                        }
                    });

                    //Iniciar descarga de imagenes primaria de cada album
                    DownloadBitmapAsyncTask  dBitmap = new DownloadBitmapAsyncTask(arrayListPhotos, listViewPhotos);
                    dBitmap.execute();

                }else{
                    //No se pudo descargar la lista de fotos(No se pudo actualizar)
                    ShowMessageUtils.SendNotification(context, getResources().getString(R.string.app_name),
                            getResources().getString(R.string.text_notification_no_update_photo_list),"");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //SendNotification("La lista de fotos fue actualizada correctamente.");
            //Cerramos el dialogo
            progDialog.dismiss();
        }
    } //end class DownloadJSONAsyncTask

    /**
     * Clase interna para la descarga de las imagenes
     */
    private class DownloadBitmapAsyncTask extends AsyncTask<Void, Void, Void> {
        private ArrayList<Photo> listPhotos;
        private ListView listView;

        public DownloadBitmapAsyncTask(ArrayList<Photo> list, ListView listView ) {
            this.listView = listView;
            this.listPhotos = list;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Descargar la imagen en segundo plano
            for (int i = 0; i < listPhotos.size(); i++) {
                Photo p = listPhotos.get(i);
                //Descargar la imagen
                int count;
                try {
                    //29818203866_30ec46b976_s.jpg
                    String urlStr = p.getUrlToSmallPhoto();
                    //Log.d("urlStr", urlStr);
                    String nameFile = urlStr.substring(urlStr.lastIndexOf("/")+1, urlStr.indexOf("_"));
                    String FILE = albumID + "_" + nameFile + ".jpg" ;

                    URL url = new URL(urlStr);
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(AppConfiguration.IMAGES_PHOTO_DIR + FILE);

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
            PhotoAdapter adapter = (PhotoAdapter) listView.getAdapter();
            //Le seteamos la imagen correspondiente
            File parentDir = new File(AppConfiguration.IMAGES_PHOTO_DIR);
            File[] files = parentDir.listFiles();
            for (File file : files) {
                String fileAlbum = file.getName();
                //PHOTO-FILE: 72157673063872941_29227073963.jpg
                //Cargar la foto por album_id y por photo_id
                String album_id = fileAlbum.substring(0, fileAlbum.indexOf("_"));
                //Log.d("fileAlbum", fileAlbum); //72157673063872941_29818203866.jpg
                //Log.d("albumID", albumID); //72157673063872941
                //Log.d("album_id", album_id); //7215767306387294

                if(albumID.equals(album_id)) {
                    String photo_id = fileAlbum.substring(fileAlbum.indexOf("_")+1, fileAlbum.indexOf("."));
                   // Log.d("photo_id", photo_id);
                    //Le seteamos la imagen correspondiente
                    for (int i = 0; i < adapter.getCount(); i++) {
                        Photo p = adapter.getItem(i);
                        //Si la foto es igual al pasado por parametro, le asignamos la foto
                        if(p.getId().equals(photo_id)) {
                            //seteo
                            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            adapter.getItem(i).setBitmap(myBitmap);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

            }
        }

        @Override
        protected void onPostExecute(Void result) {
            //Al finalizar, actualizar la DB Local
            UpdateDatabaseAsyncTask uDB = new UpdateDatabaseAsyncTask(listView);
            uDB.execute();
        }

    } //end class DownloadBitmapPrimaryAsyncTask

    /**
     * Clase interna para el manejo de la actualizacion de la DB
     */
    private class UpdateDatabaseAsyncTask extends AsyncTask<Void, Void, Void> {
        private ListView listView;
        private ArrayList<Photo> arrayList;

        public UpdateDatabaseAsyncTask(ListView list) {
            super();
            this.listView = list;
            this.arrayList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            //Cargamos los valores en una lista
            PhotoAdapter adapter = (PhotoAdapter) listView.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                Photo p = adapter.getItem(i);
                arrayList.add(p);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Si no esta el directorio en la DB local, actualizar la DB
            for (int i = 0; i < arrayList.size(); i++) {
                if (!dbHandler.isPhotoInsideLocalDB(arrayList.get(i).getId())) {
                    dbHandler.insert(new Photo(arrayList.get(i).getId(), arrayList.get(i).getBitmap(),
                            arrayList.get(i).getTitle(), arrayList.get(i).getViews(),
                            arrayList.get(i).getDateTaken(), arrayList.get(i).getDescription(),
                            arrayList.get(i).getFarmId(), arrayList.get(i).getServerId(),
                            arrayList.get(i).getPhotoSecret(), arrayList.get(i).getAlbumId()));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //cancelamos la notificacion cuando ya termino de actualizarse toding
            ShowMessageUtils.CancelNotification(PhotoListActivity.this);
        }
    }

    /**
     * Metodo que carga los datos al ListView desde la DB Local cuando no hay conexion
     * a internet
     */
    private void LoadDataFromDB() {
        ArrayList<Photo> list = dbHandler.getPhotosByAlbum(albumID);
        if(list != null){
            //Se instancia la clase adaptador personalizado
            PhotoAdapter adapter = new PhotoAdapter(this, list);
            listViewPhotos.setAdapter(adapter);

            listViewPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Evento de manejo del ListView
                    onClickListView(parent, view, position, id);
                }
            });
        }
    }

    private void onClickListView(AdapterView<?> parent, View view, int position, long id) {
        //le pasamos el photo_id
        Photo p = (Photo)parent.getItemAtPosition(position);
        //Abrimos el cuadro de dialogo
        ShowDialogOptionsPhoto(p);
    }


} //end class PhotoActivity

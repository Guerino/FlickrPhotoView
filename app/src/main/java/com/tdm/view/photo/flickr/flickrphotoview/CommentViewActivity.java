package com.tdm.view.photo.flickr.flickrphotoview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import model.FlickrAPI;
import util.AppConfiguration;
import util.DownLoaderUrl;

public class CommentViewActivity extends AppCompatActivity {
    private ImageView imagePhoto;
    private ProgressBar progressBarPhotoLoad;
    private Button btnLoadMore;
    private String albumId;
    private String photoId;
    private String photoURL;
    private String photoMediumURL;
    private String photoTitle;
    private String albumTitle;
    private FlickrAPI flickrAPI;
    private ProgressDialog progDialog;
    private DownloadPhotoAsyncTask downloadPhoto;
    private DownloadCommentsJSONAsyncTask downloadCommentsTask;
    private ViewGroup relativeLayoutComments;
    private int commentId;
    private SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_view);

        //iniciamos las preferecencias para esta aplicacion
        mSettings = getSharedPreferences(AppConfiguration.APP_NAME, MODE_PRIVATE);
        if(mSettings.getBoolean("full_screen", false)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        imagePhoto = (ImageView)findViewById(R.id.img_photo);
        progressBarPhotoLoad = (ProgressBar)findViewById(R.id.progressBar_photo);
        btnLoadMore = (Button)findViewById(R.id.btn_load_more);
        relativeLayoutComments = (ViewGroup) findViewById(R.id.relative_layout_comments);
        commentId = 0;

        // Get the Drawable custom_progressbar
        /*Drawable draw= getDrawable(R.drawable.custom_progressbar);
        // set the drawable as progress drawavle
        progressBarPhotoLoad.setProgressDrawable(draw);*/

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Recibimos los parametros necesarios
        Intent intent = getIntent();
        photoId = intent.getStringExtra("photo_id");
        albumId = intent.getStringExtra("album_id");
        photoURL = intent.getStringExtra("photo_url");
        photoMediumURL = intent.getStringExtra("photo_url_medium");
        photoTitle = intent.getStringExtra("photo_title");
        albumTitle = intent.getStringExtra("album_title");

        actionBar.setTitle(photoTitle);

        //Iniciamos la descarga de la foto seleccionada por el usuario
        downloadPhoto = new DownloadPhotoAsyncTask(imagePhoto);
        downloadPhoto.execute(photoMediumURL);

        //Iniciamos la descarga de la lista de comentarios
        flickrAPI = new FlickrAPI();
        progDialog = new ProgressDialog(this);
        downloadCommentsTask = new DownloadCommentsJSONAsyncTask(this);
        downloadCommentsTask.execute(flickrAPI.getPhotoComments(photoId));

        //Seteamos el evento onClick para el boton cargar mas
        btnLoadMore.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                onClickBtnLoadMore(v);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.directory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Volver a la PhotoListActivity
                Intent intent = new Intent(this, PhotoListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //Le pasamos a la activity de Photos el id del album del cual era esta foto
                intent.putExtra("photoset_id", albumId);
                intent.putExtra("album_title", albumTitle);

                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void onClickBtnLoadMore(View v){
        //Cargamos una imagen temporal
        InputStream is = getResources().openRawResource(R.raw.icon_user);
        addComment(BitmapFactory.decodeStream(is), "Juan Matio", "Muy lindo, lalala alalalollksa jalsdkalk adl adlak djladla dl");
    }

    public void addComment(Bitmap bitmapUser, String userName, String comment) {
        View commentView = LayoutInflater.from(this).inflate(R.layout.item_comment_list, relativeLayoutComments, false);

        ImageView imageUser = (ImageView)commentView.findViewById(R.id.img_user);
        TextView user = (TextView)commentView.findViewById(R.id.lbl_user_name);
        TextView textView = (TextView)commentView.findViewById(R.id.lbl_comment_user);

        imageUser.setImageBitmap(bitmapUser);
        user.setText(userName);
        textView.setText(comment);

        RelativeLayout.LayoutParams lpView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        if(commentId == 0) {
            lpView.addRule(RelativeLayout.BELOW, R.id.lbl_comment);
        }
        else {
            lpView.addRule(RelativeLayout.BELOW, commentId);
            commentView.setId(commentId+1);
        }

        //FUNCIONAAAAAAA!!!!!!!!!!!
        commentId = commentView.getId();

        RelativeLayout.LayoutParams lpViewBtn = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                                            RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpViewBtn.addRule(RelativeLayout.BELOW, commentId);
        btnLoadMore.setLayoutParams(lpViewBtn);

        //agregamos la vista al layout actual(ScrollView)
        relativeLayoutComments.addView(commentView, lpView);
        //Log.i("Id-After", Integer.toString(commentId));

    }

    private class DownloadPhotoAsyncTask extends AsyncTask<String, String, Void> {
        private ImageView imageView;
        private String FILE;

        public DownloadPhotoAsyncTask(ImageView imageView) {
            this.imageView = imageView;
            this.FILE = "temp.jpg";
        }

        @Override
        protected void onPreExecute() {
            //Cargamos una imagen temporal
            InputStream is = getResources().openRawResource(R.raw.icon_image);
            imageView.setImageBitmap(BitmapFactory.decodeStream(is));
            progressBarPhotoLoad.setMax(100);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            //Actualizar la barra de progreso
            progressBarPhotoLoad.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected Void doInBackground(String... params) {
           int count;
            try {
                URL url = new URL(params[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(AppConfiguration.APP_DIR + FILE);

                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();
                // closing streams
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            String imagePath = AppConfiguration.APP_DIR + FILE;
            // setting downloaded into image view
            imageView.setImageDrawable(Drawable.createFromPath(imagePath));
            //imageView.setImageBitmap(bitmap);
            imageView.setAdjustViewBounds(true);
            // Resize the bitmap to 150x100 (width x height)
            //Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 150, 100, true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            progressBarPhotoLoad.setVisibility(View.GONE);
        }
    }

    /**
     * Clase interna para la descarga del JSON correspondiente a las imagenes
     * dentro de un album
     */
    private class DownloadCommentsJSONAsyncTask extends AsyncTask<String, Void, String> {
        private Context context;

        public DownloadCommentsJSONAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            //Mostramos el dialogo
            progDialog.setMessage(getResources().getString(R.string.text_loading_data));
            progDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //Descargamos el archivo JSON en memoria
            return DownLoaderUrl.DownloadUrl(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject obj = null;
            //Parsear el Json
            try {
                obj = new JSONObject(result);
                String stat = obj.getString("stat");
                //Si se pudo recibir correctamente la solicitud del servidor
                if (stat.equals("ok")) {
                    //Obtener el array de comentarios
                    JSONObject comments = obj.getJSONObject("comments");
                    JSONArray arrayComment = comments.getJSONArray("comment");

                    //Cargamos una imagen temporal
                    InputStream is = getResources().openRawResource(R.raw.icon_user);
                    Bitmap temp = BitmapFactory.decodeStream(is);

                    //Recorremos el array de comentarios
                    for (int i = 0; i < arrayComment.length(); i++) {
                        JSONObject comment = arrayComment.getJSONObject(i);
                        String autor = comment.getString("realname");
                        String textComment = comment.getString("_content");
                        //Agregamos el comentario
                        addComment(temp, autor, textComment);
                    }

                    //Mostrar el boton cargar mas
                    //btnLoadMore.setVisibility(View.VISIBLE);

                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            //Cerramos el dialogo
            progDialog.dismiss();
        }
    }
}

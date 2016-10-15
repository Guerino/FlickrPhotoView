package com.tdm.view.photo.flickr.flickrphotoview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;

import util.AppConfiguration;

public class PreferencesActivity extends AppCompatActivity {
    private SharedPreferences mSettings;
    private CheckBox chkFullScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //iniciamos las preferecencias para esta aplicacion
        mSettings = getSharedPreferences(AppConfiguration.APP_NAME, MODE_PRIVATE);

        if(mSettings.getBoolean("full_screen", false)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //Boton atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getResources().getString(R.string.text_settings_title));

        //Controles
        chkFullScreen = (CheckBox)findViewById(R.id.chk_full_screen);
        chkFullScreen.setChecked(mSettings.getBoolean("full_screen", false));
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
                //Guardamos las preferencias antes de volver a la activity anterior
                savePreferences();

                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, DirectoryListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("full_screen", chkFullScreen.isChecked());

        //Comiteamos los cambios
        editor.commit();
    }


}

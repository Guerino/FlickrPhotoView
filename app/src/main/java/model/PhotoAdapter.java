package model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdm.view.photo.flickr.flickrphotoview.R;

import java.util.ArrayList;

/**
 * Created by Guerino on 23/09/2016.
 */

public class PhotoAdapter extends ArrayAdapter<Photo> {
    private final Context contexto;
    private final ArrayList<Photo> arrayPhotos;
    //Clase estatica que sigue el patron holder
    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView views;
        TextView taken;
        TextView description;
    }

    public PhotoAdapter(Context context, ArrayList<Photo> photos) {
        super(context, -1, photos);
        this.contexto = context;
        this.arrayPhotos = photos;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ViewHolder holder;
        //Si el layout no fue inflado anteriormente, inflarlo
        if(item == null || !(item.getTag() instanceof ViewHolder)) {
            //Inflamos el layout
            //1era Optimización: conseguir ahorrar
            // el trabajo de inflar el layout definido cada vez que se muestra un nuevo elemento
            LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.item_photo_list, parent, false);

            //2da Optimizacion: uso del patrón ViewHolder
            holder = new ViewHolder();
            holder.image = (ImageView) item.findViewById(R.id.img_photo);
            holder.title = (TextView) item.findViewById(R.id.lbl_title);
            holder.views = (TextView) item.findViewById(R.id.lbl_views);
            holder.taken = (TextView) item.findViewById(R.id.lbl_date_taken);
            holder.description = (TextView) item.findViewById(R.id.lbl_description);
            //Setear el holder
            item.setTag(holder);
        }else{
            holder = (ViewHolder)item.getTag();
        }

        // Para poder llenar los elementos del Layout de items, necesitamos obtener
        // los datos del directorio que estoy recorriendo en cada iteración de la lista
        Photo photoActual = arrayPhotos.get(position);

        //Seteamos las vistas
        holder.image.setImageBitmap(photoActual.getBitmap());
        holder.title.setText(photoActual.getTitle());
        holder.views.setText(photoActual.getViews());
        holder.taken.setText(photoActual.getDateTaken());
        holder.description.setText(photoActual.getDescription());

        return item;
    }

}

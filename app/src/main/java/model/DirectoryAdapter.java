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
 * Created by Guerino on 22/09/2016.
 */

public class DirectoryAdapter  extends ArrayAdapter<Directory> {
    private final Context contexto;
    private final ArrayList<Directory> arrayDirectory;
    //Clase estatica que sigue el patron holder
    private static class ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView photos;
        public TextView views;
        public TextView comments;
    }

    public DirectoryAdapter(Context context, ArrayList<Directory> directories) {
        super(context, -1, directories);
        this.contexto = context;
        this.arrayDirectory = directories;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ViewHolder holder;
        //Si el layout no fue inflado anteriormente, inflarlo
        if(item == null || !(item.getTag() instanceof ViewHolder)) {
            //Con esta optimización conseguimos ahorrar
            // el trabajo de inflar el layout definido cada vez que se muestra un nuevo elemento
            LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.item_directory_list, parent, false);

            //2da Optimizacion uso del patrón ViewHolder
            holder = new ViewHolder();
            holder.image = (ImageView) item.findViewById(R.id.img_directory);
            holder.name = (TextView) item.findViewById(R.id.lbl_name);
            holder.photos = (TextView) item.findViewById(R.id.lbl_photos);
            holder.views = (TextView) item.findViewById(R.id.lbl_views);
            holder.comments = (TextView) item.findViewById(R.id.lbl_comments);
            //Setear el holder
            item.setTag(holder);
        }else{
            holder = (ViewHolder)item.getTag();
        }

        // Para poder llenar los elementos del Layout de items, necesitamos obtener
        // los datos del directorio que estoy recorriendo en cada iteración de la lista
        Directory directoryActual = arrayDirectory.get(position);

        //Seteamos las vistas
        holder.image.setImageBitmap(directoryActual.getBitmap());
        holder.name.setText(directoryActual.getName());
        holder.photos.setText(directoryActual.getPhotos());
        holder.views.setText(directoryActual.getViews());
        holder.comments.setText(directoryActual.getComments());

        return item;
    }
}

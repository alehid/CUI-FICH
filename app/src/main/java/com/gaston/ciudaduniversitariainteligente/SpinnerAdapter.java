package com.gaston.ciudaduniversitariainteligente;

/**
 * Clase SpinnerAdapter que se encargará de mostrar una lista de iconos y sus respectivos nombres2
 *
 * @author ERic Bastida <eribastida@gmail.com>
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<ItemData> {
    int groupid;
    Activity context;
    ArrayList<ItemData> list;
    LayoutInflater inflater;
    Activity actividad;


    LogginCUI log = new LogginCUI();

    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<ItemData>  list){
        super(context,id,list);
        this.actividad = context;
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;

    }

    public View getView(int position, View convertView, ViewGroup parent ){

        View itemView=inflater.inflate(groupid,parent,false);
        try {
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imgItemSpinner);
            imageView.setImageResource(list.get(position).getImageId());
            TextView textView = (TextView)itemView.findViewById(R.id.txtItemSpinner);
//      Error hecho a proposito para probar el loggin
//            TextView textView = (TextView) itemView.findViewById(R.id.adjust_width);
            textView.setText(list.get(position).getText());
        }catch (Exception e){
            log.registrar(this,"getView",e);
            log.alertar("Ha ocurrido un error en la función getView.",actividad);

        }
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent){
        return getView(position,convertView,parent);

    }
}

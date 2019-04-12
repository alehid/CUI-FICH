package com.gaston.ciudaduniversitariainteligente;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Lautaro on 08/12/2016.
 */
public class ultimasBusquedas extends Fragment {

    private  LogginCUI log = new LogginCUI();
    private BaseDatos CUdb = null;
    private MainActivity oMainActivity = null;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflo mi listView
        final View rootView = inflater.inflate(R.layout.ultimas_busquedas, container, false);

        try {


            //Abro la base de datos
            CUdb = new BaseDatos(getActivity(), "DBBusquedas", null, 1);

            //Obtengo lo que hay en la BD
            SQLiteDatabase db = CUdb.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT nombre, edificio FROM Busquedas", null);
            c.moveToFirst();

            //Creo el adapter, si hay busquedas registradas
            if (c.getCount() > 0) {
                String[] array = new String[c.getCount()];
                int t = 0;
                do {
                    array[t] = c.getString(0) + ", " + c.getString(1);
                    t++;
                } while (c.moveToNext());

                ListAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_listview, array);

                final ListView listView = (ListView) rootView.findViewById(R.id.listView);


                //ItemClickListener para los elementos del listView, para hacer una busqueda desde aquí
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String itemClick = listView.getItemAtPosition(i).toString();
                        TextView texto = view.findViewById(android.R.id.text1);
                        String Nombre = itemClick.substring(0, (itemClick.indexOf(',')));
                        String Edificio = itemClick.substring((itemClick.indexOf(',') + 2), itemClick.length());
                        oMainActivity.mostrarBusqueda(Edificio, Nombre);

                    }
                });

                //Añado el adapter
                listView.setAdapter(adapter);
            }
            //Si todavia no se han hecho busquedas
            else {
                String[] array = {"Aun no se registran busquedas"};
                ListAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_listview, array);
                final ListView listView = (ListView) rootView.findViewById(R.id.listView);
                listView.setAdapter(adapter);
            }


        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de gestionar las últimas busquedas.",getActivity());
        }
        return rootView;
    }

    public void setMainActivity(MainActivity oMA){
        try {
            this.oMainActivity = oMA;
        }catch (Exception e){
            log.registrar(this,"setMainActivity",e);
            log.alertar("Ocurrió un error al momento de asignar la actividad.",getActivity());
        }
    }

}

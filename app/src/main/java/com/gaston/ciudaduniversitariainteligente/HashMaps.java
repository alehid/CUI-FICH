package com.gaston.ciudaduniversitariainteligente;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lautaro on 26/2/2017.
 */
public class HashMaps {

    private LogginCUI log = new LogginCUI();

    private Map<String, LatLngBounds> hashMapBounds = new HashMap<>();  //hashMap con el nombre del edificio y los limites del mismo
    private Map<String, Integer> hashMapID = new HashMap<>(); //hashMap con el nombre del edificio y el plano del mismo

    public HashMaps() {
        try {
            //Edificio 0 - FICH/FCBC

            hashMapBounds.put("ed0_0", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));
            hashMapBounds.put("ed0_1", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));
            hashMapBounds.put("ed0_2", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));
            hashMapBounds.put("ed0_3", new LatLngBounds(new LatLng(-31.640064, -60.673090), new LatLng(-31.639671, -60.671973)));

            //Edificio 1 - FCM
            hashMapBounds.put("ed1_0", new LatLngBounds(new LatLng(-31.639872, -60.670817), new LatLng(-31.639313, -60.670216)));


            //--------------------------------------------------------------------------------------------------------------------//

            hashMapID.put("ed0_0", R.drawable.ed0_0);
            hashMapID.put("ed0_1", R.drawable.ed0_1);
            hashMapID.put("ed0_2", R.drawable.ed0_2);
            hashMapID.put("ed0_3", R.drawable.ed0_3);

            //--------------------------------------------------------------------------------------------------------------------//

        }catch (Exception e){

            log.registrar(this,"HashMaps",e);
            throw e;

        }
    }

    public Map<String, LatLngBounds> getHashMapsBound(){return hashMapBounds;}
    public Map<String, Integer> getHashMapID(){return hashMapID;}
}

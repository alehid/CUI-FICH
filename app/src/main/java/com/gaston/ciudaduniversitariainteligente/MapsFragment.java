package com.gaston.ciudaduniversitariainteligente;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Lautaro on 29/11/2016.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, SensorEventListener {

    // String utilizado para el tracking de errores
    LogginCUI log = new LogginCUI();

    private final int REQUEST_PERMISSION_PHONE_STATE=1;

    public GoogleMap miMapa = null;
    private SensorManager miSensorManager;
    private MarkerOptions miPosicion = null;
    private Marker miPosicionMarcador = null;
    private int cantPisos = 0;
    private int pisoActual = 0;
    //Cantidad de edificios relevados
    private int cantidad_edificios = 2;
    //Vector de polilineas, cada elemento será una polilinea para un piso
    private Vector<PolylineOptions> misPolilineas = new Vector<>();
    //Vector de marcadores por piso, dos marcadores por polilinea por piso
    private Vector<MarkerOptions> marcadoresPiso = new Vector<>();
    //uno en cada extremo

    //Vector de nodos para mostrar nodos sueltos (baños, bares, oficinas, etc)
    private Vector<MarkerOptions> misMarcadores = new Vector<>();
    //Vector de overLays, los planos de cada edificio
    private Vector<Vector<GroundOverlayOptions>> misOverlays = new Vector<>();
    //El vector de afuera es para los pisos, cada elemento es un piso
    //Cada elemento es un vector que tiene overlays de 1 o mas edificios
    //Una polilinea puede pasar por mas de un edificio en un piso

    private HashMaps miHashMaps = new HashMaps();
    private Map<String, LatLngBounds> hashMapBounds = miHashMaps.getHashMapsBound();
    private Map<String, Integer> hashMapID = miHashMaps.getHashMapID();
    private Map<LatLng, Integer> hashMapImagenes = new HashMap<>();
    private float angle = 0;
    private double lat;
    private double lon;

    private PolylineOptions polilinea;

    Location initialLocationFacultad;

    public MapsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        try {



            MapFragment fragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
            fragment.getMapAsync(this);

            //LocationManager
            LocationManager mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            MyLocationListener mlocListener = new MyLocationListener();
            mlocListener.setMainActivity(this);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                showPhoneStatePermission();

            }else {

                turnGPSOn();

                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, (android.location.LocationListener) mlocListener);
                //SensorManager
                SensorManager mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
                mSensorManager.registerListener((SensorEventListener) this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 1000000);
                this.miSensorManager = mSensorManager;

            }

        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al inicializar los mapas.",getActivity());
        }
        return rootView;

    }

    private void turnGPSOn(){
        try {

            String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (!provider.contains("gps")) { //if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                getActivity().sendBroadcast(poke);
            }
        }catch (Exception e){
            log.registrar(this,"turnGPSOn",e);
            log.alertar("Ocurrió un error al momento de encender el GPS.",getActivity());
        }
    }

    private void turnGPSOff(){
        try{
            String provider = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if(provider.contains("gps")){ //if gps is enabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                getActivity().sendBroadcast(poke);


            }
        }catch (Exception e){
            log.registrar(this,"turnGPSOff",e);
            log.alertar("Ocurrió un error al momento de apagar el GPS.",getActivity());
        }
    }

    private void showPhoneStatePermission() {
        try {
            int permissionCheck_1 = ContextCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCheck_2 = ContextCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);


            if (permissionCheck_1 != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showExplanation("Permiso necesario", "Este permiso permite la interacción con los mapas y su hubicación", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
                } else {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
                }
            }

            if (permissionCheck_2 != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    showExplanation("Permiso necesario", "Este permiso permite la interacción con los mapas y su hubicación", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
                } else {
                    requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_PHONE_STATE);
                }
            }

        }catch (Exception e){
            log.registrar(this,"showPhoneStatePermission",e);
            log.alertar("Ocurrió un error al momento de mostrar el dialogo de los permisos.",getActivity());
        }


    }

    @Override
    public void onRequestPermissionsResult( int requestCode,  String permissions[],    int[] grantResults) {
        try{
            switch (requestCode) {
                case REQUEST_PERMISSION_PHONE_STATE:
                    if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
            }

        }catch (Exception e){
            log.registrar(this,"onRequestPermissionsResult",e);
            log.alertar("Ocurrió un error al momento de mostrar el resultado del pedido del permiso.",getActivity());

        }
    }

    private void showExplanation(String title,String message,  final String permission, final int permissionRequestCode) {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestPermission(permission, permissionRequestCode);
                        }
                    });
            builder.create().show();
        }catch (Exception e){
            log.registrar(this,"showExplanation",e);
            log.alertar("Ocurrió un error al momento de mostrar la explicación.",getActivity());

        }
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        try {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{permissionName}, permissionRequestCode);
        } catch (Exception e) {
            log.registrar(this, "requestPermission", e);
            log.alertar("Ocurrió un error al momento de solicitar el permiso "+ permissionName, getActivity());

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {


            miMapa = googleMap;

            //Esto es para armar el grafo, clickeando encima del overlay y viendo la lat y long del punto
            miMapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.d("Prueba", latLng.latitude + ", " + latLng.longitude);
                }
            });

            //Hago mi propia InfoWindow, para poder mostrar una imagen del nodo cuando hago click en el y ver el lugar que está señalado
            miMapa.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                    ImageView imagen = (ImageView) v.findViewById(R.id.imageView);
                    TextView titulo = (TextView) v.findViewById(R.id.titulo);

                    //Busco en el mapa por ese punto, si tiene imagen la agrego
                    if (hashMapImagenes.containsKey(marker.getPosition())) {
                        imagen.setImageResource(hashMapImagenes.get(marker.getPosition()));
                    }

                    titulo.setText(marker.getTitle());
                    return v;
                }
            });

            //Muevo la camara hasta mi posicion y agrego un marcador allí
            LatLng position = new LatLng(this.lat, this.lon);
//            miMapa.moveCamera(CameraUpdateFactory.newLatLng(position));
//            miMapa.moveCamera(CameraUpdateFactory.zoomTo(18));
            miPosicion = new MarkerOptions().position(new LatLng(this.lat, this.lon)).title("Usted está aquí");
            miPosicionMarcador = miMapa.addMarker(miPosicion);

            /* ----------------- Added by Gaston --------------------------------------- */
//            LatLng initialLocationString = null;
//            if (misPolilineas != null)
//                initialLocationString = misPolilineas.get(0).getPoints().get(0);
//
//            if (initialLocationString != null) {
//                //initialLocationString = initialLocationString.substring(1,initialLocationString.length()-1);
//                Log.i("DEBUG--APP", "Longitud: " + initialLocationString.longitude);
//                Log.i("DEBUG--APP", "Latitud: " + initialLocationString.latitude);
//                initialLocationFacultad = new Location("");
//                initialLocationFacultad.setLatitude(initialLocationString.latitude);
//                initialLocationFacultad.setLongitude(initialLocationString.longitude);
//                //Log.i("DEBUG--APP", "punto 2: " + misPolilineas.get(0).getPoints().toArray()[1]);
//            }

            /* ------------------------------------------------------------------------- */
            //Agrego los marcadores adicionales (Edificios, baños, bares,etc), si los hay
            Log.d("DEBUG-APP:", "SIZE: " + misMarcadores.size());
            for (int i = 0; i < misMarcadores.size(); i++) {
                Log.d("DEBUG-APP:", "I: " + i);
                String texto;
                if (pisoActual == 0) {
                    texto = "Planta Baja";
                } else {
                    texto = "Piso " + pisoActual;
                }
                if (misMarcadores.elementAt(i).getTitle().contains(texto)) {
                    miMapa.addMarker(misMarcadores.elementAt(i));
                }
            }

            //Agrego polilinea si la hay
            Log.d("DEBUG-APP:", "POLILINEA");
            if (misPolilineas.size() != 0) {
                miMapa.addPolyline(misPolilineas.elementAt(pisoActual));
                Log.d("DEBUG-APP:", "POLILINEA1");
                miMapa.addMarker(marcadoresPiso.elementAt(2 * pisoActual));
                Log.d("DEBUG-APP:", "POLILINEA2");
                miMapa.addMarker(marcadoresPiso.elementAt(2 * pisoActual + 1));
                Log.d("DEBUG-APP:", "POLILINEA3");
            }

            //Agrego los overlays
            Log.d("DEBUG-APP:", "OVERLAYS: " + misOverlays.size());
            Log.d("DEBUG-APP:", "piso Actual: " + pisoActual);
            if (misOverlays.size() != 0) {
                for (int i = 0; i < misOverlays.elementAt(pisoActual).size(); i++) {
                    miMapa.addGroundOverlay(misOverlays.elementAt(pisoActual).elementAt(i));
                }
                Log.d("DEBUG-APP:", "ANTES " + misOverlays.elementAt(pisoActual).size());
                if (misOverlays.elementAt(pisoActual).size() > 0)
                    miMapa.moveCamera(CameraUpdateFactory.newLatLngBounds(misOverlays.elementAt(pisoActual).elementAt(0).getBounds(), 0));
                Log.d("DEBUG-APP:", "DESPUES");
                miMapa.moveCamera(CameraUpdateFactory.zoomTo(18));
            }


        }catch (Exception e){
            log.registrar(this,"onMapReady",e);
            log.alertar("Ocurrió un error al momento cargar el mapa.",getActivity());

        }
    }

    //Setters, getters y demas utilidades
    public void setLat(double l) {
        this.lat = l;
    }

    public void setLon(double l) {
        this.lon = l;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    public int getCantPisos(){
        return cantPisos;
    }

    public void setPisoActual(int p){this.pisoActual = p;}

    public int getPisoActual(){return pisoActual;}

    public boolean modoPolilinea() throws Exception {
        boolean resultado = false;
        try{
            resultado =  !misPolilineas.isEmpty();
        }
        catch (Exception e){

            log.registrar(this,"modoPolilinea",e);
            log.alertar("Error sl consultar el estado de modo polilinea.",getActivity());


        }
        return resultado;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {

            if(miMapa != null) {

                synchronized (this) {
                    switch (sensorEvent.sensor.getType()) {
                        case Sensor.TYPE_ORIENTATION:
                            float degree = Math.round(sensorEvent.values[0]);
                            //Si el angulo de rotación con respecto a la rotación de la muestra anterior es mayor a 20
                            //roto la camara, sino no porque sino baila mucho
                            if (Math.abs(degree - angle) > 30) {
                                angle = degree;
                                CameraPosition oldPos = miMapa.getCameraPosition();
                                CameraPosition pos = CameraPosition.builder(oldPos).bearing(degree).build();
                                miMapa.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
                            }
                    }
                }
            }
        }catch (Exception e){

            log.registrar(this,"onSensorChanged",e);
            log.alertar("Ocurrió un error al momento de gestionar la información ocacionada por el cambio de valores del sensor GPS.",getActivity());

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //Limpio el mapa de polilineas, marcadores, etc
    public void limpiarMapa() {
        try{
            miPosicionMarcador.remove();
            misPolilineas.clear();
            marcadoresPiso.clear();
            misOverlays.clear();
            miMapa.clear();
            miMapa.addMarker(miPosicion);
            setPisoActual(0);

        }catch (Exception e){
            log.registrar(this,"limpiarMapa",e);
            log.alertar("Error al limpiar el mapa.",getActivity());

        }

    }

    //Actualizo mi posición si me moví. Quito mi marcador y lo pongo en donde corresponde
    @TargetApi(Build.VERSION_CODES.M)
    void actualizaPosicion() {
        try {
            LatLng position = new LatLng(this.lat, this.lon);
//            miMapa.moveCamera(CameraUpdateFactory.newLatLng(position));
            miPosicion.position(position);
            miPosicionMarcador.remove();
            miPosicionMarcador = miMapa.addMarker(miPosicion);

            /* --------------- Changes added by Gaston ------------------------------------- */
//            Location miLocation = new Location("");
//            miLocation.setLongitude(position.longitude);
//            miLocation.setLatitude(position.latitude);
//            //Log.i("DEBUG--APP", "Longitud: " + this.log);
//            //Log.i("DEBUG--APP", "Latitud: " + this.lat);
//            float Distancia = miLocation.distanceTo(initialLocationFacultad);
//            if (Distancia > 100) {
//                //String url ="http://maps.googleapis.com/maps/api/directions/json?origin="+miLocation.getLatitude()+","+miLocation.getLongitude()+"&destination="+initialLocationFacultad.getLatitude()+","+initialLocationFacultad.getLongitude();
//
//                Toast.makeText(getContext(), "Se encuentra fuera de la Facultad", Toast.LENGTH_SHORT).show();
//
//                ArrayList<LatLng> points = new ArrayList<LatLng>();
//
//                points.add(new LatLng(miLocation.getLatitude(),miLocation.getLongitude()));
//                points.add(new LatLng(initialLocationFacultad.getLatitude(), initialLocationFacultad.getLongitude()));
//
//                PolylineOptions polilinea = new PolylineOptions();
//                polilinea.addAll(points);
//                polilinea.width(5);
//                polilinea.color(Color.RED);
//
//                if (polilinea != null){
//                    Toast.makeText(getContext(), "Agrego", Toast.LENGTH_SHORT).show();
//                    miMapa.addPolyline(polilinea);
//                }
//
//                //miMapa.addPolyline(polilinea);
//            }
//            Log.i("DEBUG--APP", "Distancia en metros: " + Distancia);
            /* ----------------------------------------------------------------------------- */

            if (!misPolilineas.isEmpty() && pisoActual + 1 <= misPolilineas.size()) {
                cambiarPolilinea(pisoActual);
            } else if (pisoActual + 1 > misPolilineas.size() && !misPolilineas.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Su objetivo está en un piso inferior", Toast.LENGTH_LONG).show();
            }
            if (!misMarcadores.isEmpty() && pisoActual + 1 <= misMarcadores.size()) {
                cambiarNodos(pisoActual);
            } else if (pisoActual + 1 > misMarcadores.size() && !misMarcadores.isEmpty()) {
                Toast.makeText(getActivity().getApplicationContext(), "Su objetivo está en un piso inferior", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){

            log.registrar(this,"actualizaPosicion",e);
            log.alertar("Error al actualizar la posición.",getActivity());

        }

    }

    //Obtengo mi latitud y longitud en un objeto LatLng
    public LatLng getPosicion()  {
        LatLng resultado = null;
        try {
            resultado =  new LatLng(this.lat, this.lon);
        }catch (Exception e){
            log.registrar(this,"getPosicion",e);
            log.alertar("Error al tratar de obtener la posición.",getActivity());

        }
        return resultado;

    }

    //Recibo un vector de puntos y creo un polilinea con ellos
    public void dibujaCamino(Vector<Punto> path) {
        try {
            misPolilineas.clear();
            misMarcadores.clear();
            marcadoresPiso.clear();
            cantPisos = 0;
            Vector<String> edificios = new Vector<>();

            //Veo cuantos pisos hay
            for (int i = 0; i < path.size(); i++) {
                if (path.elementAt(i).getPiso() > cantPisos) {
                    cantPisos = path.elementAt(i).getPiso();
                }
            }

            //Creo las polilineas y overlays que voy a usar
            cantPisos = cantPisos + 1;
            for (int i = 0; i < cantPisos; i++) {
                PolylineOptions p = new PolylineOptions().width(5).color(Color.RED);
                Vector<GroundOverlayOptions> g = new Vector<>();
                misPolilineas.add(p);
                misOverlays.add(g);
            }

            //Agrego puntos a las polilineas segun piso e identifico por que edificios y pisos pasa mi polilinea
            for (int i = 0; i < path.size(); i++) {
                misPolilineas.elementAt(path.elementAt(i).getPiso()).add(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()));
                for (int j = 0; j < cantidad_edificios; j++) {
                    //Veo si ese marcador está dentro de algun edificio con el mapa y la funcion dentroDeLimites
                    //Tratar de optimizar esto
                    if (hashMapBounds.containsKey("ed" + j + "_" + path.elementAt(i).getPiso())) {
                        if (dentroDeLimites(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()), hashMapBounds.get("ed" + j + "_" + path.elementAt(i).getPiso()))) {
                            if (!edificios.contains("ed" + j + "_" + path.elementAt(i).getPiso())) {
                                edificios.add("ed" + j + "_" + path.elementAt(i).getPiso());
                            }
                        }
                    }
                }
            }

            //Agrego los overlays a mi vector
            for (int i = 0; i < edificios.size(); i++) {
                if (hashMapID.containsKey(edificios.elementAt(i))) {
                    misOverlays.elementAt(Integer.parseInt(edificios.elementAt(i).substring(edificios.elementAt(i).indexOf("_") + 1)))
                            .add(new GroundOverlayOptions()
                                    .positionFromBounds(hashMapBounds.get(edificios.elementAt(i)))
                                    .image(BitmapDescriptorFactory.fromResource(hashMapID.get(edificios.elementAt(i)))));
                }
            }

            //Busco cuales marcadores por piso voy a tener
            marcadoresPiso.add(new MarkerOptions()
                    .position(new LatLng(path.elementAt(0).getLatitud(), path.elementAt(0).getLongitud()))
                    .title(path.elementAt(0).getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            for (int i = 1; i < path.size() - 1; i++) {
                if (path.elementAt(i).getPiso() != path.elementAt(i + 1).getPiso()) {
                    marcadoresPiso.add(new MarkerOptions()
                            .position(new LatLng(path.elementAt(i).getLatitud(), path.elementAt(i).getLongitud()))
                            .title(path.elementAt(i).getNombre())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    marcadoresPiso.add(new MarkerOptions()
                            .position(new LatLng(path.elementAt(i + 1).getLatitud(), path.elementAt(i + 1).getLongitud()))
                            .title(path.elementAt(i + 1).getNombre())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                }
            }

            marcadoresPiso.add(new MarkerOptions()
                    .position(new LatLng(path.elementAt(path.size() - 1).getLatitud(), path.elementAt(path.size() - 1).getLongitud()))
                    .title(path.elementAt(path.size() - 1).getNombre())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            //Cargo las imagenes en el map
            cargarMapaImagnes(path);
        }catch (Exception e){


            log.registrar(this,"dibujaCamino",e);
            log.alertar("Ocurrió un error al momento de dibujar el camino.",getActivity());

        }
    }

    //Recibo un conjunto de puntos y creo marcadores para todos ellos
    public void mostrarNodos(Vector<Punto> nodos) {
        try {


            misMarcadores.clear();
            misPolilineas.clear();
            marcadoresPiso.clear();
            Vector<String> edificios = new Vector<>();
            cantPisos = 0;
            for (int i = 0; i < nodos.size(); i++) {
                String texto;
                if (nodos.elementAt(i).getPiso() == 0) {
                    texto = "Planta Baja";
                } else {
                    texto = "Piso " + nodos.elementAt(i).getPiso();
                }
                //Cuento la cantidad de pisos en donde encontre lo que busco
                if (nodos.elementAt(i).getPiso() > cantPisos) {
                    cantPisos = nodos.elementAt(i).getPiso();
                }

                //Agrego los marcadores
                LatLng posicion = new LatLng(nodos.elementAt(i).getLatitud(), nodos.elementAt(i).getLongitud());
                MarkerOptions markerOptions = new MarkerOptions().position(posicion );
                markerOptions.title(nodos.elementAt(i).getNombre() + " - " + texto);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                misMarcadores.add( markerOptions );

                for (int j = 0; j < cantidad_edificios; j++) {
                    //Veo si ese marcador está dentro de algun edificio
                    if (hashMapBounds.containsKey("ed" + j + "_" + nodos.elementAt(i).getPiso())) {
                        if (dentroDeLimites(new LatLng(nodos.elementAt(i).getLatitud(), nodos.elementAt(i).getLongitud()), hashMapBounds.get("ed" + j + "_" + nodos.elementAt(i).getPiso()))) {
                            if (!edificios.contains("ed" + j + "_" + nodos.elementAt(i).getPiso())) {
                                edificios.add("ed" + j + "_" + nodos.elementAt(i).getPiso());
                            }
                        }
                    }
                }
            }

            //Creo las polilineas y overlays que voy a usar
            cantPisos = cantPisos + 1;
            for (int i = 0; i < cantPisos; i++) {
                Vector<GroundOverlayOptions> g = new Vector<>();
                misOverlays.add(g);
            }

            //Agrego los overlays a mi vector
            for (int i = 0; i < edificios.size(); i++) {
                if (hashMapID.containsKey(edificios.elementAt(i))) {

                    GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
                    String nombre = edificios.elementAt(i);
                    LatLngBounds limites = hashMapBounds.get(nombre);
                    groundOverlayOptions.positionFromBounds(limites);
                    //FIX
                    Integer id_img = hashMapID.get(edificios.elementAt(i));

                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource( id_img  );
                    groundOverlayOptions.image(bitmapDescriptor);

                    misOverlays.elementAt(
                            Integer.parseInt(edificios.elementAt(i).substring(edificios.elementAt(i).indexOf("_") + 1)))
                            .add(groundOverlayOptions);
                }
            }

            //Cargo imagenes en el map
            cargarMapaImagnes(nodos);
        }catch (Exception e){


            log.registrar(this,"mostrarNodos",e);
            log.alertar("Ocurrió un error al momento de mostrar los nodos.",getActivity());
        }
    }

    public void cambiarPolilinea(int piso){
        try {


            miMapa.clear();
            miMapa.addMarker(miPosicion);
            miMapa.addPolyline(misPolilineas.elementAt(piso));
            miMapa.addMarker(marcadoresPiso.elementAt(2 * piso));
            miMapa.addMarker(marcadoresPiso.elementAt(2 * piso + 1));

            //Agrego los overlays
            if (misOverlays.size() > piso) {
                for (int i = 0; i < misOverlays.elementAt(piso).size(); i++) {
                    miMapa.addGroundOverlay(misOverlays.elementAt(piso).elementAt(i));
                }
            }
        }catch (Exception e){

            log.registrar(this,"cambiarPolilinea",e);
            log.alertar("Ocurrió un error al momento de cambiar la polilinea.",getActivity());
        }

    }

    //Funcion para actualizar los nodos según el piso que se quiera ver
    public void cambiarNodos(int piso){
        try {
            miMapa.clear();
            miMapa.addMarker(miPosicion);
            for (int i = 0; i < misMarcadores.size(); i++) {
                if (piso == 0) {
                    if (misMarcadores.elementAt(i).getTitle().contains("Planta Baja")) {
                        miMapa.addMarker(misMarcadores.elementAt(i));
                    }
                } else {
                    if (misMarcadores.elementAt(i).getTitle().contains("Piso " + piso)) {
                        miMapa.addMarker(misMarcadores.elementAt(i));
                    }
                }
            }

            //Agrego los overlays
            if (misOverlays.size() > piso) {
                for (int i = 0; i < misOverlays.elementAt(piso).size(); i++) {
                    miMapa.addGroundOverlay(misOverlays.elementAt(piso).elementAt(i));
                }
            }
        }catch (Exception e){

            log.registrar(this,"cambiarNodos",e);
            log.alertar("Ocurrió un error al momento de cambiar los nodos.",getActivity());
        }
    }

    //Funcion para saber si un punto está dentro de ciertos limites
    public boolean dentroDeLimites(LatLng posicion, LatLngBounds bounds){
        boolean resultado = true;
        try {
            LatLng limiteInfIzquierdo = bounds.southwest;
            LatLng limiteSupDerecho = bounds.northeast;

            if (posicion.latitude > limiteSupDerecho.latitude || posicion.latitude < limiteInfIzquierdo.latitude || posicion.longitude > limiteSupDerecho.longitude || posicion.longitude < limiteInfIzquierdo.longitude) {
                resultado = false;
            }

        }catch (Exception e){
            log.registrar(this,"dentroDeLimites",e);
            log.alertar("Ocurrió un error al momento establecer los limites de la aplicación.",getActivity());
        }
        return resultado;
    }

    //hashMap de Posición de nodo - Imagen del nodod
    public void cargarMapaImagnes(Vector<Punto> puntos){
        try {
            hashMapImagenes.clear();
            for (int i = 0; i < puntos.size(); i++) {
                if (puntos.elementAt(i).getImagen() != null) {
                    hashMapImagenes.put(new LatLng(puntos.elementAt(i).getLatitud(), puntos.elementAt(i).getLongitud()), puntos.elementAt(i).getImagen());
                }
            }
        }catch (Exception e){

            log.registrar(this,"cargarMapaImagnes",e);
            log.alertar("Ocurrió un error al momento de cargar las imagenes al mapa.",getActivity());
        }
    }

    @Override
    public void onDestroyView() {
        try {
            super.onDestroyView();

            FragmentManager fm = getActivity().getFragmentManager();
            Fragment fragment = (fm.findFragmentById(R.id.map));
            if (fragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        }catch (Exception e){
            log.registrar(this,"onDestroyView",e);
            log.alertar("Ocurrió un error al momento de destruir la vista del mapa.",getActivity());
        }
    }


}


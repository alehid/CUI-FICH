package com.gaston.ciudaduniversitariainteligente;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, usuarioFragment.OnFragmentInteractionListener, MessageFragment.OnFragmentInteractionListener {


    private String CORREO_SOPORTE_TECNICO = "eribastida@gmail.com";
    private static final String BACK_STACK_ROOT_TAG = "root_fragment";

    /* Atributos de la clase*/
    private LogginCUI log = new LogginCUI();
    private ArmaCamino oArmaCamino = null;
    private MapsFragment mapsFragment = null;
    private MessageFragment mensajesFragment = null;
    private FragmentManager fm = getFragmentManager();
    private FloatingActionButton qrBoton = null;
    private IntentIntegrator scanIntegrator = new IntentIntegrator(this);
    private ultimasBusquedas ultimasBusquedas = null;
    private Menu menu = null;
    private BaseDatos CUdb = null;
    private boolean MODO_INVITADO = false;
    private MenuItem usuarioItem ;
    private NavigationView navigationView = null;
    private usuarioFragment usuario = null;
    private Bundle usuario_bundle = null;
    private Usuario usuario_app = null;
    Busqueda busqueda ;

    public static boolean logIn = false;
    public static DatosUsuario currentUser = new DatosUsuario();
    public static Bitmap userBitMap = null;
    public static int lastID = -1;
    private static Context mContext;


    /*Funciones*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            mContext = getApplicationContext();
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            Toolbar toolbar =  findViewById(R.id.toolbar);
            toolbar.setTitle("Ciudad Inteligente");
            setSupportActionBar(toolbar);

            navigationView =  findViewById(R.id.nav_view);


            //Instancio la base de datos
            CUdb = new BaseDatos(getApplicationContext(), "DBCUI", null, 1);

            //Instancio los objetos para ArmaCamino y el MapFragment
            oArmaCamino = new ArmaCamino(this);
            mapsFragment = new MapsFragment();
            mensajesFragment = new MessageFragment();
            MapsInitializer.initialize(getApplicationContext());
            ultimasBusquedas = new ultimasBusquedas();
            ultimasBusquedas.setMainActivity(this);
            busqueda = new Busqueda();


            //Agrego Nodos a mi vector de nodos en oArmaCamino
            cargaNodos();

            //Boton Flotante que está abajo a la derecha, para leer QR
            qrBoton =  findViewById(R.id.fab);
            qrBoton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Se procede con el proceso de scaneo
                    scanIntegrator.initiateScan();
                }
            });


            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


            // Compruebo que se haya iniciado sesión

            Intent i = getIntent();
            usuario_bundle  = i.getBundleExtra("usuario");
            if(i.getBooleanExtra("existe_usuario",false)){


                usuario_bundle  = i.getBundleExtra("usuario");
                usuario =  usuarioFragment.newInstance(usuario_bundle);


            }else{
                modoInvitado(false);
                MODO_INVITADO = true;
                Toast.makeText(this,"MODO INVITADO",Toast.LENGTH_SHORT).show();
            }



            //Inicializo el primer fragment por defecto
            agregarFragment(busqueda,false,BACK_STACK_ROOT_TAG);



        }catch (Exception e) {

            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de inicializar la actividad principal.",this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, Notificaciones.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            if (requestCode == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted
                    //Do your work here
                    //Perform operations here only which requires permission
//                    Toast.makeText(this,"Tengo permisos",Toast.LENGTH_SHORT).show();

                } else {
//                    Toast.makeText(this,"No tengo permisos",Toast.LENGTH_SHORT).show();

                }

            } else {
//                Toast.makeText(this,"No tengo permisos",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e ){
            log.registrar(this,"onRequestPermissionsResult",e);
            log.alertar("Ocurrió un error al momento de consultar los permisos.",this);
        }

    }

    // Función encargada de ocultar las caracteristicas extras que tiene la app si ingresaría con una cuenta
    public void modoInvitado(boolean visible){

        navigationView.getMenu().findItem(R.id.usuario).setVisible(visible);
        navigationView.getMenu().findItem(R.id.cerrar).setVisible(visible);
        navigationView.getMenu().findItem(R.id.enviarMensaje).setVisible(visible);

    }
    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if(fm.getBackStackEntryCount() == 0){
                    super.onBackPressed();
                }
                else{
                    if(fm.findFragmentById(R.id.fragment_container) instanceof MapsFragment){
                        finish();
                    }
                    else {
                        mapsFragment.limpiarMapa();

                    }
                }
            }
        } catch (Exception e) {
            log.registrar(this,"onBackPressed",e);
            log.alertar("Ocurrió un error al momento de presionar el botón atrás.",this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer,menu);
        this.menu = menu;


        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            for (int i = 0; i < menu.size(); i++) {
                if (menu.getItem(i).getTitle().charAt(0) == '*') {
                    menu.getItem(i).setTitle(menu.getItem(i).getTitle().toString().substring(1));
                    item.setTitle("*" + item.getTitle());
                    break;
                }
            }
            /*Si estoy mostrando una polilinea, la cambio segun la opcion de piso seleccionada*/
            if (mapsFragment.modoPolilinea()) {
                if (item.toString().contains("Baja")) {
                    mapsFragment.cambiarPolilinea(0);
                    return true;
                } else {
                    mapsFragment.cambiarPolilinea(Integer.parseInt(item.toString().substring(item.toString().indexOf(' ') + 1)));
                }
            }
            /*Esto es si estoy mostrando nodos*/
            else {
                if (item.toString().contains("Baja")) {
                    mapsFragment.cambiarNodos(0);
                    return true;
                } else {
                    mapsFragment.cambiarNodos(Integer.parseInt(item.toString().substring(item.toString().indexOf(' ') + 1)));
                }
            }

        }catch (Exception e){
            log.registrar(this,"onOptionsItemSelected",e);
            log.alertar("Ocurrió un error al momento de seleccionar el item del menú.",this);
        }

        return super.onOptionsItemSelected(item);
    }

    //Switch segun en que opcion del menu desplegable se selecciona
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.buscar) {
                if (!(fm.findFragmentById(R.id.fragment_container) instanceof Busqueda)) {
                    agregarFragment(busqueda,false,BACK_STACK_ROOT_TAG);
                }

            } else if (id == R.id.mapa_completo) {
//                mapsFragment.limpiarMapa();
                menu.clear();
                if (!(fm.findFragmentById(R.id.fragment_container) instanceof MapsFragment)) {
                    agregarFragment(mapsFragment,false,BACK_STACK_ROOT_TAG);
                }

            } else if (id == R.id.ultimas) {
                if (!(fm.findFragmentById(R.id.fragment_container) instanceof ultimasBusquedas)) {
//                    qrBoton.hide();
//                    menu.clear();
                    agregarFragment(ultimasBusquedas,false,BACK_STACK_ROOT_TAG);

                }

            } else if (id == R.id.usuario) {
//                Toast.makeText(this, "Este es el usuario ", Toast.LENGTH_SHORT).show();

                qrBoton.hide();
                menu.clear();
                agregarFragment(usuario, false, BACK_STACK_ROOT_TAG);

            }else if (id == R.id.enviarMensaje){
                qrBoton.hide();
                menu.clear();
                agregarFragment(mensajesFragment, false, BACK_STACK_ROOT_TAG);

            }else if (id == R.id.informar_error) {
                informarError();

            }else if (id == R.id.creditos) {

                Toast.makeText(this,"Función no implementada. Consultar al autor del proyecto. ",Toast.LENGTH_SHORT).show();

            }else if (id == R.id.cerrar) {

//                usuario_app = new Usuario();
//                usuario_app.cerrar_sesion();

                Toast.makeText(this,"Cerrar sesion.",Toast.LENGTH_SHORT).show();
                currentUser = null;
                lastID = -1;
                logIn = false;
                FirebaseAuth.getInstance().signOut();
                finishActivity(0);

                finish();

            }
            DrawerLayout drawer =  findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }catch (Exception e){
            log.registrar(this,"onNavigationItemSelected",e);
            log.alertar("Ocurrió un error al momento de seleccionar una opción del menú desplegable.",this);
        }


        return true;
    }

    public void agregarFragment(Fragment fragment, boolean addToBackStack, String tag) {
        try{

//        Toast.makeText(this,"YA EXISTE EL FRAGMENT " + fm.findFragmentById(fragment.getId()).toString(),Toast.LENGTH_LONG).show();

        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.replace(R.id.fragment_container, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception e){
            log.registrar(this,"agregarFragment",e);
            log.alertar("Ocurrió un error al momento cargar una pantalla fragment.",this);
        }

    }
    /*
    Mostrar busqueda llama a las funciones del mapFragment que:
    -muestran un conjunto de puntos en el mapa
    -muestran una polilinea desde el punto mas cercano hasta el objetivo
    *setPuntoMasCercano setea en oArmaCamino el nodo mas cercano a la posición donde estoy parado
    Luego reemplazo el fragment de Busqueda por el de mapa
    */
    public void mostrarBusqueda(String Edificio, String Nombre) {
        try {
            if (mapsFragment!=null) {
                mapsFragment.setPisoActual(0);
                if (Edificio.equals("*")) {
                    Vector<Punto> puntos  = oArmaCamino.nodosMapa(Nombre);
                    mapsFragment.mostrarNodos(puntos);
                    menu.clear();
                    menu.add("Planta Baja");
                    for (int i = 1; i < mapsFragment.getCantPisos(); i++) {
                        menu.add("Piso " + i);
                    }
                    menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
                    getMenuInflater().inflate(R.menu.main, menu);
                } else {
                    oArmaCamino.setPuntoMasCercano(mapsFragment.getPosicion(), mapsFragment.getPisoActual());
                    mapsFragment.dibujaCamino(oArmaCamino.camino(Edificio, Nombre));
                    menu.clear();
                    menu.add("Planta Baja");
                    for (int i = 1; i < mapsFragment.getCantPisos(); i++) {
                        menu.add("Piso " + i);
                    }
                    menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
                    getMenuInflater().inflate(R.menu.main, menu);
                    String texto = "Su objetivo está en " + oArmaCamino.getPisoObjetivo();
                    Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
                }
                agregarFragment(mapsFragment,false,BACK_STACK_ROOT_TAG);
                qrBoton.show();
            }else{
                Toast.makeText(this,"Por favor ingrese a la pestaña del mapa.",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            log.registrar(this,"mostrarBusqueda",e);
            log.alertar("Ocurrió un error al momento de mostrar la búsqueda.",this);
        }
    }

    //Funcion que le pasa a oArmaCamino un edificio y devuelve un Vector con todas las aulas de ese edificio
    public Vector<Punto> verAulasPorEdificio(String Edificio) {

        Vector<Punto> resultado = new Vector<Punto>();
        try {
            resultado =  oArmaCamino.verAulasPorEdificio(Edificio);
        }catch (Exception e){
            log.registrar(this,"verAulasPorEdificio",e);
            log.alertar("Ocurrió un error al querer mostrar las aulas.",this);
        }
        return  resultado;
    }


    //Funcion para crear nodos del mapa y sus conexiones
    private void cargaNodos() {
        try {
            Vector<Punto> puntos = new Vector<>();
            SQLiteDatabase db1 = CUdb.getReadableDatabase();
            Cursor c = db1.rawQuery("SELECT *  FROM Punto", null);
            c.moveToFirst();

            //Creo y agrego los nodos
            if (c.getCount() > 0) {
                do {
                    Punto oPunto = new Punto(c.getInt(0), Double.parseDouble(c.getString(1)), Double.parseDouble(c.getString(2)), c.getString(3), c.getInt(4), c.getString(5), c.getInt(6));
                    puntos.add(oPunto);
                } while (c.moveToNext());
            }

            //Genero las conexiones
            for (int i = 0; i < puntos.size(); i++) {
                Cursor d = db1.rawQuery("SELECT idHasta FROM Conexiones WHERE idDesde = " + puntos.elementAt(i).getId(), null);
                d.moveToFirst();
                if (d.getCount() > 0) {
                    do {
                        puntos.elementAt(i).addVecino(puntos.elementAt(d.getInt(0)));
                    } while (d.moveToNext());
                }
                oArmaCamino.addNodo(puntos.elementAt(i));
            }
            //Cierro DB
            puntos.clear();
            db1.close();
        }catch (Exception e){
            log.registrar(this,"cargaNodos",e);
            log.alertar("Ocurrió un error al momento de cargar los nodos.",this);
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {


            //Se obtiene el resultado del proceso de scaneo y se parsea
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
                //Quiere decir que se obtuvo resultado pro lo tanto:
                //Desplegamos en pantalla el contenido del código de barra scaneado
                String scanContent = scanningResult.getContents();
                mapsFragment.setLat(Double.parseDouble(scanContent.toString().substring(0, (scanContent.toString().indexOf(',')))));
                mapsFragment.setLon(Double.parseDouble(scanContent.toString().substring((scanContent.toString().indexOf(',')) + 1, scanContent.length() - 2)));
                mapsFragment.setPisoActual(Integer.parseInt(scanContent.toString().substring(scanContent.toString().length() - 1)));
                mapsFragment.actualizaPosicion();

                //Actualizo el * del menu de pisos cuando cambio el piso por QR
                if (mapsFragment.getPisoActual() + 1 <= mapsFragment.getCantPisos()) {
                    for (int i = 0; i < menu.size(); i++) {
                        if (menu.getItem(i).getTitle().charAt(0) == '*') {
                            menu.getItem(i).setTitle(menu.getItem(i).getTitle().toString().substring(1));
                            menu.getItem(mapsFragment.getPisoActual()).setTitle("*" + menu.getItem(mapsFragment.getPisoActual()).getTitle());
                            break;
                        }
                    }
                }
            } else {
                //Quiere decir que NO se obtuvo resultado
                Toast toast = Toast.makeText(getApplicationContext(),
                        "No se ha recibido datos del scaneo!", Toast.LENGTH_SHORT);
                toast.show();
            }


        }catch (Exception e){
            log.registrar(this,"onActivityResult",e);
            log.alertar("Ocurrió un error al momento de obtener el resultado del escaneo del QR..",this);
        }
    }

    // Función encargada de crear un Intet para poder informar de algún error.
    public void informarError(){
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",CORREO_SOPORTE_TECNICO, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Me apareció el siguiente error en CUI");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Durante el uso de la aplicación ocurrio que ...");
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
        }catch (Exception e ){
            log.registrar(this,"informarError",e);
            log.alertar("Ocurrió un error al momento de tratar de enviar el correo para informar un error.",this);

        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static void inicializarDataSnapshot(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("userData");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatosUsuario temp;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()){

                    temp = userSnapshot.getValue(DatosUsuario.class);
                    if (user.getEmail().equals(temp.getCorreo())) {
                        currentUser = temp;
                        logIn = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        usuariosRef.addValueEventListener(eventListener);
    }

}

package com.gaston.ciudaduniversitariainteligente;

/**
 * Clase encargada de gestionar la base de datos (FIREBASE)
 *
 * @author ERic Bastida <eribastida@gmail.com>
 */

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDB {

    private DatabaseReference database;
    private String NOMBRE_RAIZ_USUARIOS = "usuarios";
    private DatabaseReference usuariosRef;
    private String TAG = "AlojaFireBase";
    private LogginCUI log = new LogginCUI();
    private Activity actividad;

    private AdminDB.OnDB_Listener mListener;

    final static public int COD_USUARIO_EXISTE    = 1;
    final static public int COD_USUARIO_AGREGADO  = 2;
    final static public int COD_USUARIO_ELIMINADO = 3;
    final static public int COD_USUARIO_MODIFICADO= 4;



    public AdminDB(Activity actividad)  {
        try{
            this.actividad = actividad;

            database = FirebaseDatabase.getInstance().getReference();
            usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS);


        }catch (Exception e ){


            log.registrar(this,"AdminDB",e);
            log.alertar("Ocurrió un error al momento de crear el administrador de bases de datos Firebase.",actividad);

        }

    }
    public interface OnDB_Listener {

        void result(int codigo, Usuario usuario , boolean resultado);

    }


    public void agregarUsuario(final OnDB_Listener escuchador, final Usuario usuario) {
        try{


            final DatabaseReference usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS).child(usuario.getKey());

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        escuchador.result(COD_USUARIO_AGREGADO,usuario,false);


                    } else {

                        usuariosRef.setValue(usuario);
                        escuchador.result(COD_USUARIO_AGREGADO,usuario,true);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };

            usuariosRef.addListenerForSingleValueEvent(eventListener);
        }catch (Exception e ){

            log.registrar(this,"agregarUsuario",e);
            log.alertar("Ocurrió un error al momento de agregar un usuario a la base de datos.",actividad);

        }




    }

    public void modificarUsuario(final OnDB_Listener escuchador,final Usuario usuario) {

        try{

            final DatabaseReference usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS).child(usuario.getKey());

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        usuariosRef.setValue(usuario);
                        escuchador.result(COD_USUARIO_MODIFICADO,usuario,true);

                    } else {

                        escuchador.result(COD_USUARIO_MODIFICADO,usuario,false);



                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };

            usuariosRef.addListenerForSingleValueEvent(eventListener);
        }catch (Exception e ){

            log.registrar(this,"modificarUsuario",e);
            log.alertar("Ocurrió un error al momento de modificar un usuario en la base de datos.",actividad);

        }


    }

    public void eliminarUsuario(String key) {

        Task<Void> resultado = usuariosRef.child(key).removeValue();

    }

    public void consultarUsuario(final OnDB_Listener escuchador, String key) {
        try{
            final DatabaseReference usuariosRef = database.child(NOMBRE_RAIZ_USUARIOS).child(key);

            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        Usuario usuario_consultado = dataSnapshot.getValue(Usuario.class);

                        escuchador.result(COD_USUARIO_EXISTE,usuario_consultado,true);


                    } else {


                        escuchador.result(COD_USUARIO_EXISTE,null,false);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                }
            };

            usuariosRef.addListenerForSingleValueEvent(eventListener);

        }catch (Exception e ){

            log.registrar(this,"consultarUsuario",e);
            log.alertar("Ocurrió un error al momento de consultar un usuario en la base de datos.",actividad);

        }


}



}
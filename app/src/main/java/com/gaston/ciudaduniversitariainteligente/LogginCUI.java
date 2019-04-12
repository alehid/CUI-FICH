package com.gaston.ciudaduniversitariainteligente;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

//Clase desarrollada por ERic Bastida - eribastida@gmail.com


/*
* Ejemplo de implementación

            log.registrar(this,"nombre_funcion",excepcion);
            log.alertar("Descripcion del error (para el usuario) ",actividad);

* */


public class LogginCUI {

    public LogginCUI(){

    }

    final public String ETIQUETA_ERROR = "ERROR-CUI";
    final public String ETIQUETA_INFO = "INFO-CUI";
    final public String TITULO_ERROR = "Error en Ciudad Universitaria Inteligente";


    //    [Nombre de la clase]/[Nombre de la funcion] => [Causa], [Mensaje]:, [Origen]:
    private String STRING_MENSAJE = "%s/%s => \n[Causa]: %s  \n[Mensaje]: %s  \n[Origen]: %s";
    private String mensaje;

    private  Exception excepcion_especifica;
    private Activity actividad;
    private boolean registrado = false;


    public void registrar_info(Object clase , String nombre_funcion, String  info){

        Log.d(ETIQUETA_INFO,String.format(STRING_MENSAJE,clase.getClass().getName(), nombre_funcion, info));
    }

    public void registrar(Object clase , String nombre_funcion, Exception e){

        registrado = true;
        excepcion_especifica = e;
        mensaje = String.format(STRING_MENSAJE,clase.getClass().getName(), nombre_funcion, e.getCause(),e.getMessage(),e.getClass().toString());
        Log.d(ETIQUETA_ERROR,mensaje);



    }

    // Función encargada de gestionar el DialogAlert para informar al usuario un respectivo mensaje.
    public void alertar(String mensaje, @Nullable  final Activity activity ){

        // Se debe registrar el error de antemano.
        if (registrado == true) {
            actividad = activity;

            //Se instancia un objeto AlerteDialog
            final AlertDialog.Builder preAlerta = new AlertDialog.Builder(activity);
            // Se definen sus respectivos datos para mostrar
            preAlerta.setMessage(mensaje).setTitle(TITULO_ERROR);

            preAlerta.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Toast.makeText(actividad.getApplicationContext(), "Si la falla continua reinicie la apliación.", Toast.LENGTH_SHORT).show();
                    //TODO: averiguar como cerrar la aplicación y no solo la actividad.
                    activity.finish();

                }
            });

            preAlerta.setNegativeButton("Ver más detalles", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    detalles();


                }
            });

            AlertDialog alerta = preAlerta.create();
            alerta.show();
        }

    }

    private void detalles(){
        //Se instancia un objeto AlerteDialog
        AlertDialog.Builder preAlerta = new AlertDialog.Builder(actividad);

        // Se definen sus respectivos datos para mostrar
        preAlerta.setMessage(mensaje);

        preAlerta.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(actividad.getApplicationContext(),"Si la falla continua reinicie la apliación.",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alerta = preAlerta.create();
        alerta.show();

    }


}
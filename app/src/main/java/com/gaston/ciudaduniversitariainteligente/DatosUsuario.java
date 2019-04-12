package com.gaston.ciudaduniversitariainteligente;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

class DatosUsuario {
    public String nombre;
    public String apellido;
    public String correo;
    public int carrera;

    public DatosUsuario(String nombre, String apellido, String correo, int carrera) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.carrera = carrera;
    }

    public DatosUsuario(){}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getCarrera() {
        return carrera;
    }

    public void setCarrera(int carrera) {
        this.carrera = carrera;
    }
}

class Usuarios {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private DatosUsuario datosUsuario;
    private LogginCUI log = new LogginCUI();
    private Activity activity;


    public Usuarios(Activity activity, String nombre, String apellido, String correo, int carrera) {
        datosUsuario = new DatosUsuario(nombre, apellido, correo, carrera);

        this.firebaseAuth = FirebaseAuth.getInstance();

        this.databaseReference = FirebaseDatabase.getInstance().getReference("userData");
        this.activity = activity;
    }

    public boolean Save(){
        FirebaseUser user = this.firebaseAuth.getCurrentUser();

        if (user == null)
            return false;
        try {
            databaseReference.child(user.getUid()).setValue(this.datosUsuario);
        } catch (Exception e){
            log.registrar(this,"save",e);
            log.alertar("Ocurrió un error al momento de salvar informacion del usuario en Firebase.", this.activity);
        }

        return true;
    }
}

package com.gaston.ciudaduniversitariainteligente;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Description:
 * Actividad encargada del inicio y registro de los usuarios a la aplicacion
 * -------------------------------------
 * Created by ERic Bastida <eribastida@gmail.com>
 * on 15-Jan-19.
 * -------------------------------------
 */

public class Login_SingUp extends AppCompatActivity implements loginFragment.OnFragmentInteractionListener, usuarioFragment.OnFragmentInteractionListener {

    LogginCUI log = new LogginCUI();
    private loginFragment login;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login__sing_up);

            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            login = new loginFragment();

            getFragmentManager().beginTransaction().add(R.id.fragment_container, login).commit();
        }catch (Exception e){
            log.registrar(this,"onCreate",e);
            log.alertar("Ocurrió un error al momento de gestionar el ingreso al usuario.",this);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}

package com.gaston.ciudaduniversitariainteligente;

/**
 * Description:
 * Fragment encargado del inicio de sesion del usuario
 * y proporcionar las redes sociales en caso de querer ingresar de manera espontanea
 * -------------------------------------
 * Created by ERic Bastida <eribastida@gmail.com>
 * on 15-Jan-19.
 * -------------------------------------
 */

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class loginFragment extends Fragment implements AdminDB.OnDB_Listener
{
    private ImageView imgLogoUNL;

    private AdminDB baseDatos;
    private Button btnIniciar;
    private Button btnGoToSignUp;
    private Button btnIngresante;
    private TextView txtUser;
    private TextView txtPass;
    private usuarioFragment signUp;
    private usuarioFragment.OnFragmentInteractionListener mListener;
    private LogginCUI log  = new LogginCUI();
    private static final int RC_SIGN_IN = 123;
    private int id_proveedor = 0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressDialog progressDialog;


    List<AuthUI.IdpConfig> proveedor = null;

    public loginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        progressDialog = new ProgressDialog(getContext());
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        id_proveedor = 0;


        btnIniciar =  view.findViewById(R.id.btnIniciar);
        btnGoToSignUp =  view.findViewById(R.id.btnGoToSignUp);
        btnIngresante = view.findViewById(R.id.btnIngresante);
        txtUser =  view.findViewById(R.id.txtUser);
        txtPass =  view.findViewById(R.id.txtPass);
        imgLogoUNL = view.findViewById(R.id.imgLogoUNL);

        try {


            btnIniciar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //El usuario debe ser el nombre del correo sin @servidor.com
                    progressDialog.setMessage("Verificando usuario...");
                    progressDialog.show();
                    String email = txtUser.getText().toString();
                    String pass = txtPass.getText().toString();

                    if (hay_conexion()){
                        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    try {
                                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        MainActivity.inicializarDataSnapshot();
                                        downloadProfileImage(user);

//                                        Handler h = new Handler();
//                                        h.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                if (MainActivity.logIn && user!=null){
//                                                    progressDialog.dismiss();
//                                                    Usuario user_login = new Usuario();
//                                                    user_login.copy(user);
//
//                                                    ir_a_CUI(user_login);
//                                                } else {
//                                                    progressDialog.dismiss();
//                                                    Toast.makeText(getContext(), "No se puede verificar la informacion del usuario", Toast.LENGTH_LONG).show();
//                                                }
//                                            }
//                                        }, 5000);
                                    } catch (Exception e){
                                        log.registrar(this,"onCreateView",e);
                                        log.alertar("Ocurrió un error al momento de crear el login.",getActivity());
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), com.gaston.ciudaduniversitariainteligente.R.string.usuPassWrong, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(),"Compruebe la conexión a Internet.",Toast.LENGTH_SHORT).show();
                    }

                }

            });

            // Inflate the layout for this fragmen
            btnGoToSignUp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    signUp = new usuarioFragment();
                    FragmentTransaction fragmentManager = getActivity().getFragmentManager().beginTransaction();
                    fragmentManager.replace(R.id.fragment_container, signUp);
                    fragmentManager.addToBackStack(null);
                    fragmentManager.commit();


                }
            });


            btnIngresante.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MainActivity.currentUser = null;
                    MainActivity.lastID = -1;
                    MainActivity.logIn = false;
                    ir_a_CUI(null);

                }
            });

//            ArrayList<ItemData> list=new ArrayList<>();
//            list.add(new ItemData("Iniciar con cuenta UNL",R.drawable.unl_icon));
//            list.add(new ItemData("Iniciar con Facebook",R.drawable.facebook_icon));
//            list.add(new ItemData("Iniciar con Google+",R.drawable.google_icon));
//            list.add(new ItemData("Iniciar con Twitter",R.drawable.twitter_icon));
//
//            Spinner spinner=(Spinner) view.findViewById(R.id.spinner2);
//
//            SpinnerAdapter adapter= new SpinnerAdapter(getActivity(),R.layout.spinner_layout_signup, R.id.txtItemSpinner,list);
//
//            spinner.setAdapter(adapter);
//
//            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                    asignarServicio(position);
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                    //Another interface callback
//                }
//            });


        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de crear el login.",getActivity());

        }
        return view;


    }

    private void downloadProfileImage(final FirebaseUser user){
        if (user != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + user.getUid() + ".jpg");
            progressDialog.setMessage("Downloading information...");
            final long ONE_MEGABYTE = 1024 * 1024;
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    progressDialog.dismiss();
//                    ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
                    MainActivity.userBitMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    MainActivity.userBitMap = BitmapFactory.decodeStream(arrayInputStream);

                    Usuario user_login = new Usuario();
                    user_login.copy(user);

                    ir_a_CUI(user_login);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failure to download data...", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            progressDialog.setMessage("Verificando usuario...");
            progressDialog.show();
            MainActivity.inicializarDataSnapshot();

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.logIn && user!=null){
                        progressDialog.dismiss();
                        Usuario user_login = new Usuario();
                        user_login.copy(user);

                        ir_a_CUI(user_login);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "No se puede verificar la informacion del usuario", Toast.LENGTH_LONG).show();
                    }
                }
            }, 5000);
        }
    }

    //Función encargada de comprobar la conexión a Internet
    private boolean hay_conexion() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Función encargada de preparar el servicio para iniciar sesion al momento de presionar el boton "INICIAR"
    private void asignarServicio(int id_red_social) {

        try {

            switch (id_red_social) {

                case 0:

                    proveedor = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
                    break;
                case 1: //Facebook
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build());
                    break;

                case 2: //Google +
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build());
                    break;

                case 3: //Twitter
                    //                proveedor = Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build());
                    proveedor = Arrays.asList(new AuthUI.IdpConfig.TwitterBuilder().build());;
                    break;

                default:
                    proveedor = null;

            }

            id_proveedor = id_red_social;
        }catch (Exception e){
            log.registrar(this,"asignarServicio",e);
            log.alertar("Ocurrió un error al momento de asignar la red social para iniciar sesión.",getActivity());

        }

    }

    public void createSignInIntent() {
        try {

            if (proveedor != null) {
                // Create and launch sign-in intent
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(proveedor)
                                .build(),
                        RC_SIGN_IN);
            } else {
                Toast.makeText(getActivity(), "Se debe seleccionar un proveedor para iniciar sesion.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            log.registrar(this,"createSignInIntent",e);
            log.alertar("Ocurrió un error al momento de iniciar sesión con la red social.",getActivity());
        }

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            if (requestCode == RC_SIGN_IN) {
//                IdpResponse response = IdpResponse.fromResultIntent(data);
//
//                if (resultCode == getActivity().RESULT_OK) {
//                    // Successfully signed in
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    Usuario usuario_red_social = new Usuario();
//                    usuario_red_social.copy(user);
//
//                    ir_a_CUI(usuario_red_social);
//
//                } else {
//                    Toast.makeText(getActivity(), "No se ha podido iniciar sesión." + response.toString(), Toast.LENGTH_SHORT).show();
//                    throw new Exception(response.getError());
//
//                }
//            }
//        }catch (Exception e ){
//
//            log.registrar(this,"onActivityResult",e);
//            log.alertar("Ocurrió un error al momento de gestionar el inicio de sesión.",getActivity());
//
//        }
//    }

    public void privacyAndTerms() {
        try{
            List<AuthUI.IdpConfig> providers = Collections.emptyList();
            // [START auth_fui_pp_tos]
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTosAndPrivacyPolicyUrls(
                                    "https://example.com/terms.html",
                                    "https://example.com/privacy.html")
                            .build(),
                    RC_SIGN_IN);
        }catch (Exception e ){
            log.registrar(this,"privacyAndTerms",e);
            log.alertar("Ocurrió un error al momento de querer mostrar los terminos de privacidad.",getActivity());

        }

    }


    private void ir_a_CUI(Usuario user){
        try{
            Intent gotoCUI = new Intent(getActivity(),MainActivity.class);
            String mensaje = "";
            if (user != null){
                gotoCUI.putExtra("existe_usuario", true);
                gotoCUI.putExtra("usuario",user.toBundle());

                mensaje = "con usuario -> " + user.getNombre();
            }else{
                gotoCUI.putExtra("existe_usuario", false);
                mensaje = "sin usuario.";
            }

            startActivity(gotoCUI);
            getActivity().finish();

        }catch (Exception e ){
            log.registrar(this,                  "ir_a_CUI",e);
            log.alertar("Ocurrió un error al momento de intentar ir a CUI.",getActivity());

    }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void result(int codigo, Usuario usuario, boolean resultado) {

    }

    public void resultado_BD(int codigo, Usuario usuario, boolean resultado) {
        try{
            if (AdminDB.COD_USUARIO_EXISTE == codigo){
                String mensaje = "";

                if (resultado){


                    ir_a_CUI(usuario);

                }else{
                    mensaje = "No existe el usuario. Por favor registrese o ingrese por alguna red social.";
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                }



            }
        }catch (Exception e ){
            log.registrar(this,"resultado_BD",e);
            log.alertar("Ocurrió un error al momento recibir el resultado de la base de datos",getActivity());

        }

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}

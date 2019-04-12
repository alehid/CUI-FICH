package com.gaston.ciudaduniversitariainteligente;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class usuarioFragment extends Fragment implements AdminDB.OnDB_Listener{


    private AdminDB BaseDatos;
    private LottieAnimationView animationView;
    private FloatingActionButton btnUsuario;
    private TextView txtName;
    private TextView txtLastName;
    private AppCompatEditText txtEmail;
    private AppCompatEditText txtPass;
    private AppCompatEditText txtPass2;

    private TextView txtCarrera;
    private Spinner spinner_carreras;
    private loginFragment login;
    private LogginCUI log = new LogginCUI();
    private OnFragmentInteractionListener mListener;
    private Usuario usuario_modificado;

    private boolean MODO_EDICION = true;
    private boolean MODO_EDICION_2 = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    Bitmap imageBitmap;
    Uri uri;
    private ProgressDialog progressDialog;

    FirebaseStorage storage;
    StorageReference storageReference;


    private String[] carreras;

    private CircleImageView imagen_perfil;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageBitmap = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        try {
            progressDialog = new ProgressDialog(getContext());
            imageBitmap = null;
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_usuario, container, false);
            animationView = view.findViewById(R.id.animation_view);
            btnUsuario =  view.findViewById(R.id.btnUsuario);

            // Inflate the layout for this fragment
            txtName =  view.findViewById(R.id.txtName);
            txtLastName =  view.findViewById(R.id.txtLastName);
            txtEmail =  view.findViewById(R.id.txtEmail);
            txtPass =  view.findViewById(R.id.txtPass);
            txtPass2 =  view.findViewById(R.id.txtPass2);
            txtCarrera = view.findViewById(R.id.txtCarrera);
            spinner_carreras =  view.findViewById(R.id.spinner_carreras);
            imagen_perfil = view.findViewById(R.id.profile_image);
            carreras = getResources().getStringArray(R.array.carreras);

            imagen_perfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.spinner_item_layout, carreras);
            spinner_carreras.setAdapter(adapter);

            txtCarrera.setText(spinner_carreras.getSelectedItem().toString());

            // Si tengo información que mostrar, inhabilito los campos y seteo la información recibida.
            FirebaseUser info_usuario = FirebaseAuth.getInstance().getCurrentUser();
            if (MainActivity.logIn && MainActivity.currentUser != null){

                MODO_EDICION = false;

//                String nombre_completo = ((FirebaseUser) info_usuario).getDisplayName().toString();
//                String[] nombres = nombre_completo.split(" ");
//                Log.d("DEBUG-APP", "Nombre: " + nombre_completo);
//                Log.d("DEBUG-APP", "tamanio: " + nombres.length);

                txtName.setText(MainActivity.currentUser.getNombre());
                txtLastName.setText(MainActivity.currentUser.getApellido());
                txtEmail.setText(MainActivity.currentUser.getCorreo());
//                txtPass.setText();
//                txtPass2.setText(info_usuario.getString("pass"));
                spinner_carreras.setSelection(MainActivity.currentUser.getCarrera());
//                String uri_image = ((FirebaseUser) info_usuario).getPhotoUrl().toString();
                if (MainActivity.userBitMap == null) {
                    imagen_perfil.setImageResource(R.drawable.profile_example);
                }else{

//                    new DescargarImagenPerfil((CircleImageView) view.findViewById(R.id.profile_image)).execute(uri_image);
                      imagen_perfil.setImageBitmap(MainActivity.userBitMap);
//                    Toast.makeText(getActivity(),uri_image,Toast.LENGTH_SHORT).show();

                }


                habilitar_campos(false);
                btnUsuario.setImageResource(R.drawable.edit_icon);


            }
            btnUsuario.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    if (!MODO_EDICION){
                        btnUsuario.setImageResource(R.drawable.save_icon);
                        habilitar_campos(true);
                        MODO_EDICION = !MODO_EDICION;
                        MODO_EDICION_2 = true;

                    }else{
                        final String name = txtName.getText().toString();
                        final String lastName = txtLastName.getText().toString();
                        final String email = txtEmail.getText().toString();
                        String pass = txtPass.getText().toString();
                        String pass2 = txtPass2.getText().toString();


                        if (hay_conexion()){

                            if (!name.isEmpty() && !lastName.isEmpty() && !email.isEmpty() && !pass.isEmpty() && !pass2.isEmpty()) {

                                if (spinner_carreras.getSelectedItemPosition() > 0){


                                    final int carrera = spinner_carreras.getSelectedItemPosition();
//                                    Toast.makeText(getContext(), name + " " + lastName, Toast.LENGTH_LONG).show();
                                    if (pass.equals(pass2)) {
                                        progressDialog.setMessage("Registrando usuario.");
                                        progressDialog.show();
                                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                                                                  .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                      @Override
                                                                      public void onComplete(@NonNull Task<AuthResult> task) {
                                                                            if (task.isSuccessful()) {
                                                                                try {
                                                                                    progressDialog.setMessage("Salvando información del usuario.");
                                                                                    Usuarios usuario = new Usuarios(getActivity(), name, lastName, email, carrera);
                                                                                    usuario.Save();
//                                                                                        Toast.makeText(getContext(), "Usuario guardado correctamente", Toast.LENGTH_LONG).show();
//                                                                                    }
                                                                                    MainActivity.inicializarDataSnapshot();
                                                                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                                                                    if (imageBitmap != null) {
                                                                                        uploadPicture(user.getUid(), user);
                                                                                    } else {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText(getContext(), "Imagen subida exitosamente.", Toast.LENGTH_LONG).show();
                                                                                        Usuario user_login = new Usuario();
                                                                                        user_login.copy(user);

                                                                                        ir_a_CUI(user_login);
                                                                                    }

                                                                                    Usuario userLogIn = new Usuario();
                                                                                    userLogIn.copy(MainActivity.currentUser);
                                                                                    ir_a_CUI(userLogIn);
                                                                                }catch (Exception e){
                                                                                    Log.d("DEBUG-APP", e.getMessage());
                                                                                }
                                                                            } else {
                                                                                progressDialog.dismiss();
                                                                                Toast.makeText(getContext(), "Can't save the user", Toast.LENGTH_LONG).show();
                                                                            }

                                                                      }
                                                                  });
                                    } else {
                                        Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(getActivity(),"Ingrese una carrera.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(getActivity(),"Compruebe la conexión a internet.", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });





            spinner_carreras.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putString("datos", "datos que necesito");
                    txtCarrera.setText(spinner_carreras.getSelectedItem().toString());

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Another interface callback
                }
            });


        }catch (Exception e){
            log.registrar(this,"onCreateView",e);
            log.alertar("Ocurrió un error al momento de gestionar los campos para el inicio de sesion.",getActivity());
        }
        return view;



    }

    private void uploadPicture(String uuid, FirebaseUser inputUser){
        try {
            final FirebaseUser user = inputUser;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            progressDialog.setTitle("Uploading...");
//            progressDialog.show();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference ref = storageReference.child("images/" + uuid + ".jpg");

            UploadTask uploadTask = ref.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    try {
                        MainActivity.userBitMap = imageBitmap;
                        Toast.makeText(getContext(), "Imagen subida exitosamente.", Toast.LENGTH_LONG).show();
                        Usuario user_login = new Usuario();
                        user_login.copy(user);

                        ir_a_CUI(user_login);
                    } catch (Exception e){
                        Log.d("DEBUG-APP", e.getMessage());
                    }
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploading ... "+ (int)progress + "%");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Fallo al subir imagen.", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e){
            log.registrar(this,"IMAGEN",e);
            log.alertar("Ocurrió un error al momento de subir la imagen.",getActivity());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            uri = data.getData();
            imageBitmap = (Bitmap) extras.get("data");

            imagen_perfil.setImageBitmap(imageBitmap);
        }
    }

    private void funcion_boton_usuario(View view){

    }
    //Función encargada de comprobar la conexión a Internet
    private boolean hay_conexion() {
        boolean resultado = false;
        try{
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            resultado = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (Exception e){
            log.registrar(this,"hay_conexion",e);
            log.alertar("Ocurrió un error al momento de comprobar la conexión a internet.",getActivity());
        }
        return resultado;
    }

    public void habilitar_campos( boolean is_enable){
        try{
            txtName.setEnabled(is_enable);
            txtLastName.setEnabled(is_enable);
            txtEmail.setEnabled(is_enable);
            txtPass.setEnabled(is_enable);
            txtPass2.setEnabled(is_enable);
            txtCarrera.setEnabled(is_enable);
            spinner_carreras.setEnabled(is_enable);
        }catch (Exception e){
            log.registrar(this,"habilitar_campos",e);
            log.alertar("Ocurrió un error al momento de habilitar/deshabilitar los campos de datos.",getActivity());
        }



    }
    public void resetear_campos(){
        try {

            txtName.clearComposingText();
            txtLastName.clearComposingText();
            txtEmail.clearComposingText();
            txtPass.clearComposingText();
            txtPass2.clearComposingText();
        }catch (Exception e){
            log.registrar(this,"resetear_campos",e);
            log.alertar("Ocurrió un error al momento de limpiar los campos de datos.",getActivity());
        }


}

    public void siguiente_pantalla(){
        try{
            //Si dentro de CUI, no hace falta ir a algún lado
            if (!MODO_EDICION_2) {
                login = new loginFragment();
                FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, login);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }catch (Exception e){
            log.registrar(this,"siguiente_pantalla",e);
            log.alertar("Ocurrió un error al momento de proceder a cargar la pantalla siguiente.",getActivity());
        }


    }

    public void result(int codigo, Usuario usuario, boolean resultado) {

    }

    public void resultado_BD(int codigo, Usuario usuario, boolean resultado) {
        try{
            String mensaje ="";

            switch (codigo){
                case AdminDB.COD_USUARIO_AGREGADO:

                    if (resultado){
                        mensaje = "Se ha agregado exitosamente el usuario : " + usuario.getNombre();
                        resetear_campos();
                        siguiente_pantalla();
                    }else{
                        mensaje = "Ya existe un usuario asocidado con el correo : " + usuario.getKey() + usuario.getCorreo();
                    }
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
                    break;
                case AdminDB.COD_USUARIO_MODIFICADO:

                    if (resultado){
                        mensaje = "Se ha modificado exitosamente el usuario : " + usuario.getNombre();
                        resetear_campos();
                        siguiente_pantalla();
                    }else{
                        if (MODO_EDICION_2 == true){
                            BaseDatos.agregarUsuario(new AdminDB.OnDB_Listener(){

                                                         @Override
                                                         public void result(int codigo, Usuario usuario, boolean resultado) {
                                                             resultado_BD(codigo,usuario,resultado);
                                                         }

                                                     },
                                    usuario_modificado);
                            mensaje = "Te has registrado a la aplicación, desde ahora ya puedes ingresar con tu correo a la app.";
                        }else{
                            mensaje = "No se ha podido modificar el usuario.";
                        }

                    }
                    Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
                    break;

            }
        }catch (Exception e){
            log.registrar(this,"resultado_BD",e);
            log.alertar("Ocurrió un error al momento de gestionar el resultado de la base de datos. ",getActivity());

        }


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Toast.makeText(getActivity(),"AAAh queres irte piyuelo",Toast.LENGTH_SHORT).show();
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //Los argumentos (según la documentación de Android) deben ser pasados extrictamente al momento
    // de crear dicho fragment, en caso de enviarlo después no se adjuntarán.
    public static usuarioFragment newInstance(Bundle arguments){
        usuarioFragment f = new usuarioFragment();
        if(arguments != null){
            f.setArguments(arguments);
        }
        return f;
    }

    public usuarioFragment(){

    }

    private class DescargarImagenPerfil extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DescargarImagenPerfil(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {

            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {

                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
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
}

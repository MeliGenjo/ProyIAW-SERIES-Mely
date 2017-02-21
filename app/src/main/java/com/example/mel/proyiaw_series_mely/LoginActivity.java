package com.example.mel.proyiaw_series_mely;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.widget.Toast.LENGTH_SHORT;


public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AdminSQLiteOpenHelper BD;
    private Uri img;
    private String nameUser;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);

        //loginButton.setReadPermissions(Array.asList("email"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override

            // si el inicio de sesion es correcto
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                BD = new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);

                if (profile!=null) { //si el profile no es nulo obtengo los datos de facebook para almacenar en la BD
                    nameUser = profile.getName();
                    String idFaceBD = profile.getId();
                    img = profile.getProfilePictureUri(150,150); //obtengo la foto de perfil de FB
                    String msj = BD.altaUsuario(idFaceBD,nameUser); //cargo el usuario segun corresponda
                    Toast.makeText(getApplicationContext(),msj, Toast.LENGTH_SHORT).show();
                    irPantallaPrincipal();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.login_cancel, Toast.LENGTH_SHORT).show();
            }

           @Override
            public void onError(FacebookException e) {
               Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("Foto",img.toString()); //envio la foto de perfil a mainActivity
        intent.putExtra("NombreUsuario",nameUser);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

package com.example.mel.proyiaw_series_mely;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {

    private  ImageView fotoPerfil;
    private  TextView txt;
    private Button btnBuscar;
    private String nameUser;
    private Uri img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setearDatosUsuarios();

        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaBuscar();
            }
        });


        if (AccessToken.getCurrentAccessToken()== null) {
            irPantallaLogin();
        }

    }

    // permite setear la foto y el nombre del usuario
    private void setearDatosUsuarios() {

        fotoPerfil = (ImageView) findViewById(R.id.imageView);
        txt = (TextView) findViewById(R.id.textHola);

        Profile profile = Profile.getCurrentProfile();
        if (profile!=null) {
            nameUser = profile.getName();
            img = profile.getProfilePictureUri(150, 150);
            String foto = img.toString();
            Picasso.with(getApplicationContext()).load(foto).into(fotoPerfil); //seteo la foto al imageView
            txt.setText("Hola " + nameUser + "!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ){
            case R.id.itemBuscar:
                Toast.makeText(MainActivity.this, "Ir activity buscar", Toast.LENGTH_SHORT).show();
                irPantallaBuscar();
                return true;

            case R.id.itemCompartir:
                Toast.makeText(MainActivity.this, "Compartir contenido", Toast.LENGTH_SHORT).show();
                irPantallaCompartir();
                return true;

            case R.id.itemUsuarios:
                irPantallaUsuarios();
                return true;

            case R.id.itemLogout:
               // Toast.makeText(MainActivity.this, "Te esperamos pronto!!", Toast.LENGTH_SHORT).show();
                logout();
                return true;

            case R.id.itemCerrarApp:
               // Toast.makeText(MainActivity.this, "Hasta luego!", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void irPantallaUsuarios() {
        Intent intent = new Intent(this, UsuariosActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void irPantallaCompartir() {
        Intent intent = new Intent(this, ShareActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void irPantallaBuscar() {
        Intent intent = new Intent(this, buscarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void irPantallaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logout(){
        LoginManager.getInstance().logOut();
        irPantallaLogin();
    }


}

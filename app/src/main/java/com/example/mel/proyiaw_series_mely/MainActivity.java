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
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity {

    private ImageView fotoPerfil;
    private TextView txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        fotoPerfil = (ImageView) findViewById(R.id.imageView);
        txt = (TextView) findViewById(R.id.textHola);

        //traigo los extras(datos) mediante intent y los seteo a esta actividad
        Intent i = getIntent();
        Bundle extra = i.getExtras();
        if (extra!= null) {
            String foto = extra.getString("Foto");
            Picasso.with(getApplicationContext()).load(foto).into(fotoPerfil);
            String nombre = extra.getString("NombreUsuario");
            txt.setText("Hola "+nombre+"!");
        }



        if (AccessToken.getCurrentAccessToken()== null) {
            irPantallaLogin();
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
                Toast.makeText(MainActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                logout();
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

    private void shareFacebook(View v) {

    }
}

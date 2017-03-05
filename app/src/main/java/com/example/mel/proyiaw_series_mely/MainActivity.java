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

    private static final String TAG = "SERIES";
    private  ImageView fotoPerfil;
    private  TextView txt;
    private Button btnBuscar;
    private Button btnAgenda, verSeries, btnRecomendaciones,btnVerFavoritas;
    private String nameUser;
    private Uri img;
    private envioNotificaciones notificaciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setearDatosUsuarios();




        btnRecomendaciones = (Button) findViewById(R.id.btnRecomendaciones);
        btnRecomendaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaRecomendaciones();
            }
        });

        btnVerFavoritas = (Button) findViewById(R.id.btnVerFavoritas);
        btnVerFavoritas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaVerSeriesFavoritas();
            }
        });

        verSeries = (Button) findViewById(R.id.verSeries);
        verSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaVerSeries();
            }
        });

        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaBuscar();
            }
        });

        btnAgenda= (Button) findViewById(R.id.verAgenda);
        btnAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaAgenda();
            }
        });


        if (AccessToken.getCurrentAccessToken()== null) {
            irPantallaLogin();
        }

    }

    private void irPantallaVerSeriesFavoritas() {
        Intent intent = new Intent(this, MostrarFavoritasActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        Profile profile=Profile.getCurrentProfile();
        notificaciones=new envioNotificaciones();
        notificaciones.controlNotificaciones(this,profile.getId());
        startActivity(intent);
    }


    private void irPantallaVerSeries() {
        Intent intent = new Intent(this, MostrarSeriesActivity.class);
        Toast.makeText(getApplicationContext(), "Ir a ver series ", Toast.LENGTH_SHORT).show();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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




    private void irPantallaRecomendaciones() {
        Intent intent = new Intent(this, RecomendacionesActivity.class);
        Toast.makeText(getApplicationContext(), "Obtener recomendaciones ", Toast.LENGTH_SHORT).show();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

            case R.id.itemFavoritos:
                irPantallaFavoritos();
                return true;

            case R.id.itemLogout:
                Toast.makeText(MainActivity.this, "Te esperamos pronto!!", Toast.LENGTH_SHORT).show();
                logout();
                return true;

            case R.id.itemSeries:
                irPantallaSeries();
                return true;

            case R.id.itemCerrarApp:
                Toast.makeText(MainActivity.this, "Hasta luego!", Toast.LENGTH_SHORT).show();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void irPantallaFavoritos() {
        Intent intent = new Intent(this, FavoritosActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void irPantallaSeries() {
        Intent intent = new Intent(this, SeriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

    private void irPantallaAgenda(){
        Intent intent = new Intent(this, AgendaActivity.class);
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

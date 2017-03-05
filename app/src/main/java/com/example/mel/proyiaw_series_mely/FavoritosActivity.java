package com.example.mel.proyiaw_series_mely;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.Profile;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
/*
public class FavoritosActivity extends AppCompatActivity {

    ArrayList<String> lista;
    ArrayAdapter<String> adaptador;
    ListView lv;
    Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        Profile profile = Profile.getCurrentProfile();

        mostrarListadoFavoritos(profile.getId());

        home = (Button) findViewById(R.id.btnHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaPrincipal();
            }
        });
    }

    private void mostrarListadoFavoritos(String usuario) {
        AdminSQLiteOpenHelper BD = new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);
        lv = (ListView) findViewById(R.id.lista);

        lista = BD.obtenerSeries(usuario);
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lista);
        lv.setAdapter(adaptador);

    }
    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
*/
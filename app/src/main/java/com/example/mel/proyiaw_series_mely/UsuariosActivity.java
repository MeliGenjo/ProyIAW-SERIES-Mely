package com.example.mel.proyiaw_series_mely;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Mel on 21 feb 2017.
 */
public class UsuariosActivity extends AppCompatActivity {

    ArrayList <String> lista;
    ArrayAdapter<String> adaptador;
    ListView lv;
    Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.usuarios_activity);
        mostrarListadoUsuarios();
        home = (Button) findViewById(R.id.btnHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaPrincipal();
            }
        });

    }

    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void mostrarListadoUsuarios() {
        AdminSQLiteOpenHelper BD = new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);
        lv = (ListView) findViewById(R.id.lista);
        lista = BD.llenarLista();
        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lista);
        lv.setAdapter(adaptador);

    }


}

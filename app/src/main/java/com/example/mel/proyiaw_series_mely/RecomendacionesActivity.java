package com.example.mel.proyiaw_series_mely;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.android.volley.Request.*;

public class RecomendacionesActivity extends AppCompatActivity {

    private JsonObjectRequest jsObjRequest;
    private List<Item> seriesFavoritas = new ArrayList<Item>();
    private TextView mTxtDisplay;
    private ArrayList<String> generosFavoritos = new ArrayList<String>();
    Button home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendaciones);

        listarSeriesGeneroFavoritas();

        mTxtDisplay = (TextView) findViewById(R.id.textView5);


        mTxtDisplay.setText("TAREA SIN TERMINAR - MUY PRONTO ");

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

    private void listarSeriesGeneroFavoritas(){

        AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);
        ArrayList<Integer> idSeriesFavoritas = new ArrayList<Integer>();

        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {

            //idSeriesFavoritas = sql.obtenerSeries(profile.getId());

            //probando con dos series de id cualquiera
            idSeriesFavoritas.add(1);
            idSeriesFavoritas.add(2);


            for (int i=0; i<idSeriesFavoritas.size();i++){
                int ID = idSeriesFavoritas.get(i);
                String url = "http://api.tvmaze.com/shows/"+ID;

                cargarListaSeriesFavoritas(url);
                Toast.makeText(this, "cargue serie id "+ID, Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void cargarListaSeriesFavoritas(String url){
        //Log.e("UBICACION ","entrando");

        jsObjRequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
               try{
                        JSONObject obj=response;
                       // Log.e("objectJSON ","estoy dentro del json");

                        Item item=new Item();

                        item.setId(obj.getInt("id"));

                        item.setTitle(obj.getString("name"));

                        String nameS=obj.getString("name");
                        Log.e("SERIE ",nameS);

                        //image es un JSON, la url esta dentro de medium
                        JSONObject imagen = obj.getJSONObject("image");
                        item.setImage(imagen.getString("medium"));

                        //rating es un json, el puntaje esta dentro de average
                        JSONObject puntaje = obj.getJSONObject("rating");
                        double ptj = puntaje.getDouble("average");
                        item.setRate(ptj);

                        //premiered es una fecha tipo anio-mes-dia
                        String fecha = obj.getString("premiered");
                        String [] datos = fecha.split("-");
                        int anio = Integer.parseInt(datos[0]);
                        item.setYear(anio);

                        //genre is json array
                        JSONArray genreArray=obj.getJSONArray("genres");
                        ArrayList<String> genre=new ArrayList<String>();
                        for(int j=0;j<genreArray.length();j++){
                            genre.add((String) genreArray.get(j));
                            generosFavoritos.add((String) genreArray.get(j));
                        }

                        item.setGenre(genre);
                        //add to array
                        seriesFavoritas.add(item);
                    }catch(JSONException ex){
                        ex.printStackTrace();
                    }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Log.e("ErrorListener ","error" + error.getMessage());
            }
        });
        AppController.getmInstance().addToRequesQueue(jsObjRequest);

    }

}

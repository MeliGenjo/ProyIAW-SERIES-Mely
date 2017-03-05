package com.example.mel.proyiaw_series_mely;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class AgendaActivity extends AppCompatActivity {

    private TextView titulo;
    private int monthDay;
    private int month;
    private int year;
    private ProgressDialog progress;
    private ImageView imagen_capitulo;
    private ProgressDialog dialog;
    private List<Item> array = new ArrayList<Item>();
    private ListView listView;
    private Adapter adapter;
    private TextView nohaycap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        //Obtengo la fecha actual
        //Aclaración: como los meses arrancan en 0, le tengo que sumar 1
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        monthDay = today.monthDay;
        month = today.month +1;
        year= today.year;

        //Inicializo gráficos

        nohaycap= (TextView) findViewById(R.id.noHayCapitulos);

        titulo = (TextView) findViewById(R.id.Titulo);
        //titulo.setText("Capítulos estreno");
        listView = (ListView) findViewById(R.id.list_item);
        adapter=new Adapter(this,array);
        listView.setAdapter(adapter);

        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object o = listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                Log.i("Click", "click en el elemento " + position + " de mi ListView");

              /*  Item selItem = (Item) adapter.getItem(position);
                Log.i("TITULO", "titulo del elemento " + selItem.getTitle() + " de mi ListView");
                // Starting new intent
                Intent intent = new Intent(getApplicationContext(), verItemSerieActivity.class);
                intent.putExtra("titulo",selItem.getTitle());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/


            }
        });

        //Obtengo los episodios a estrenar o recientemente estrenados.

        List<String> id_series_favoritas= obtener_id_series_favoritas();
        obtener_estrenos(id_series_favoritas);

        /*dialog=new ProgressDialog(this);
        dialog.setMessage("Cargando estrenos...");
        dialog.show();*/
    }

    private List<String> obtener_id_series_favoritas() {
        List<String> idSeriesFavoritas = new ArrayList<String>();
        Profile profile = Profile.getCurrentProfile();
        AdminSQLiteOpenHelper sql = new AdminSQLiteOpenHelper(getApplicationContext(), null, null, 1);

        if (profile != null) {
            idSeriesFavoritas = sql.obtenerSeries(profile.getId());
        }
        return idSeriesFavoritas;

    }

    private void obtener_estrenos(List<String> id_series_favoritas){
        String mes_armado="";
        String dia_armado="";
        if(month<10){
           mes_armado="0"+month;
        }
        else
            mes_armado=""+month;
        if(monthDay<10){
            dia_armado="0"+monthDay;
        }
        else
            dia_armado=""+monthDay;

        int id=0;
        String url;

        if(id_series_favoritas.size()==0)
            nohaycap.setText("Aun no tienes series favoritas");

        for(id=0;id<id_series_favoritas.size();id++){
            //Por cada serie veo si hay estrenos en el día actual y del día siguiente
            //Hoy
         ver_si_hay_estrenos("http://api.tvmaze.com/shows/"+id_series_favoritas.get(id)+"/episodesbydate?date="+year+"-"+mes_armado+"-"+dia_armado,id_series_favoritas.get(id),"hoy");

            if((month!=2 && monthDay+1>31) || (month==2 &&monthDay+1>28)){
                dia_armado="01";
                if(month<12)
                    if(month>10)
                        mes_armado=(month+1)+"";
                    else
                        mes_armado="0"+(month+1);
                else
                    mes_armado="01";
            }
            else{
                if(monthDay+1<10)
                  dia_armado="0"+(monthDay +1);
                else
                    dia_armado=""+(monthDay+1);
            }

            //Mañana
              ver_si_hay_estrenos("http://api.tvmaze.com/shows/"+id_series_favoritas.get(id)+"/episodesbydate?date="+year+"-"+mes_armado+"-"+dia_armado,id_series_favoritas.get(id),"mañana");
            // ver_si_hay_estrenos("http://api.tvmaze.com/shows/1/episodesbydate?date=2013-07-01",1+"","mañana");

        }



    }

    private void ver_si_hay_estrenos(String url, final String id_serie, final String dia){
        Log.i("VER SI HAY","ENTRE AL METODO");
       // sendGetRequest("http://api.tvmaze.com/shows/1/episodesbydate?date=2013-07-01");
        //url="http://api.tvmaze.com/shows/1/episodesbydate?date=2013-07-01";

        JsonArrayRequest req = new JsonArrayRequest(url,
        new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("JSONARRAY", response.toString());

                try {
                    // Parsing json array response
                    // loop through each json object
                    //respuesta = "";
                    if(response.length()==0)
                        nohaycap.setText("No hay estrenos para visualizar");

                    for (int i = 0; i < response.length(); i++) {

                        JSONObject objeto = (JSONObject) response.get(i);
                        Item item=new Item();

                        String name = objeto.getString("name");
                        item.setTitle("Capitulo:" + name+": "+dia);

                        JSONObject imagen = objeto.getJSONObject("image");
                        item.setImage(imagen.getString("medium"));

                        ArrayList<String> a = new ArrayList<String>();
                        item.setGenre(a);
                        item.setId(0);
                        item.setRate(0);
                        item.setYear(0);

                        //respuesta += "Name: " + name + "\n\n";


                        //add to array
                        array.add(item);

                    }

                   // titulo.setText(respuesta);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                //hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("VOLLEY", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                       "No hay estrenos para visualizar", Toast.LENGTH_SHORT).show();
                nohaycap.setText("No hay estrenos para visualizar");
                //hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getmInstance().addToRequesQueue(req);
    }



}

package com.example.mel.proyiaw_series_mely;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MostrarFavoritasActivity extends AppCompatActivity {

    private int IDserie;
    private String url = "http://api.tvmaze.com/shows/"+ IDserie;

    private ProgressDialog dialog;
    private List<Item> array = new ArrayList<Item>();
    private ListView listView;
    private Adapter adapter;
    private JsonObjectRequest jsObjRequest;
    private List<String> idSeriesFavoritas = new ArrayList<String>();
    private List<String> urlFavoritos = new ArrayList<String>();
    private TextView sinFavoritos;

    private Button bAction, bDrama, bHorror, bRomance, bAdventure, bFantasy, bThriller, bCrime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_favoritas);

        listView = (ListView) findViewById(R.id.list_item);
        adapter=new Adapter(this,array);
        listView.setAdapter(adapter);

        /*dialog=new ProgressDialog(this);
        dialog.setMessage("Cargando Series Favoritas...");
        dialog.show();*/

        Profile profile = Profile.getCurrentProfile();
        AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);

        if (profile != null) {

            idSeriesFavoritas = sql.obtenerSeries(profile.getId());

            if (idSeriesFavoritas.size()==0){
                sinFavoritos = (TextView) findViewById(R.id.textView);
                sinFavoritos.setText("Aún no tienes series favoritas");
            }
            else {
                inicializarBotonGeneros();

                for (int i = 0; i < idSeriesFavoritas.size(); i++) {
                    urlFavoritos.add("http://api.tvmaze.com/shows/" + idSeriesFavoritas.get(i));
                }

                Toast.makeText(getApplicationContext(), "CANT URL " + urlFavoritos.size(), Toast.LENGTH_SHORT).show();
                inicializarLista(urlFavoritos);
            }
        }



    }


    private void inicializarBotonGeneros() {

        bAction = (Button)findViewById(R.id.bAction);
        bDrama = (Button)findViewById(R.id.bDrama);
        bHorror = (Button)findViewById(R.id.bHorror);
        bRomance = (Button)findViewById(R.id.bRomance);

        bAdventure = (Button)findViewById(R.id.bAdventure);
        bFantasy = (Button)findViewById(R.id.bFantasy);
        bThriller = (Button)findViewById(R.id.bThriller);
        bCrime = (Button)findViewById(R.id.bCrime);

        bCrime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Crime");
                listView.setSelection(0);
            }
        });

        bThriller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Thriller");
                listView.setSelection(0);
            }
        });

        bFantasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Fantasy");
                listView.setSelection(0);
            }
        });

        bAdventure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Adventure");
                listView.setSelection(0);
            }
        });

        bAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Action");
                listView.setSelection(0);
            }
        });

        bDrama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Drama");
                listView.setSelection(0);
            }
        });

        bHorror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Horror");
                listView.setSelection(0);
            }
        });

        bRomance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem("Romance");
                listView.setSelection(0);
            }
        });
    }


    private void inicializarLista(List<String> urlFavoritos){

        for(int i=0; i<urlFavoritos.size();i++) {
            jsObjRequest = new JsonObjectRequest(urlFavoritos.get(i), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    //hideDialog();
                    try {
                        JSONObject obj = response;
                        Item item = new Item();
                        item.setId(obj.getInt("id"));
                        item.setTitle(obj.getString("name"));

                        //image es un JSON, la url esta dentro de medium
                        JSONObject imagen = obj.getJSONObject("image");
                        item.setImage(imagen.getString("medium"));

                        //rating es un json, el puntaje esta dentro de average
                        JSONObject puntaje = obj.getJSONObject("rating");
                        double ptj = puntaje.getDouble("average");
                        item.setRate(ptj);

                        //premiered es una fecha tipo anio-mes-dia
                        String fecha = obj.getString("premiered");
                        String[] datos = fecha.split("-");
                        int anio = Integer.parseInt(datos[0]);
                        item.setYear(anio);

                        //genre is json array
                        JSONArray genreArray = obj.getJSONArray("genres");
                        ArrayList<String> genre = new ArrayList<String>();
                        for (int j = 0; j < genreArray.length(); j++) {
                            genre.add((String) genreArray.get(j));
                        }
                        item.setGenre(genre);

                        //add to array
                        array.add(item);
                        Log.e("Serie", "AGREGUE SERIE "+ item.getTitle());


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            AppController.getmInstance().addToRequesQueue(jsObjRequest);
            Log.e("URL",jsObjRequest.getUrl() );
        }
        adapter.notifyDataSetChanged();

    }

    public void searchItem(String textToSearch){

        //ordenar poniendo en primer lugar las series con genero textToSearch

        ArrayList<Item> contieneGenero = new ArrayList<Item>();
        ArrayList<Item> noContieneGenero = new ArrayList<Item>();


        for(int i=0; i<array.size(); i++) {
            Item item = array.get(i);
            ArrayList<String> generos = item.getGenre();
            if (!generos.contains(textToSearch)) {
                noContieneGenero.add(item);
            }
            else
                contieneGenero.add(item);
        }

        //tengo las dos listas, las concateno primero la que si contiene genero buscado
        array.clear();
        for(int i=0; i<contieneGenero.size(); i++) {
            Item item = contieneGenero.get(i);
            array.add(item);
        }
        for(int i=0; i<noContieneGenero.size(); i++) {
            Item item = noContieneGenero.get(i);
            array.add(item);
        }

        Toast.makeText(this, "Series genero "+textToSearch, Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();

    }

    public void hideDialog(){
        if(dialog !=null){
            dialog.dismiss();
            dialog=null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {

            case R.id.volverHome:
                irPantallaPrincipal();
                return true;

            case R.id.sortNombre:
                Toast.makeText(this, "Series ordenadas por nombre", Toast.LENGTH_SHORT).show();
                sortByTitle();
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                return true;

            case R.id.sortAnio:
                Toast.makeText(this, "Series ordenadas por anio", Toast.LENGTH_SHORT).show();
                sortByYear();
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                return true;

            case R.id.sortRaiting:
                Toast.makeText(this, "Series ordenadas por Raiting", Toast.LENGTH_SHORT).show();
                sortByRate();
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                return true;

            case R.id.sortGenero:
                Toast.makeText(this, "Series ordenadas por Genero", Toast.LENGTH_SHORT).show();
                sortByGenero();
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                return true;



            default:
                return super.onOptionsItemSelected(item);

        }
    }


    public void sortByTitle (){
        Collections.sort(array);
    }

    public void sortByYear (){
        Collections.sort(array, Item.comparatorAnio());
    }

    public void sortByRate(){
        Collections.sort(array, Item.comparatorPtje());
    }

    public void sortByGenero(){
        Collections.sort(array, Item.comparatorGenero());
    }

}

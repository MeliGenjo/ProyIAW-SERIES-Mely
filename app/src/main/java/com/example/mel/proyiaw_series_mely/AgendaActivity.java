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

    String respuesta="";

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

        titulo = (TextView) findViewById(R.id.Titulo);
        titulo.setText("Capítulos estreno");
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

        for(id=0;id<id_series_favoritas.size();id++){
            //Por cada serie veo si hay estrenos en el día actual y del día siguiente
            //Hoy
         //  ver_si_hay_estrenos("http://api.tvmaze.com/shows/"+id_series_favoritas.get(id)+"/episodesbydate?date="+year+"-"+mes_armado+"-"+dia_armado);

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
           // ver_si_hay_estrenos("http://api.tvmaze.com/shows/"+id_series_favoritas.get(id)+"/episodesbydate?date="+year+"-"+mes_armado+"-"+dia_armado);
            ver_si_hay_estrenos("http://api.tvmaze.com/shows/1/episodesbydate?date=2013-07-01");

        }



    }

    private void ver_si_hay_estrenos(String url){
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
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject objeto = (JSONObject) response.get(i);
                        Item item=new Item();

                        String name = objeto.getString("name");
                        item.setTitle(name);

                        JSONObject imagen = objeto.getJSONObject("image");
                        item.setImage(imagen.getString("medium"));

                        ArrayList<String> a = new ArrayList<String>();
                        item.setGenre(a);
                        item.setId(1);
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
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                //hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getmInstance().addToRequesQueue(req);
    }









    //PARA HACER GET ASINCRONICO
    public void sendGetRequest(String sitio) {

        new GetClass(this,sitio).execute();
    }



    /*********************************************************************************************
     *                   Clase para la obtención de datos de la serie
     *********************************************************************************************/

    private class GetClass extends AsyncTask<String, Void, Void> {

        private final Context context;
        private final String sitio;


        //Atributos de la serie obtenidos del JSon
        protected String json;
        protected String nombre_string;
        protected String id_serie;
        protected String horario;
        protected String [] dias;
        protected String duracion;
        protected String descripcion;
        protected String [] genero;
        protected String puntaje;
        protected String año;
        protected String url_imagen;


        public GetClass(Context c, String sitio){
            this.context = c;
            this.sitio=sitio;


            //Inicializo valores de la serie a buscar
            json="";
            nombre_string="";
            id_serie="";
            horario="";
            dias= new String[5];
            duracion="";
            descripcion="";
            genero= new String[3];
            puntaje="";
            año="";
            url_imagen="";
        }

        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Buscando");
            progress.show();
            Log.i("Entre aca","sali de aca");
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL url = new URL(sitio);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

               /* if(responseCode != HttpURLConnection.HTTP_OK){
                    String msj="NO ES 200 OK";
                    Toast.makeText(getApplicationContext(),msj, Toast.LENGTH_SHORT).show();
                }*/

                final StringBuilder output = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // String jsonString="";


                String line = "";
                // StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    // responseOutput.append(line);
                    json+=line; //meti todo el json aca
                }
                br.close();

                try {
                    JSONArray jsonArray = new JSONArray(json);
                    Log.i("JSONARRAY","JSON TAMAÑO "+jsonArray.length());
                    JSONObject mJsonObject = jsonArray.getJSONObject(0);


                    String name = mJsonObject.getString("name");
                    nombre_string=name;
                    Log.i("JSON","el name es:" +name);


                   // if(jsonArray.length()>0) {
                      //  JSONObject jsonObject = jsonArray.getJSONObject(0);
                      //  Log.i("JSON","JSON "+jsonObject.length());
                       /* JSONObject show = (JSONObject) jsonObject.get("show");

                        id_serie = show.optString("id").toString();
                        output.append("Id serie: " + id_serie + "\n\n");
                        idSerie=id_serie;
                        nombre_string = show.optString("name").toString();

                        JSONObject schedule = (JSONObject) show.get("schedule");
                        horario=schedule.optString("time").toString();
                        output.append("Horario: " + horario + "\n\n");
                        // dias=schedule.optString("days");

                        duracion=show.optString("runtime").toString();
                        output.append("Duración: " + duracion + "minutos"+ "\n\n");


                        String premiered = show.optString("premiered").toString();
                        output.append("Fecha de lanzamiento :" + premiered + "\n\n");

                        descripcion = show.optString("summary").toString();
                        output.append("Resumen: " + descripcion + "\n\n");

                        JSONObject imagen = (JSONObject) show.get("image");
                        url_imagen = imagen.get("medium").toString();
                        //output.append(imagen.get("medium"));*/
                   // }
                    //obtener id serie

                    //}

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //output.append(jsonString.toString());
                AgendaActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                       /* //datosSerie.setText(descripcion);
                        datosSerie.setText(output);
                        nombre.setText(nombre_string);
                        progress.dismiss();
                        //url_imagen_serie=url_imagen;
                        cargar_imagen(url_imagen);*/
                        titulo.setText(nombre_string);
                    }
                });


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }


        private void cargar_imagen(String url){
            if(url!=null){
                CargaImagenes nuevaTarea = new CargaImagenes();
                nuevaTarea.execute(url);}
            else {
                String msj="La imagen no ha sido cargada";
                Toast.makeText(getApplicationContext(),msj, Toast.LENGTH_SHORT).show();
            }
        }



    }//GetClass


    /*********************************************************************************************
     *                   Clase para la carga de imágenes de la serie
     *********************************************************************************************/

    private class CargaImagenes extends AsyncTask<String, Void, Bitmap>{

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            pDialog = new ProgressDialog(AgendaActivity.this);
            pDialog.setMessage("Cargando Imagen");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground" , "Entra en doInBackground");
            String url = params[0];
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            //imagen_serie.setImageBitmap(result);
            pDialog.dismiss();
        }

        private Bitmap descargarImagen (String imageHttpAddress){
            URL imageUrl = null;
            Bitmap imagen = null;
            try{
                imageUrl = new URL(imageHttpAddress);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.connect();
                imagen = BitmapFactory.decodeStream(conn.getInputStream());
            }catch(IOException ex){
                ex.printStackTrace();
            }

            return imagen;
        }

    }



}

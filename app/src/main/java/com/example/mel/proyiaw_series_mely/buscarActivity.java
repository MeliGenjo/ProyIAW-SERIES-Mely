package com.example.mel.proyiaw_series_mely;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.visible;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class buscarActivity extends AppCompatActivity {


    private Button buscar;
    private Button agregar_favoritos;
    private Button verCapitulos;
    private String idSerie;
    private String sitio;
    private String serie_usuario;
   // private EditText serie_ingresada;
    private TextView contenido_serie;
    private TextView nombre_serie;
    private TextView horario_serie;
    private TextView duracion_serie;
    private TextView puntaje_serie;
    private ImageView imagen_serie;
    private ScrollView scroll;
    private ProgressDialog progress;
    private  ArrayAdapter adapter;

    private String titulo;


    //Autocomplete
    private List<String> arreglo_nombres = new ArrayList<String>();
    JsonArrayRequest jsonArrayRequest;
    AutoCompleteTextView autocompletar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buscar);

        autocompletar= (AutoCompleteTextView) findViewById (R.id.texto_ingresado);
        crear_arreglo_nombres();
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);

        //Serie a buscar ingresada por el usuario
        serie_usuario="";
        titulo="";

        //Boton para buscar la serie
        buscar = (Button) findViewById(R.id.boton_buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarSerie();
            }
        });

        //Contenido de la serie buscada
        contenido_serie= (TextView) findViewById(R.id.contenidoSerie);
        nombre_serie=(TextView) findViewById(R.id.nombre_serie);
        horario_serie=(TextView) findViewById(R.id.horario);
        duracion_serie = (TextView) findViewById(R.id.duracion);
        puntaje_serie = (TextView) findViewById(R.id.puntaje);
        imagen_serie = (ImageView)findViewById(R.id.imagen);
        scroll=(ScrollView) findViewById(R.id.scroll);

        //Agrego una serie a favoritos
        agregar_favoritos= (Button) findViewById(R.id.agregarAFavoritos);
        agregar_favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    agregar_a_favoritos(profile.getId(), idSerie);
                   // Log.e("favorito",profile.getId()+" "+ idSerie);
                }
            }
        });

        verCapitulos= (Button) findViewById(R.id.verCapitulos);
        verCapitulos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaCapitulos(idSerie);
            }
        });
    }


    private void crear_arreglo_nombres(){
        String url="";

        for(int i=0; i<100;i++) {
            url = "http://api.tvmaze.com/shows?page="+i;

            inicializarListaNombres(url);
        }

        adapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,arreglo_nombres);
        autocompletar.setThreshold(1);
        autocompletar.setAdapter(adapter);
    }

    private void inicializarListaNombres(String url){


        jsonArrayRequest=new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //parsing json
                for(int i=0;i<response.length();i++){
                    try{
                        JSONObject obj=response.getJSONObject(i);
                        arreglo_nombres.add(obj.getString("name"));

                    }catch(JSONException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JSON Error","onErrorResponse de inicializarLista" );
            }
        });
        AppController.getmInstance().addToRequesQueue(jsonArrayRequest);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buscar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ) {

            case R.id.volverHome:
                irPantallaPrincipal();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void agregar_a_favoritos(String usuario, String  serie){
        AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);

       boolean guardarFav= sql.guardarFavoritas(usuario,serie);
        String msj;
        if (guardarFav){
            msj="La serie se agrego a sus favoritas con éxito";
        }else{
            msj="Hubo un error al guardar como favorita su serie";
        }
         Toast.makeText(getApplicationContext(),msj, Toast.LENGTH_SHORT).show();
    }


    private void buscarSerie(){
        serie_usuario=autocompletar.getText().toString();

        if (!serie_usuario.equals("")) {

            sitio= "http://api.tvmaze.com/search/shows?q="+serie_usuario;

            sendGetRequest(sitio,contenido_serie,nombre_serie);


        }
        else{
            String msj="Ingrese el nombre de la serie a buscar";
            Toast.makeText(getApplicationContext(),msj, Toast.LENGTH_SHORT).show();
        }
    }


    public void sendGetRequest(String sitio,TextView datosSerie,TextView nombre) {

       new GetClass(this,sitio,datosSerie,nombre).execute();
    }

        /* pantalla de capitulos */
    private void irPantallaCapitulos(String idSerie) {
        Intent intent = new Intent(this, CapitulosActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("idserie",idSerie);
        intent.putExtra("esFavorito","false");
        intent.putExtra("titulo",titulo);
        intent.putExtra("vengoDe","verSeries");
        startActivity(intent);
    }
    /*********************************************************************************************
     *                   Clase para la obtención de datos de la serie
     *********************************************************************************************/

    private class GetClass extends AsyncTask<String, Void, Void> {
        private final Context context;
        private final String sitio;
        private final TextView datosSerie;
        private final TextView nombre;

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


        public GetClass(Context c, String sitio, TextView datosSerie,TextView nombre){
            this.context = c;
            this.sitio=sitio;
            this.datosSerie=datosSerie;
            this.nombre=nombre;

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


                final StringBuilder output = new StringBuilder("");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));


                String line = "";

                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {

                    json+=line; //meti todo el json aca
                }
                br.close();


                try {
                    JSONArray jsonArray = new JSONArray(json);



                    //Me faltan obtener los dias y el género pero son arreglos y no me deja obtener
                    //el arreglo como un arreglo, sino que me lo setea a string -> tengo que ver eso
                    if(jsonArray.length()>0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        JSONObject show = (JSONObject) jsonObject.get("show");

                        id_serie = show.optString("id").toString();
                        output.append("Id serie: " + id_serie + "\n\n");
                        idSerie=id_serie;
                        nombre_string = show.optString("name").toString();
                        titulo=nombre_string;

                        JSONObject schedule = (JSONObject) show.get("schedule");
                        horario=schedule.optString("time").toString();
                        output.append("Horario: " + horario + "\n\n");
                       // dias=schedule.optString("days");

                        duracion=show.optString("runtime").toString()+" minutos";
                        output.append("Duración: " + duracion + "minutos"+ "\n\n");


                        String premiered = show.optString("premiered").toString();
                        output.append("Fecha de lanzamiento :" + premiered + "\n\n");

                        descripcion = show.optString("summary").toString();
                        output.append("Resumen: " + descripcion + "\n\n");

                        JSONObject rat = (JSONObject) show.get("rating");
                        puntaje = rat.optString("average").toString();

                        JSONObject imagen = (JSONObject) show.get("image");
                        url_imagen = imagen.get("medium").toString();
                        //output.append(imagen.get("medium"));
                    }
                        //obtener id serie

                    //}

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (json.equals("[]")){
                    output.append("La serie ingresada no existe");

                }


                //output.append(jsonString.toString());
                buscarActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //datosSerie.setText(output);


                        scroll.setVisibility(View.VISIBLE);

                        datosSerie.setText(Html.fromHtml(descripcion));
                        nombre.setText(nombre_string);
                        horario_serie.setText(horario);
                        duracion_serie.setText(duracion);
                        puntaje_serie.setText(puntaje);
                        progress.dismiss();
                        //url_imagen_serie=url_imagen;
                        cargar_imagen(url_imagen);
                        if(control_de_favoritas(id_serie)){
                            agregar_favoritos.setVisibility(View.GONE);
                        }
                        else
                            agregar_favoritos.setVisibility(View.VISIBLE);

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

        private boolean control_de_favoritas(String id_serie){
            Profile profile = Profile.getCurrentProfile();
            AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);
            List<String> idSeriesFavoritas = new ArrayList<String>();
            boolean encontre=false;
            if (profile != null) {

                idSeriesFavoritas = sql.obtenerSeries(profile.getId());
                int i=0;
                while(i<idSeriesFavoritas.size()&&!encontre){
                  if(idSeriesFavoritas.get(i).equals(id_serie)){
                            encontre=true;
                  }
                  i ++;
                }
            }
            return encontre;
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

            pDialog = new ProgressDialog(buscarActivity.this);
            pDialog.setMessage("Cargando Imagen");
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.show();

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
           // Log.i("doInBackground" , "Entra en doInBackground");
            String url = params[0];
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            imagen_serie.setImageBitmap(result);
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





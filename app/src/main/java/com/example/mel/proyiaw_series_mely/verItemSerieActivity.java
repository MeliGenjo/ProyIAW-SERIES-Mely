package com.example.mel.proyiaw_series_mely;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class verItemSerieActivity extends AppCompatActivity {

    private String titulo;

    private String sitio;
    private String genero;
    private String idSerie;
    private String año;
    private String rate;

    private TextView nombre_serie;
    private TextView contenido_serie;
    private ImageView imagen_serie;
    private Button agregar_favoritos;
    private Button verCapitulos;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_item_serie);

        titulo = getIntent().getStringExtra("titulo");
        nombre_serie= (TextView) findViewById(R.id.titulo);
        nombre_serie.setText(titulo);

        contenido_serie= (TextView) findViewById(R.id.contenido);
        imagen_serie = (ImageView) findViewById(R.id.image);

        verCapitulos = (Button) findViewById(R.id.verCap);
        verCapitulos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaCapitulos();
            }
        });

        //Agrego una serie a favoritos
        agregar_favoritos= (Button) findViewById(R.id.agregarFav);
        agregar_favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    agregar_a_favoritos(profile.getId(), idSerie);
                    Log.e("favorito",profile.getId()+" "+ idSerie);
                }
            }
        });

        buscarSerie();
    }


    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /* pantalla de capitulos favoritos*/
    private void irPantallaCapitulos() {
        Intent intent = new Intent(this, CapitulosActivity.class);
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

            sitio= "http://api.tvmaze.com/search/shows?q="+titulo;
            sendGetRequest(sitio,contenido_serie,nombre_serie);
            agregar_favoritos.setVisibility(View.VISIBLE);
            verCapitulos.setVisibility(View.VISIBLE);


    }

    public void sendGetRequest(String sitio,TextView datosSerie,TextView nombre) {

        new GetClass(this,sitio,datosSerie,nombre).execute();
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

                /*if(responseCode != HttpURLConnection.HTTP_OK){
                    String msj="NO ES 200 OK: "+url_imagen_serie;
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
                    //String name;
                   /* TextView nombre = (TextView) findViewById(R.id.nombreSerie);
                    nombre.setText("");*/
                    //for (int i=0; i<jsonArray.length();i++){


                    //Me faltan obtener los dias y el género pero son arreglos y no me deja obtener
                    //el arreglo como un arreglo, sino que me lo setea a string -> tengo que ver eso
                    if(jsonArray.length()>0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        JSONObject show = (JSONObject) jsonObject.get("show");

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

                        Log.i("URL IMAGEN VER ITEM", "URL: "+url_imagen);
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
                else{
                    //Muestro el contenido y hago visibles los botones de agregar a favoritos y ver capitulos
                    // agregar_favoritos.setVisibility(View.VISIBLE);
                }

                //output.append(jsonString.toString());
                verItemSerieActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //datosSerie.setText(descripcion);
                        datosSerie.setText(output);
                        nombre.setText(nombre_string);
                        progress.dismiss();
                        //url_imagen_serie=url_imagen;
                        cargar_imagen(url_imagen);
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
                verItemSerieActivity.CargaImagenes nuevaTarea = new verItemSerieActivity.CargaImagenes();
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

            pDialog = new ProgressDialog(verItemSerieActivity.this);
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

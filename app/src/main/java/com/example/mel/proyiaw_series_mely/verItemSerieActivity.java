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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private TextView titulo_link;
    private TextView link_ver_serie;
    private TextView tv_horario;
    private TextView tv_duracion;
    private TextView tv_puntaje;
    private ImageView imagen_serie;
    private Button agregar_favoritos;
    private Button eliminar_favoritos;
    private Button verCapitulos;
    private ProgressDialog progress;

    private String vengoDe;

    private String es_favorita; // true = es favorita false=no es
    private int hay_link; //200 hay 404 no hay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_item_serie);

        titulo = getIntent().getStringExtra("titulo");
        if (titulo.equals("")){
            vengoDe="verSeries";
            retrocederPantalla();
        }



        vengoDe= getIntent().getStringExtra("vengoDe");
        es_favorita = getIntent().getStringExtra("esfavorita");

        nombre_serie= (TextView) findViewById(R.id.titulo);
        nombre_serie.setText(titulo);

        contenido_serie= (TextView) findViewById(R.id.contenido);
        imagen_serie = (ImageView) findViewById(R.id.image);

        verCapitulos = (Button) findViewById(R.id.verCap);
        verCapitulos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaCapitulos(idSerie);
            }
        });

        //Agrego una serie a favoritos
        agregar_favoritos= (Button) findViewById(R.id.agregarFav);
        if (es_favorita.equals("true")){
            agregar_favoritos.setText("Eliminar de Fav.");
        }else{
            agregar_favoritos.setText("Agregar a Favoritas");
        }
        agregar_favoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = Profile.getCurrentProfile();
                if (profile != null) {
                    if (es_favorita.equals("true")) {
                        eliminar_lista_favoritos(profile.getId(), idSerie);
                        agregar_favoritos.setVisibility(View.GONE);
                        Log.e("favorito",profile.getId()+" "+ idSerie);

                    }else{
                        agregar_a_favoritos(profile.getId(), idSerie);
                        Log.e("favorito",profile.getId()+" "+ idSerie);
                        agregar_favoritos.setVisibility(View.GONE);
                    }

                }
            }
        });


       if(es_favorita.equals("true")){
            agregar_favoritos.setVisibility(View.GONE);
        }

        tv_horario=(TextView) findViewById(R.id.horario);
        tv_duracion=(TextView) findViewById(R.id.duracion);
        tv_puntaje=(TextView) findViewById(R.id.puntaje);

        titulo_link = (TextView) findViewById(R.id.titulo_link);
        link_ver_serie= (TextView) findViewById(R.id.link);


        hay_link=404;

        verificar_links();

        buscarSerie();
    }

    public void verificar_links(){

        String tit=titulo.replace(" ","%20");
        String url= "http://www.seriesblanco.com/search.php?q1="+tit;

        sendGetValidacion(url);
        if(hay_link==404) {
            url = "http://www.verseriesynovelas.tv/archivos/h1/?s=" + tit;
            sendGetValidacion(url);
           /* if(hay_link==404){
                titulo_link.setText("No hay links disponibles");
            }*/
        }
    }

    private void eliminar_lista_favoritos(String id, String idSerie) {
        AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);

        boolean guardarFav= sql.eliminarFavoritas(id,idSerie);
        String msj;
        if (guardarFav){
            msj="La serie se elimino de sus favoritas con éxito";
        }else{
            msj="No se pudo eliminar su serie favorita ";
        }
        Toast.makeText(getApplicationContext(),msj, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item_serie, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId() ){

            case R.id.back:
                retrocederPantalla();
                return true;

            case R.id.share:
                compartirSerie();
                return true;

            case R.id.home:
                irPantallaPrincipal();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void retrocederPantalla() {

        if (vengoDe.equals("Recomendaciones")) {
            Intent intent = new Intent(this, RecomendacionesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (vengoDe.equals("verSeries")) {
            Intent intent = new Intent(this, MostrarSeriesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (vengoDe.equals("Favoritas")) {
            Intent intent = new Intent(this, MostrarFavoritasActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }

    private void compartirSerie() {
        String sitio = "http://www.tvmaze.com/shows/"+idSerie;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, sitio);
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /* pantalla de capitulos favoritos*/
    private void irPantallaCapitulos(String idSerie) {
        Intent intent = new Intent(this, CapitulosActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("titulo",titulo);
        intent.putExtra("idserie",idSerie);
        intent.putExtra("esFavorito",es_favorita);
        intent.putExtra("vengoDe",vengoDe);


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

    public void sendGetValidacion(String sitio) {

        new GetClassValidacion(this,sitio).execute();
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

                        JSONObject rat = (JSONObject) show.get("rating");
                        puntaje = rat.optString("average").toString();

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
                        tv_horario.setText(horario);
                        tv_duracion.setText(duracion);
                        tv_puntaje.setText(puntaje);
                        datosSerie.setText(Html.fromHtml(descripcion));
                        nombre.setText(nombre_string);
                        progress.dismiss();
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

    /*********************************************************************************************
     *                   Clase para la validación de links para ver la serie
     *********************************************************************************************/

    private class GetClassValidacion extends AsyncTask<String, Void, Void> {
        private final Context context;
        private final String sitio;

        public GetClassValidacion(Context c, String sitio){
            this.context = c;
            this.sitio=sitio;
        }

        protected void onPreExecute(){
          //  progress= new ProgressDialog(this.context);
          //  progress.setMessage("Buscando link");
          //  progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL url = new URL(sitio);

                Log.i("RECIEN ENTRE","ENTRE RECIEN con este url "+sitio);

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "fizz=buzz";
                connection.setRequestMethod("GET");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                Log.i("RESPONSE CODE","CODE:"+responseCode);
                if(responseCode != HttpURLConnection.HTTP_OK){
                    hay_link=404;
                }
                else
                    hay_link=200;

                //output.append(jsonString.toString());
                verItemSerieActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if(hay_link==200){
                            link_ver_serie.setText(sitio);
                            titulo_link.setText("Link para ver serie online");
                        }
                        else{
                            Log.i("LINK","NO HAY LINK");
                        }
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


    }//GetClass


}

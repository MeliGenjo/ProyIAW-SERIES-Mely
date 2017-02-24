package com.example.mel.proyiaw_series_mely;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class buscarActivity extends AppCompatActivity {


    private Button home;
    private Button buscar;
    private String sitio;
    private String serie_usuario;
    private EditText serie_ingresada;
    private TextView contenido_serie;
    private TextView nombre_serie;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        //Serie a buscar ingresada por el usuario
        serie_usuario="";
        serie_ingresada= (EditText) findViewById(R.id.texto_ingresado);

        //Boton para volver al home
        home = (Button) findViewById(R.id.btnHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaPrincipal();
            }
        });

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
    }

    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void buscarSerie(){
        serie_usuario=serie_ingresada.getText().toString();

        if (!serie_usuario.equals("")) {
            sitio= "http://api.tvmaze.com/search/shows?q=" + serie_usuario;
            //contenido_serie.setText(sitio);
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

    private class GetClass extends AsyncTask<String, Void, Void> {
        private final Context context;
        private final String sitio;
        private final TextView datosSerie;
        private final TextView nombre;

        public GetClass(Context c, String sitio, TextView datosSerie,TextView nombre){
            this.context = c;
            this.sitio=sitio;
            this.datosSerie=datosSerie;
            this.nombre=nombre;
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

                String jsonString="";

                String line = "";
               // StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    // responseOutput.append(line);
                    jsonString+=line; //meti todo el json aca
                }
                br.close();


                try {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    String name;
                   /* TextView nombre = (TextView) findViewById(R.id.nombreSerie);
                    nombre.setText("");*/
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        JSONObject show= (JSONObject) jsonObject.get("show");
                        String id_serie = show.optString("id").toString();
                        output.append("Id serie: " + id_serie +"\n\n");
                        name = show.optString("name").toString();
                        output.append("Nombre serie: " + name +"\n\n");
                        String lenguaje = show.optString("language").toString();
                        output.append("Lenguaje original: " + lenguaje +"\n\n");
                        String premiered = show.optString("premiered").toString();
                        output.append("Fecha de lanzamiento :" + premiered + "\n\n");
                        String summary = show.optString("summary").toString();
                        output.append("Resumen: " + summary + "\n\n");
                        JSONObject imagen = (JSONObject) show.get("image");
                        output.append(imagen.get("medium"));
                        //obtener id serie

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (jsonString.equals("[]")){
                    output.append("La serie ingresada no existe");

                }

                //output.append(jsonString.toString());
                buscarActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        datosSerie.setText(output);
                        progress.dismiss();

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


    } //fin GetClass



}

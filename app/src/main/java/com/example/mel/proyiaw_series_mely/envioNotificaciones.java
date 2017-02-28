package com.example.mel.proyiaw_series_mely;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static java.net.Proxy.Type.HTTP;

/**
 * Created by Gimena on 26/02/2017.
 */

public class envioNotificaciones {
   public final static Uri urlSerie = Uri.parse("http://api.tvmaze.com/");

    public void controlNotificaciones(String $usuario){
   /**    AdminSQLiteOpenHelper sql= new AdminSQLiteOpenHelper(getApplicationContext(),null, null, 1);
        //obtengo las series asociadas al usuario
       ArrayList<Integer> listSeries= sql.obtenerSeries($usuario);
 if (listSeries!=null) {
     for (Integer idSerie : listSeries) { //recorro las series pra ver si hay notificaciones que mandar
         String subtitulo = "";
         String NuevaTemporada = nuevasTemporadas(idSerie); //controlo nueva temporada
         if (NuevaTemporada != null) {
             subtitulo = NuevaTemporada;
         } else { //sino hay  temporada nueva controlo nuevo capitulo
             String NuevoCapitulo = nuevosCapitulos(idSerie);
             if (NuevoCapitulo != null) {
                 subtitulo = NuevoCapitulo;
             }
         }
         if (subtitulo != "") {
             try {
                 armarNotificacion(subtitulo);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
 }  */
            GetClass g = new GetClass();
        new GetClass().execute();
    }
    public String nuevosCapitulos(Integer $idSerie)
    {
        http://api.tvmaze.com/shows/1/episodebynumber?season=1&number=1
        return null;
    }

    public String nuevasTemporadas(Integer $idSerie)
    {
        http://api.tvmaze.com/shows/1/seasons
        return null;
    }




    private class GetClass extends AsyncTask<String, Void, Void> {
        private static final String APP_TAG = "SERIES" ;


        @Override
        protected Void doInBackground(String... params) {
        /**URL url = new URL("https://fcm.googleapis.com/fcm/send");
        Map<String, Object> params = new LinkedHashMap<>();

        params.put("to", FirebaseInstanceId.getInstance().getToken());
        params.put("notificacion", "notificacion de prueba");

        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (postData.length() != 0)
                postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()),
                    "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "application/json");
        conn.setRequestProperty("Content-Length",
                String.valueOf(postDataBytes.length));
        conn.setRequestProperty("Authorization:","key=AAAAFRaNPEA:APA91bEmH8J-dUttPKvJqsfWeS1-t5XDGOY_jaGV0lBofukHVd8p7jkc7RB4rZ98CRQ8jm0Oq942PlHdLGZai8kte6Xx-_T_XkJPjxFT-8dOiDtgbNNsH3mzHpakHD-7XT4fNhqXgGtb");

        conn.setDoOutput(true);
        BufferedReader in = null;
        String baseUrl = "https://fcm.googleapis.com/fcm/send";
        String jsonData ="{\"title\": \"Noticia desde el servidor\"}";
        try {
            //Creamos un objeto Cliente HTTP para manejar la peticion al servidor
            HttpClient httpClient = new DefaultHttpClient();
            //Creamos objeto para armar peticion de tipo HTTP POST
            HttpPost post = new HttpPost(baseUrl);

            //Configuramos los parametos que vaos a enviar con la peticion HTTP POST
            List<NameValuePair> nvp = new ArrayList<NameValuePair>(2);

          nvp.add(new BasicNameValuePair("to", FirebaseInstanceId.getInstance().getToken()));
          nvp.add(new BasicNameValuePair("notification", jsonData));

            post.setHeader("Content-type", "application/json");

           post.setHeader("Authorization","key=AAAAFRaNPEA:APA91bEmH8J-dUttPKvJqsfWeS1-t5XDGOY_jaGV0lBofukHVd8p7jkc7RB4rZ98CRQ8jm0Oq942PlHdLGZai8kte6Xx-_T_XkJPjxFT-8dOiDtgbNNsH3mzHpakHD-7XT4fNhqXgGtb");
            post.setEntity(new UrlEncodedFormEntity(nvp));

            //Se ejecuta el envio de la peticion y se espera la respuesta de la misma.
            HttpResponse response = httpClient.execute(post);

            Log.d(APP_TAG, response.getStatusLine().toString());
            //Obtengo el contenido de la respuesta en formato InputStream Buffer y la paso a formato String
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();


        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
         */
            URL url = null;
            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");

            Log.d(APP_TAG, url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            Log.d(APP_TAG, "hola");
            String urlParameters = "{\"to\": \""+FirebaseInstanceId.getInstance().getToken()+"\",\"notification\": {\"title\": \"Noticia desde el servidor\",\"body\": \"Descripción de la noticia desde el servidor,\"},}";
            Log.d(APP_TAG,urlParameters);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=AAAAFRaNPEA:APA91bEmH8J-dUttPKvJqsfWeS1-t5XDGOY_jaGV0lBofukHVd8p7jkc7RB4rZ98CRQ8jm0Oq942PlHdLGZai8kte6Xx-_T_XkJPjxFT-8dOiDtgbNNsH3mzHpakHD-7XT4fNhqXgGtb");


        int responseCode = connection.getResponseCode();

        Log.d(APP_TAG,"\nSending 'POST' request to URL : " + url);
        Log.d(APP_TAG,"Post parameters : " + urlParameters);
        Log.d(APP_TAG,"Response Code : " + responseCode);

            final StringBuilder output = new StringBuilder("");
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // String jsonString="";


            String line = "";
            // StringBuilder responseOutput = new StringBuilder();
            System.out.println("output===============" + br);
            br.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return null;
            }
    }
}


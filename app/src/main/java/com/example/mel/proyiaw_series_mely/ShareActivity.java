package com.example.mel.proyiaw_series_mely;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ShareActivity extends AppCompatActivity {

    private Button share, home;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_share);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        //shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() { ... });

        home = (Button) findViewById(R.id.btnHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irPantallaPrincipal();
            }
        });


        // opción compartir con cualquier red social


        share = (Button) findViewById(R.id.btnShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sitio = "http://www.tvmaze.com/shows/4144/imposters"; //CAMBIAR ESTO segun el sitio que quiera compartir
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, sitio);
                startActivity(Intent.createChooser(intent, "Share with"));
            }
        });

    }

    private void irPantallaPrincipal(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void shareContenido(View view) {
        try{
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Proyecto IAW")
                        .setContentDescription("Vamos que podemos :)")
                        .setContentUrl(Uri.parse("https://www.elandroidelibre.com/wp-content/uploads/2014/12/desarrollador-android-680x394.jpg"))
                        .build();

                shareDialog.show(linkContent);
            }
            Toast.makeText(this, "Se publicó imagen en Facebook",Toast.LENGTH_LONG ).show();
        }
        catch (Exception e) {
            Toast.makeText(this, "Error al compartir en Facebook",Toast.LENGTH_LONG ).show();
        }

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}

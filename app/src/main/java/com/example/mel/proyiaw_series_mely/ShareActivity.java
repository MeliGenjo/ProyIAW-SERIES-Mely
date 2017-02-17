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

public class ShareActivity extends AppCompatActivity {

    private Button shareFace, shareWhatsapp, share, btnFacebook;
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

        btnFacebook = (Button) findViewById(R.id.btnShareFace);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap imagen = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher);
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(imagen)
                        .build();

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://www.elandroidelibre.com/wp-content/uploads/2014/12/desarrollador-android-680x394.jpg"))
                        .setQuote("Connect on a global scale.")
                        .build();

               /* SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();*/

                ShareDialog.show(ShareActivity.this, content);
            }
        });


        /// botones extras
        shareWhatsapp= (Button) findViewById(R.id.btnWhatsapp);
        shareFace= (Button) findViewById(R.id.btnFacebook);
        share = (Button) findViewById(R.id.btnShare);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "El mejor blog de android http://javaheros.blogspot.pe/");
                startActivity(Intent.createChooser(intent, "Share with"));
            }
        });

        shareWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "El mejor blog de android http://javaheros.blogspot.pe/");
                intent.setPackage("com.whatsapp");
                startActivity(intent);
            }
        });

        shareFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "El mejor blog de android http://javaheros.blogspot.pe/");
                intent.setPackage("com.facebook.katana");
                startActivity(intent);
            }
        });
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

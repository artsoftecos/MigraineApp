package co.artsoft.migraineapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import co.artsoft.migraineapp.cipher.RSACipher;
import co.artsoft.migraineapp.fragments.HistoryEpisode;
import co.artsoft.migraineapp.fragments.Home;

public class MainActivity extends AppCompatActivity {

    private static Home home;
    private static HistoryEpisode historyEpisode;

    private static DrawerLayout mDrawerLayout;
    private static NavigationView mNavigationView;
    private static FragmentManager mFragmentManager;
    private static FragmentTransaction mFragmentTransaction;
    private static View decorView;
    private static android.support.v7.widget.Toolbar toolbar;

    private RSACipher cipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            cipher = new RSACipher();

            String msg = "mensaje para cifrar la primera vez";

            String cifrado = cipher.encrypt(msg);
            String decifrado = cipher.decrypt(cifrado);


            //Log.d( "I", msg );
            //Log.d( "I", cifrado );
            //Log.d( "I", decifrado );
            //Log.d("I", cipher.getPublicKey("pkcs8-pem"));
            //Log.d("I", cipher.stringToPublicKey(cipher.getPublicKey("pkcs8-pem")).toString());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        home = new Home();
        historyEpisode = new HistoryEpisode();

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Migraine");

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        /**
         * Setup the bars behavior
         */
        setBarsVisibility();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;


        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, home).commit();


        /**
         * Setup click events on the Navigation View Items.
         */
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();
                Context context = getApplicationContext();
                String text = "Hello toast!";
                int icon = R.drawable.sent;

                if (menuItem.getItemId() == R.id.nav_item_home) {
                    toolbar.setTitle("Home");
                    setBarsVisibility();
                    FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                    xfragmentTransaction.replace(R.id.containerView, home).commit();
                    text = "Home";
                    icon = R.drawable.sent;
                }
                if (menuItem.getItemId() == R.id.nav_item_personas) {
                    toolbar.setTitle("History of Episodes");
                    setBarsVisibility();
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, historyEpisode).commit();
                    text = "Historial of Episodes";
                    icon = R.drawable.inbox;
                }
                if (menuItem.getItemId() == R.id.nav_item_salir) {
                    text = "Good Bye";
                    icon = R.drawable.pokemon;
                    customToast(text, icon );
                    finish();
                }

                customToast( text, icon );

                return false;
            }

        });


        /**
         * Setup Drawer Toggle of the Toolbar
         */
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,R.string.app_name);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setBarsVisibility();
    }

    public void customToast(String text, int icon) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView textToast1 = (TextView) layout.findViewById(R.id.textView4);
        textToast1.setText(text);
        ImageView imageView1 = (ImageView) layout.findViewById(R.id.imageView);
        imageView1.setImageResource(icon);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, Gravity.CENTER_VERTICAL);
        toast.setMargin(0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    protected void setBarsVisibility(){
        decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.

                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                        }
                    }
                });
    }
}

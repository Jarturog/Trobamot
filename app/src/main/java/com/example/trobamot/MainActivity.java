package com.example.trobamot;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * On es comença a executar el projecte.
 * @author Juan Arturo Abaurrea Calafell i Marta González Juan
 */
public class MainActivity extends AppCompatActivity {

    // missatges per a la pantalla final
    public static final String MESSAGE_PARAULA = "com.example.trobamot.MainActivity.PARAULA";
    public static final String MESSAGE_GUANYAT = "com.example.trobamot.MainActivity.GUANYAT";
    public static final String MESSAGE_RESTRICCIONS = "com.example.trobamot.MainActivity.RESTRICCIONS";
    public static final String MESSAGE_POSSIBILITATS = "com.example.trobamot.MainActivity.POSSIBILITATS";
    // missatges per a la pantalla principal
    public static final String MESSAGE_LENGTHWORD = "com.example.trobamot.MainActivity.LENGTHWORD";
    public static final String MESSAGE_MAXTRY = "com.example.trobamot.MainActivity.MAXTRY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Mostra un popup amb el text introduit
     * @param context context on es vol mostrar
     * @param text text que es vol mostrar
     */
    public static void missatgeError(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //hideSystemUI(this);
        Intent intent = new Intent(this, Menu.class); // es crea l'intent
        startActivity(intent);
    }

    public static void hideSystemUI(AppCompatActivity app, boolean barraInferior, boolean barraSuperior) {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        //app.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = app.getWindow().getDecorView();
        int visibility = View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // no posar amb notch
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                ;
        // Hide the nav bar and status bar
        if (!barraInferior) {
            visibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            //app.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (!barraSuperior) visibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;

        decorView.setSystemUiVisibility(visibility);
    }

    public static int getStatusBarHeight(AppCompatActivity app) {
        int height = 0;
        int resourceId = app.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = app.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
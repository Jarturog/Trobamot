package com.example.trobamot;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * On es comença a executar el projecte.
 * @author Juan Arturo Abaurrea Calafell i Marta González Juan
 */
public class MainActivity extends AppCompatActivity {

    // missatges
    public static final String MESSAGE_PARAULA = "com.example.trobamot.MainActivity.PARAULA";
    public static final String MESSAGE_GUANYAT = "com.example.trobamot.MainActivity.GUANYAT";
    public static final String MESSAGE_RESTRICCIONS = "com.example.trobamot.MainActivity.RESTRICCIONS";
    public static final String MESSAGE_POSSIBILITATS = "com.example.trobamot.MainActivity.POSSIBILITATS";

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
        hideSystemUI();
        new PantallaPrincipal(this);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE  // no posar amb notch
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
package com.example.trobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.icu.text.DateTimePatternGenerator;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

public class PantallaFinal extends AppCompatActivity {

    private final static String TITOL_PERDRE = "Oh oh oh oh...", TITOL_GUANYAR = "Enhorabona!";
    private String paraulaCorrecta;
    private boolean guanyat;
    private int widthDisplay, heightDisplay;
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);

        constraintLayout = findViewById(R.id.layoutFinal);
        // Object to store display information
        DisplayMetrics metrics = new DisplayMetrics();
        // Get display information
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        Intent intent = getIntent();
        paraulaCorrecta = intent.getStringExtra(MainActivity.MESSAGE_PARAULA);
        guanyat = intent.getBooleanExtra(MainActivity.MESSAGE_GUANYAT, false);
        String restriccions = intent.getStringExtra(MainActivity.MESSAGE_RESTRICCIONS);
        String paraulesPossibles = intent.getStringExtra(MainActivity.MESSAGE_POSSIBILITATS);
        crearTitol();
        crearTextDefinicio();
        if (!guanyat) {
            crearTextRestriccions();
            crearTextParaulesPossibles();
        }
    }

    private void crearTitol() {
        String sTitol;
        if (guanyat) {
            sTitol = TITOL_GUANYAR;
        } else {
            sTitol = TITOL_PERDRE;
        }
        // Cream el textview i el definim
        TextView titol = new TextView(this);
        TextView paraula = new TextView(this);
        titol.setText(sTitol);
        paraula.setText(paraulaCorrecta);
        int tamanyTextTitol = widthDisplay/25;
        int tamanyTextParaula = widthDisplay/400;
        titol.setTextSize(tamanyTextTitol);
        paraula.setTextSize(tamanyTextParaula);
        // Posicionam el textview
        titol.setX(widthDisplay/4);
        titol.setY(heightDisplay/16);
        paraula.setX(10);
        paraula.setY(widthDisplay/10);
        constraintLayout.addView(titol);
        constraintLayout.addView(paraula);
    }

    private void crearTextDefinicio() {
        // Aconseguim la definició
        InternetDefinicioParaula internet = new InternetDefinicioParaula(paraulaCorrecta, this);
        String definicio = internet.getDefinicio();
        // Cream el textview i el definim
        TextView textView = new TextView(this);
        textView.setText(Html.fromHtml(definicio));
        int tamanyText = widthDisplay/70;
        textView.setTextSize(tamanyText);
        ScrollView
        textView.setScroller(scroll);
        // Posicionam el textview
        int x = 20;
        int y = widthDisplay/2;
        textView.setX(x);
        textView.setY(y);
        textView.setWidth(widthDisplay -2*x);
        textView.setHeight(heightDisplay-y- 2*x);
        constraintLayout.addView(textView);
    }

    private void crearTextRestriccions() {

    }


    private void crearTextParaulesPossibles() {

    }
}
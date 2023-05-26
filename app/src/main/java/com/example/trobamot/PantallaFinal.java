package com.example.trobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class PantallaFinal extends AppCompatActivity {

    private final static String TITOL_PERDRE = "Oh oh oh oh...", TITOL_GUANYAR = "Enhorabona!";
    private String paraulaCorrecta;
    private boolean guanyat;
    private int widthDisplay, heightDisplay;
    private ConstraintLayout constraintLayout;
    private Intent intent;
    private static final int MARGE = 20;
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

        intent = getIntent();
        paraulaCorrecta = intent.getStringExtra(MainActivity.MESSAGE_PARAULA);
        guanyat = intent.getBooleanExtra(MainActivity.MESSAGE_GUANYAT, false);
        crearTitol();
        crearTextDefinicio();
        if (!guanyat) {
            // poner fondo gris
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
        int tamanyTextTitol = widthDisplay/24;
        int tamanyTextParaula = widthDisplay/36;
        titol.setTextSize(tamanyTextTitol);
        paraula.setTextSize(tamanyTextParaula);
        // Posicionam el textview
        titol.setX((widthDisplay/2)-tamanyTextTitol*titol.getText().length()/2);
        titol.setY(heightDisplay/20);
        paraula.setX((widthDisplay/2)-tamanyTextParaula*paraula.getText().length()/2);
        paraula.setY(heightDisplay/7);
        titol.setTypeface(Typeface.DEFAULT_BOLD);
        constraintLayout.addView(titol);
        constraintLayout.addView(paraula);
    }

    private void crearTextDefinicio() {
        // Aconseguim la definició
        InternetDefinicioParaula internet = new InternetDefinicioParaula(paraulaCorrecta);
        String definicio = internet.getDefinicio();
        // Cream el textview i el definim
        TextView textView = new TextView(this);
        textView.setText(Html.fromHtml(definicio));
        int tamanyText = widthDisplay/70;
        textView.setTextSize(tamanyText);
        textView.setMovementMethod(new ScrollingMovementMethod());
        // Posicionam el textview
        int x = MARGE;
        int y = heightDisplay/5;
        textView.setX(x);
        textView.setY(y);
        textView.setWidth(widthDisplay - MARGE * 2);
        textView.setHeight(heightDisplay * 3/4);
        constraintLayout.addView(textView);
    }

    private void crearTextRestriccions() {
        String restriccions = intent.getStringExtra(MainActivity.MESSAGE_RESTRICCIONS);
        String textRestriccions = "Restriccions: ";
        SpannableString spannableString = new SpannableString(textRestriccions + restriccions);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, textRestriccions.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(this);
        textView.setText(spannableString);
        int tamanyText = widthDisplay/70;
        textView.setTextSize(tamanyText);
        textView.setMovementMethod(new ScrollingMovementMethod());
        // Posicionam el textview
        int x = MARGE;
        int y = heightDisplay/2;
        textView.setX(x);
        textView.setY(y);
        textView.setWidth(widthDisplay - MARGE * 2);
        textView.setHeight(heightDisplay * 3/4);
        constraintLayout.addView(textView);
    }


    private void crearTextParaulesPossibles() {
        String paraulesPossibles = intent.getStringExtra(MainActivity.MESSAGE_POSSIBILITATS);
        String partDestacada = "Paraules possibles: ";
        TextView textView = new TextView(this);
        SpannableString spannableString = new SpannableString(partDestacada + paraulesPossibles);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, partDestacada.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        int tamanyText = widthDisplay/70;
        textView.setTextSize(tamanyText);
        textView.setMovementMethod(new ScrollingMovementMethod());
        // Posicionam el textview
        int x = MARGE;
        int y = heightDisplay*5/8;
        textView.setX(x);
        textView.setY(y);
        textView.setWidth(widthDisplay - MARGE * 2);
        textView.setHeight(heightDisplay * 3/4);
        constraintLayout.addView(textView);
    }
}
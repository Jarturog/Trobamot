package com.example.trobamot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * Pantalla final resultant de perdre o guanyar.
 * @author Juan Arturo Abaurrea Calafell i Marta González Juan
 */
public class PantallaFinal extends AppCompatActivity {

    // missatges de perdre/guanyar
    private final static String TITOL_PERDRE = "Oh oh oh oh...", TITOL_GUANYAR = "Enhorabona!";
    private String paraulaCorrecta; // paraula amb accents
    private boolean guanyat; // true si ha guanyat, false si ha perdut
    private int widthDisplay, heightDisplay; // dimensions del dispositiu
    private ConstraintLayout constraintLayout; // constraintLayout on s'afegiran els TextView's
    private Intent intent; // l'Intent amb el qual s'ha arribat a aquesta Activity
    private static final int MARGE_HOR = 20; // el marge entre el text i les parets del dispositiu
    private static final int MARGE_VER = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_final);
        MainActivity.hideSystemUI(this, true, true);
        constraintLayout = findViewById(R.id.layoutFinal);
        // Object to store display information
        DisplayMetrics metrics = new DisplayMetrics();
        // Get display information
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;
        // s'agafa l'intent, la paraula i si ha guanyat o no
        intent = getIntent();
        paraulaCorrecta = intent.getStringExtra(MainActivity.MESSAGE_PARAULA);
        guanyat = intent.getBooleanExtra(MainActivity.MESSAGE_GUANYAT, false);
        crearTitol();
        crearTextDefinicio();
        if (!guanyat) {
            // S'estableix el fons gris i s'informa de les restriccions i les paraules possibles
            //constraintLayout.setBackgroundColor(Color.parseColor("#DAE1E7"));
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            crearTextRestriccions();
            crearTextParaulesPossibles();
        }
    }

    /**
     * Crea la part superior de la pantalla final
     */
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
        titol.setY(2*MARGE_VER+heightDisplay/20);
        paraula.setX((widthDisplay/2)-tamanyTextParaula*paraula.getText().length()/2);
        paraula.setY(2*MARGE_VER+heightDisplay/7);
        titol.setTypeface(Typeface.DEFAULT_BOLD); // en negreta
        constraintLayout.addView(titol);
        constraintLayout.addView(paraula);
    }

    /**
     * Crea el TextView corresponent a la definició de la paraula.
     */
    private void crearTextDefinicio() {
        // Aconseguim la definició
        InternetDefinicioParaula internet = new InternetDefinicioParaula(paraulaCorrecta);
        String definicio = internet.getDefinicio();
        // Cream el textview i el definim
        TextView textView = new TextView(this);
        textView.setText(Html.fromHtml(definicio));
        int tamanyText = widthDisplay/70;
        textView.setTextSize(tamanyText);
        textView.setMovementMethod(new ScrollingMovementMethod()); // scroll
        // Posicionam el textview
        int x = MARGE_HOR;
        int y = MARGE_VER+heightDisplay/3;
        textView.setX(x);
        textView.setY(y);
        textView.setWidth(widthDisplay - MARGE_HOR * 2);
        textView.setHeight(heightDisplay * 2/9 - MARGE_HOR * 2);
        constraintLayout.addView(textView);
    }

    /**
     * Crea el TextView corresponent a les restriccions de la paraula.
     */
    private void crearTextRestriccions() {
        String restriccions = intent.getStringExtra(MainActivity.MESSAGE_RESTRICCIONS);
        String textRestriccions = "Restriccions: ";
        SpannableString spannableString = new SpannableString(textRestriccions + restriccions);
        // es posa en negreta la primera paraula
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, textRestriccions.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView textView = new TextView(this);
        textView.setText(spannableString);
        int tamanyText = widthDisplay/70;
        textView.setTextSize(tamanyText);
        textView.setMovementMethod(new ScrollingMovementMethod()); // scroll
        // Posicionam el textview
        int x = MARGE_HOR;
        int y = MARGE_VER+heightDisplay*5/9;
        textView.setX(x);
        textView.setY(y);
        textView.setWidth(widthDisplay - MARGE_HOR * 2);
        textView.setHeight(heightDisplay * 2/9 - MARGE_HOR * 2);
        constraintLayout.addView(textView);
    }

    /**
     * Crea el TextView corresponent a les paraules possibles que depenen de les restriccions.
     */
    private void crearTextParaulesPossibles() {
        String paraulesPossibles = intent.getStringExtra(MainActivity.MESSAGE_POSSIBILITATS);
        String partDestacada = "Paraules possibles: ";
        TextView textView = new TextView(this);
        SpannableString spannableString = new SpannableString(partDestacada + paraulesPossibles);
        // es posa en negreta la primera paraula
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, partDestacada.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        int tamanyText = widthDisplay/70;
        textView.setTextSize(tamanyText);
        textView.setMovementMethod(new ScrollingMovementMethod()); // scroll
        // Posicionam el textview
        int x = MARGE_HOR;
        int y = MARGE_VER+heightDisplay*7/9;
        textView.setX(x);
        textView.setY(y);
        textView.setWidth(widthDisplay - MARGE_HOR * 2);
        textView.setHeight(heightDisplay * 2/9 - heightDisplay/10);
        constraintLayout.addView(textView);
    }
}
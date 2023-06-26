package com.example.trobamot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Arrays;

public class Menu extends AppCompatActivity{

    class Opcion {

        public final String idioma, enviarText, esborrarText, hihaText, solText, link;
        public final int diccionari;

        public Opcion(String idioma, String enviarText, String esborrarText, int diccionari, String hihaText, String solText, String link){
            this.idioma = idioma;
            this.enviarText = enviarText;
            this.esborrarText = esborrarText;
            this.diccionari = diccionari;
            this.hihaText = hihaText;
            this.solText = solText;
            this.link = link;
        }
    }

    private static Opcion o; // final
    private Opcion cat, esp, eng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final EditText lengthText = new EditText(this), triesText = new EditText(this);
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics(); // Object to store display information
        getWindowManager().getDefaultDisplay().getMetrics(metrics); // Get display information
        int widthDisplay = metrics.widthPixels; // se assignen els valors per emprar-los després
        int heightDisplay = metrics.heightPixels;
        setContentView(R.layout.activity_menu);
        ConstraintLayout constraintLayout = findViewById(R.id.layoutMenu);
        constraintLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        MainActivity.hideSystemUI(this, true, false);

        cat = new Opcion("Català", "Enviar", "Esborrar", R.raw.paraules,
                "Hi ha", "solucions possibles", "https://www.vilaweb.cat/paraulogic/?diec=");
        esp = new Opcion("Español", "Enviar", "Borrar", R.raw.palabras,
                "Hay", "soluciones posibles", "https://www.wordreference.com/definicion/");
        eng = new Opcion("English", "Send", "Remove", R.raw.words,
                "There are", "possible solutions", "https://www.wordreference.com/definition/");
        Button bCat = new Button(this), bEsp = new Button(this), bEng = new Button(this);
        bCat.setText(cat.idioma);
        bEsp.setText(esp.idioma);
        bEng.setText(eng.idioma);
        // Tamany dels botons
        int buttonWidth = widthDisplay * 3 / 8, buttonHeight = heightDisplay / 10;
        bCat.setY(heightDisplay * 2 / 5 - buttonHeight * 2);
        bEsp.setY(heightDisplay * 3 / 5 - buttonHeight * 2);
        bEng.setY(heightDisplay * 4 / 5 - buttonHeight * 2);
        for (Button botonOpcion : Arrays.asList(bCat, bEsp, bEng)) {
            ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            p.height = buttonHeight;
            p.width = buttonWidth;
            botonOpcion.setLayoutParams(p);
            // Posicionar els botons
            botonOpcion.setX(widthDisplay / 2 - buttonWidth / 2);
            // Afegir el botó al layout
            constraintLayout.addView(botonOpcion);
            // Afegir la funcionalitat al botó
            botonOpcion.setOnClickListener(v -> {
                int lengthword = -1, maxTry = -1;
                try {
                    lengthword = Integer.parseInt(lengthText.getText().toString());
                    maxTry = Integer.parseInt(triesText.getText().toString());
                }catch (NumberFormatException e){
                    MainActivity.missatgeError(this, "S'han posat els valors predeterminats");
                    lengthword = PantallaPrincipal.DEFAULT_LENGTHWORD;
                    maxTry = PantallaPrincipal.DEFAULT_MAXTRY;
                }
                Button b = (Button) v;
                if (b.getText().equals(cat.idioma)) {
                    o = cat;
                } else if (b.getText().equals(esp.idioma)) {
                    o = esp;
                } else if (b.getText().equals(eng.idioma)) {
                    o = eng;
                }
                Intent intent = new Intent(this, PantallaPrincipal.class); // es crea l'intent
                intent.putExtra(MainActivity.MESSAGE_LENGTHWORD, lengthword);
                intent.putExtra(MainActivity.MESSAGE_MAXTRY, maxTry);
                startActivity(intent);
            });
        }
        // lengthword
        lengthText.setInputType(InputType.TYPE_CLASS_NUMBER);
        lengthText.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(2)});
        lengthText.setTextColor(Color.BLACK);
        lengthText.setHint("Llargària paraula");
        lengthText.setHintTextColor(Color.parseColor("#D9E1E8"));
        ConstraintLayout.LayoutParams p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        p.height = buttonHeight/2;
        p.width = buttonWidth;
        lengthText.setLayoutParams(p);
        // Posicionar els botons
        lengthText.setX(widthDisplay / 2 - buttonWidth / 2);
        lengthText.setY(buttonHeight/4 + (bCat.getY() + bEsp.getY())/2);
        // Afegir el botó al layout
        constraintLayout.addView(lengthText);
        // maxTry
        triesText.setInputType(InputType.TYPE_CLASS_NUMBER);
        triesText.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(1)});
        triesText.setTextColor(Color.BLACK);
        triesText.setHint("Intents màxims");
        triesText.setHintTextColor(Color.parseColor("#D9E1E8"));
        p = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        p.height = buttonHeight/2;
        p.width = buttonWidth;
        triesText.setLayoutParams(p);
        // Posicionar els botons
        triesText.setX(widthDisplay / 2 - buttonWidth / 2);
        triesText.setY(buttonHeight/4 + (bEsp.getY() + bEng.getY())/2);
        // Afegir el botó al layout
        constraintLayout.addView(triesText);
    }

    public static Opcion getOpcion(){
        return o;
    }
}



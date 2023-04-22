package com.example.trobamot;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class PantallaPrincipal {

    // instància de l'execució
    private AppCompatActivity context;

    private int widthDisplay, heightDisplay, intentActual, lletraActual;

    // Variables de lògica del joc
    private static final int lengthWord = 5, maxTry = 6;

    private final static String grayColor = "#D9E1E8", ALFABET = "ABCÇDEFGHIJKLMNOPQRSTUVWXYZ";

    public PantallaPrincipal(AppCompatActivity context){
        // Object to store display information
        DisplayMetrics metrics = new DisplayMetrics();
        // Get display information
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;
        // Get context/instance
        this.context = context;
        lletraActual = 0;
        intentActual = 0;
        crearGraella();
        crearTeclat();
        //crearParaula(0);
    }

    private void crearGraella() {
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        // Definir les característiques del "pinzell"
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(grayColor));
        // crear totes les caselles
        for (int i = 0; i < maxTry; i++) {
            for (int j = 0; j < lengthWord; j++) {
                // Crear el TextView i afegir-lo al layout
                // + 2 pels espais als bordes i - 2 pels 2+2 bytes extra entre casella i casella
                constraintLayout.addView(new Casella(context, i, j, gd, widthDisplay/(lengthWord+2)));
            }
        }

    }

    private void crearTeclat() {
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        int files = 3;
        int offsetLletra = 8;
        int espaiOcupatPerLletra = widthDisplay/(ALFABET.length()/files);
        int tamanyLletra = espaiOcupatPerLletra - 2 * offsetLletra;
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < ALFABET.length()/files; j++) {
                char lletra = ALFABET.charAt(i * (ALFABET.length()/files) + j);
                Button button = new Button(context);
                button.setText(lletra + "");
                // Tamany dels botons
                ConstraintLayout.LayoutParams paramsBoto = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                paramsBoto.height = tamanyLletra;
                paramsBoto.width = tamanyLletra;
                button.setLayoutParams(paramsBoto);
                // Posicionar els botons
                button.setX(widthDisplay - ((ALFABET.length()/files)-j) * espaiOcupatPerLletra);
                button.setY(heightDisplay - (files-i) * espaiOcupatPerLletra);
                // Afegir el botó al layout
                constraintLayout.addView(button);
                // Afegir la funcionalitat al botó
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        escriureLletra(lletra);
                    }
                });
            }
        }
        // Crear els botons
        Button buttonEsborrar = new Button(context);
        Button buttonEnviar = new Button(context);
        buttonEsborrar.setText("Esborrar");
        buttonEnviar.setText("Enviar");
        // Tamany dels botons
        int buttonWidth = 400, buttonHeight = 200;
        ConstraintLayout.LayoutParams paramsBoto = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        paramsBoto.height = buttonHeight;
        paramsBoto.width = buttonWidth;
        buttonEsborrar.setLayoutParams(paramsBoto);
        buttonEnviar.setLayoutParams(paramsBoto);
        // Posicionar els botons
        int offset = 16;
        buttonEnviar.setX(widthDisplay/2 + offset);
        buttonEnviar.setY(heightDisplay - files * espaiOcupatPerLletra - 2*offset - buttonHeight);
        buttonEsborrar.setX(widthDisplay/2 - (offset + buttonWidth));
        buttonEsborrar.setY(heightDisplay - files * espaiOcupatPerLletra - 2*offset - buttonHeight);
        // Afegir el botó al layout
        constraintLayout.addView(buttonEsborrar);
        constraintLayout.addView(buttonEnviar);
        // Afegir la funcionalitat al botó
        buttonEsborrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                llevarLletra();
            }
        });
        buttonEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                comprobarParaula();
            }
        });
    }

    private void llevarLletra(){
        if (lletraActual <= 0){
            return;
        }
        lletraActual--;
        Casella casella = Casella.getCasella(context, intentActual, lletraActual);
        casella.setText("");
    }

    private void escriureLletra(char c){
        if (lletraActual >= lengthWord){
            return;
        }
        Casella casella = Casella.getCasella(context, intentActual, lletraActual);
        casella.setText(c + "");
        lletraActual++;
    }

    private void comprobarParaula(){
        String s = "";
        for (int i = 0; i < lengthWord; i++) {
            s += Casella.getCasella(context, intentActual, i);
        }
        System.out.println(s);
    }

    private void crearParaula(int fila){
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        UnsortedArrayMapping<Casella, Character> paraula = new UnsortedArrayMapping<>(lengthWord);
        String s = getParaula(lengthWord);
        for (int i = 0; i < s.length(); i++) {
            paraula.put(Casella.getCasella(context, fila, i), s.charAt(i));
        }

    }

    private String getParaula(int longitudParaula){
        return "aigua";
    }

    public static int getLongitudParaula() {
        return lengthWord;
    }
}

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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class PantallaPrincipal {

    // instància de l'execució
    private AppCompatActivity context;

    private int widthDisplay, heightDisplay, intentActual, lletraActual;

    // Variables de lògica del joc
    private static final int lengthWord = 5, maxTry = 6;
    private final int NUM_PARAULES;

    private final static String grayColor = "#D9E1E8", ALFABET = "ABCÇDEFGHIJKLMNOPQRSTUVWXYZ";
    private String paraulaRaw;
    public PantallaPrincipal(AppCompatActivity context, Diccionari dic){
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
        paraulaRaw = crearParaula(new Random().nextInt());
        crearGraella();
        crearTeclat();
        NUM_PARAULES = dic.getNumParaules();
        
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
        boolean lletresOcupades[] = new boolean[paraulaRaw.length()/2];
        UnsortedArrayMapping<Character, Integer> uam = new UnsortedArrayMapping<>(ALFABET.length());
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < ALFABET.length()/files; j++) {
                char lletra = ALFABET.charAt(i * (ALFABET.length()/files) + j);
                boolean lletraEnParaula = false;
                for (int pos = 0; pos < paraulaRaw.length()/2; pos++) {
                    if (!lletresOcupades[pos] && Character.toLowerCase(paraulaRaw.charAt(pos)) == Character.toLowerCase(lletra)){
                        lletresOcupades[pos] = true;
                        lletraEnParaula = true;
                        uam.put(lletra, pos);
                        break;
                    }
                }
                if (!lletraEnParaula){
                    uam.put(lletra, null);
                }
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
                button.setOnClickListener(v -> escriureLletra(lletra));
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
        buttonEsborrar.setOnClickListener(v -> llevarLletra());
        buttonEnviar.setOnClickListener(v -> comprobarParaula());
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
        lletraActual = 0;
        intentActual++;
        String s = "";
        for (int i = 0; i < lengthWord; i++) {
            s += Casella.getCasella(context, intentActual, i);
        }
        if ((s.toLowerCase()).compareTo((paraulaRaw.substring(0,paraulaRaw.length()/2)).toLowerCase()) == 0){
            System.out.println("ganaste");
        }else if(intentActual >= maxTry){
            System.out.println("perdiste: "+paraulaRaw.substring(0,paraulaRaw.length()/2));
        }
    }

    private String crearParaula(int seed){
        /*
        Random r = new Random(seed);
        String paraulaRaw = null;
        try {
            while (true) {
                int numParaula = r.nextInt(NUM_PARAULES);
                for (int i = 0; i < numParaula; i++) {
                    br.readLine();
                }
                paraulaRaw = br.readLine();
                while (paraulaRaw != null && paraulaRaw.charAt(lengthWord) != ';') { // això vol dir que no és de la grandària
                    paraulaRaw = br.readLine();
                }
                br.reset();
                if (paraulaRaw != null) {
                    break;
                }
            }
        }catch (IOException e){
            System.err.println("Error llegint el diccionari: "+e);
        }

        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        UnsortedArrayMapping<Casella, Character> paraulaRaw = new UnsortedArrayMapping<>(lengthWord);
        String s = getParaula(lengthWord);
        for (int i = 0; i < s.length(); i++) {
            paraulaRaw.put(Casella.getCasella(context, 0, i), s.charAt(i));
        }
        return paraulaRaw;

         */
        return "canya;canya";
    }

    public static int getLongitudParaula() {
        return lengthWord;
    }
}

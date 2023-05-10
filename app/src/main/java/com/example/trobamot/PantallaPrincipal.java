package com.example.trobamot;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    /**
     * Mapping de posicions de paraula relacionades amb la lletra
     */
    private UnsortedArrayMapping<Integer, Character> uam;
    private int widthDisplay, heightDisplay, intentActual, lletraActual, solucions;
    private TextView textViewInformatiu;
    // Variables de lògica del joc
    private static final int lengthWord = 5, maxTry = 6;
    private final int NUM_PARAULES;

    private final static String grayColor = "#D9E1E8", ALFABET = "ABCÇDEFGHIJKLMNOPQRSTUVWXYZ";
    private String paraula, paraulaBenEscsrita;
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
        String paraulaRaw = crearParaula(new Random().nextInt());
        paraula = paraulaRaw.substring(0, paraulaRaw.length()/2);
        paraulaBenEscsrita = paraulaRaw.substring((paraulaRaw.length()/2)+1);
        crearGraella();
        crearTextInformatiu();
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

    private void crearTextInformatiu(){
        textViewInformatiu = new TextView(context);
        // Definir el textview
        textViewInformatiu.setText("Hi ha "+solucions+" solucions posibles.");
        int tamanyText = widthDisplay/50;
        textViewInformatiu.setTextSize(tamanyText);
        // Posicionam el textview
        textViewInformatiu.setX((widthDisplay/2)-tamanyText*textViewInformatiu.getText().length()/2);
        textViewInformatiu.setY(((1+maxTry)*widthDisplay/(lengthWord+2))+(widthDisplay/(2*(lengthWord+2))));
        ((ConstraintLayout)context.findViewById(R.id.layout)).addView(textViewInformatiu);
    }

    private void crearTeclat() {
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        int files = 3, offsetLletra = 0,
                espaiOcupatPerLletra = widthDisplay/(ALFABET.length()/files),
                tamanyLletra = espaiOcupatPerLletra - 2 * offsetLletra;
        uam = new UnsortedArrayMapping<>(lengthWord);
        for (int pos = 0; pos < lengthWord; pos++) {
            uam.put(pos, paraula.charAt(pos));
        }
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
        ConstraintLayout.LayoutParams paramsBotoEnviar = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        paramsBotoEnviar.height = buttonHeight;
        paramsBotoEnviar.width = buttonWidth;
        buttonEnviar.setLayoutParams(paramsBotoEnviar);
        ConstraintLayout.LayoutParams paramsBotoEsborrar = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        paramsBotoEsborrar.height = buttonHeight;
        paramsBotoEsborrar.width = buttonWidth;
        buttonEsborrar.setLayoutParams(paramsBotoEsborrar);
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
        //casella.
        lletraActual++;
    }

    private void actualitzarPunter(){
        Casella c = Casella.getCasella(context, intentActual, lletraActual);
        //c.
    }

    private void comprobarParaula(){
        String paraulaEscrita = "";
        for (int i = 0; i < lengthWord; i++) {
            paraulaEscrita += Casella.getCasella(context, intentActual, i);
        }
        if (lletraActual < lengthWord){ // paraula incompleta
            missatgeError("Paraula incompleta!");
            return;
        } else if (paraulaEscrita.compareToIgnoreCase(paraula) == 0){ // ha guanyat
            new PantallaFinal(context, true, paraulaBenEscsrita);
            return;
        } else if (!existeixParaula(paraulaEscrita)){ // es comprova si forma part del catàleg
            missatgeError("Paraula no vàlida!");
            return;
        }
        if(intentActual+1 >= maxTry){ // si no té més oportunitats ha perdut
            new PantallaFinal(context, false, paraulaBenEscsrita);
            return;
        }
        for (int i = 0; i < paraulaEscrita.length(); i++) { // recòrrer la paraula escrita
            boolean coloretjat = false;
            char lletraParaula = Character.toLowerCase(paraulaEscrita.charAt(i));
            Casella c = Casella.getCasella(context, intentActual, i);
            if (Character.toLowerCase(uam.get(i)) == lletraParaula){
                c.setBackgroundColor(Color.GREEN);
                coloretjat = true;
            }else{
                for (int pos = 0; pos < lengthWord; pos++) { // recòrrer uam
                    if (Character.toLowerCase(uam.get(pos)) == lletraParaula){
                        Casella groga = Casella.getCasella(context, intentActual, pos);
                        groga.setBackgroundColor(Color.YELLOW);
                        coloretjat = true;
                        break;
                    }
                }
            }
            if (!coloretjat){
                c.setBackgroundColor(Color.RED);
            }
        }
        lletraActual = 0; // torna a començar a escriure per la següent fila
        intentActual++; // si la paraula ha estat vàlida es té en compte aquest intent
    }

    private boolean existeixParaula(String s){
        return true;
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
        return "pasta;pasta";
    }

    private void missatgeError(String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static int getLongitudParaula() {
        return lengthWord;
    }
}

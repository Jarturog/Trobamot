package com.example.trobamot;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class PantallaPrincipal {

    // instància de l'execució
    private AppCompatActivity context;
    /**
     * Mapping de posicions de paraula relacionades amb la lletra
     */
    private UnsortedArrayMapping<Integer, Character> paraulaAmbPosicionsLletres;
    private HashMap<String, String> diccionariComplet;
    private HashSet<String> diccionariSolucions;
    private int intentActual, lletraActual, solucions;
    private final int widthDisplay, heightDisplay;
    private TextView textViewInformatiu;
    // Variables de lògica del joc
    private static int lengthWord = 5, maxTry = 1;
    private static final int COLOR_DEFAULT_TECLA = Color.BLACK;
    private UnsortedArrayMapping<Integer, Character> pistesAcertades;
    private UnsortedArraySet<Character> pistesQuasiAcertades, restriccions;
    private final static String grayColor = "#D9E1E8", orangeColor = "#E69138", redColor = "#CC0000",
             greenColor = "#38761D", ALFABET = "ABCÇDEFGHIJKLMNOPQRSTUVWXYZ";
    private String paraula, paraulaBenEscrita;
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
        int numParaules = inicialitzarDiccionari();
        crearParaula(numParaules);
        while (Casella.getBEGIN_IDs_CASELLAS() + maxTry * lengthWord >= ALFABET.charAt(0)) {
            MainActivity.missatgeError(context, maxTry + " son massa intents.");
            maxTry--;
        }
        crearTeclat();
        crearTextInformatiu();
        crearGraella();
    }

    private int inicialitzarDiccionari() {
        try {
            diccionariComplet = new HashMap<>();
            diccionariSolucions = new HashSet<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.paraules)));
            String linia = br.readLine();
            while (linia != null && linia != "") {
                String paraulaAmbAccents = "";
                char lletra = linia.charAt(0);
                for (int i = 0; lletra != ';' && i < lengthWord; i++) {
                    paraulaAmbAccents += lletra;
                    linia = linia.substring(1);
                    lletra = linia.charAt(0);
                }
                if (lletra == ';' && paraulaAmbAccents.length() == lengthWord) {
                    String paraulaSenseAccents = linia.substring(1);
                    diccionariComplet.put(paraulaSenseAccents, paraulaAmbAccents);
                    diccionariSolucions.add(paraulaSenseAccents);
                    solucions++;
                }
                linia = br.readLine();
            }
            return solucions;
        } catch (IOException e) {
            MainActivity.missatgeError(context, "No s'ha pogut inicialitzar el diccionari");
            throw new RuntimeException("No s'ha pogut inicialitzar el diccionari");
        }
    }

    private void crearGraella() {
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        // Definir les característiques del "pinzell"
        GradientDrawable gd = getPinzell(false);
        // crear totes les caselles
        for (int i = 0; i < maxTry; i++) {
            for (int j = 0; j < lengthWord; j++) {
                // Crear el TextView i afegir-lo al layout
                // + 2 pels espais als bordes i - 2 pels 2+2 bytes extra entre casella i casella
                constraintLayout.addView(new Casella(context, i, j, gd, widthDisplay/(lengthWord+2)));
            }
        }
        Casella c = Casella.getCasella(context, 0, 0);
        c.setBackground(getPinzell(true));
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
        paraulaAmbPosicionsLletres = new UnsortedArrayMapping<>(lengthWord);
        for (int pos = 0; pos < lengthWord; pos++) {
            paraulaAmbPosicionsLletres.put(pos, paraula.charAt(pos));
        }
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < ALFABET.length()/files; j++) {
                char lletra = ALFABET.charAt(i * (ALFABET.length()/files) + j);
                Button button = new Button(context);
                button.setId(Character.toLowerCase(lletra));
                button.setText(lletra + "");
                button.setTextColor(COLOR_DEFAULT_TECLA);
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
            MainActivity.missatgeError(context, "No es pot esborrar lletres si no n'hi ha!");
            return;
        }
        Casella casella;
        if (!(lletraActual >= lengthWord && intentActual >= maxTry-1)){ // si última casilla no pinto la siguiente
            casella = Casella.getCasella(context, intentActual, lletraActual);
            casella.setBackground(getPinzell(false));
        }
        lletraActual--;
        casella = Casella.getCasella(context, intentActual, lletraActual);
        casella.setText("");
        casella.setBackground(getPinzell(true));
    }

    private void escriureLletra(char c){
        if (lletraActual >= lengthWord){
            MainActivity.missatgeError(context, "Envia per provar altres lletres!");
            return;
        }
        Casella casella = Casella.getCasella(context, intentActual, lletraActual);
        casella.setText(c + "");
        casella.setBackground(getPinzell(false));
        lletraActual++;
        if (!(lletraActual >= lengthWord && intentActual >= maxTry-1)){ // si última casilla no pinto la siguiente
            casella = Casella.getCasella(context, intentActual, lletraActual);
            casella.setBackground(getPinzell(true));
        }
    }

    private GradientDrawable getPinzell(boolean nouPunter){
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(nouPunter ? orangeColor : grayColor));
        return gd;
    }

    private void comprobarParaula(){
        String paraulaEscrita = "";
        for (int i = 0; i < lengthWord; i++) {
            paraulaEscrita += Casella.getCasella(context, intentActual, i);
        }
        paraulaEscrita = paraulaEscrita.toLowerCase();
        if (lletraActual < lengthWord){ // paraula incompleta
            MainActivity.missatgeError(context, "Paraula incompleta!");
            return;
        } else if (paraulaEscrita.compareToIgnoreCase(paraula) == 0){ // ha guanyat
            Intent intent = new Intent(context, PantallaFinal.class);
            intent.putExtra(MainActivity.MESSAGE_GUANYAT, true);
            intent.putExtra(MainActivity.MESSAGE_PARAULA, paraulaBenEscrita);
            context.startActivity(intent);
            return;
        } else if (!diccionariSolucions.contains(paraulaEscrita)){ // es comprova si forma part del catàleg
            MainActivity.missatgeError(context, "Paraula no vàlida!");
            return;
        } else if (intentActual >= maxTry-1){ // si no té més oportunitats ha perdut
            Intent intent = new Intent(context, PantallaFinal.class);
            intent.putExtra(MainActivity.MESSAGE_GUANYAT, false);
            intent.putExtra(MainActivity.MESSAGE_PARAULA, paraulaBenEscrita);
            intent.putExtra(MainActivity.MESSAGE_RESTRICCIONS, "res"); // inacabado -----------------------------
            intent.putExtra(MainActivity.MESSAGE_POSSIBILITATS, "pos");
            context.startActivity(intent);
            return;
        }
        for (int i = 0; i < paraulaEscrita.length(); i++) { // recòrrer la paraula escrita
            char lletraParaulaEscrita = paraulaEscrita.charAt(i);
            char lletraParaulaEsbrinar = Character.toLowerCase(paraulaAmbPosicionsLletres.get(i));
            Casella c = Casella.getCasella(context, intentActual, i);
            Button tecla = context.findViewById(lletraParaulaEscrita);
            boolean teclaPintada = tecla.getCurrentTextColor() != COLOR_DEFAULT_TECLA;
            if (lletraParaulaEsbrinar == lletraParaulaEscrita){
                c.setBackgroundColor(Color.parseColor(greenColor));
                tecla.setTextColor(Color.parseColor(greenColor));
                pistesAcertades.put(i, lletraParaulaEscrita);
                pistesQuasiAcertades.remove(lletraParaulaEscrita); // en cas de que estigués
                continue;
            }
            boolean dinsParaula = false;
            for (int pos = 0; pos < lengthWord; pos++) { // recòrrer paraulaAmbPosicionsLletres
                lletraParaulaEsbrinar = Character.toLowerCase(paraulaAmbPosicionsLletres.get(pos));
                if (lletraParaulaEsbrinar == lletraParaulaEscrita){
                    c.setBackgroundColor(Color.parseColor(orangeColor));
                    if (!teclaPintada) {
                        tecla.setTextColor(Color.parseColor(orangeColor));
                        pistesQuasiAcertades.add(lletraParaulaEscrita);
                    }
                    dinsParaula = true;
                    break;
                }
            }
            if (!dinsParaula){
                c.setBackgroundColor(Color.parseColor(redColor));
                if (!teclaPintada) {
                    tecla.setTextColor(Color.parseColor(redColor));
                    restriccions.add(lletraParaulaEscrita);
                }
            }
        }
        actualitzarSolucions();
        lletraActual = 0; // torna a començar a escriure per la següent fila
        intentActual++; // si la paraula ha estat vàlida es té en compte aquest intent
    }

    private void actualitzarSolucions(){
        Iterator iterador = diccionariSolucions.iterator();
        while (iterador.hasNext()) {
            String paraula = (String) iterador.next();
            if(!possibleSolucio(paraula)) {
                diccionariSolucions.remove(paraula);
            }
        }
        textViewInformatiu.setText("Hi ha "+solucions+" solucions posibles.");
    }

    private boolean possibleSolucio(String s) {
        Iterator iterador = pistesAcertades.iterator();
        while (iterador.hasNext()) {
            UnsortedArrayMapping.Pair element = (UnsortedArrayMapping.Pair)iterador.next();
            if (!pistesAcertades.get((int)element.getKey()).equals(s.charAt((int)element.getKey()))) {
                return false;
            }
        } // si pasa el bucle es porque van bien las pistas verdes
        iterador = pistesQuasiAcertades.iterator();
        while (iterador.hasNext()) {
            char lletra = (char)iterador.next();
            boolean trobat = false;
            for (int i = 0; i < s.length(); i++) {
                if (lletra == s.charAt(i)) {
                    trobat = true;
                    break;
                }
            }
            if (!trobat) {
                return trobat;
            }
        } // si pasa el bucle es porque van bien las pistas amarillas
        iterador = restriccions.iterator();
        while (iterador.hasNext()) {
            char lletra = (char)iterador.next();
            boolean trobat = false;
            for (int i = 0; i < s.length(); i++) {
                if (lletra == s.charAt(i)) {
                    trobat = true;
                    break;
                }
            }
            if (trobat) {
                return !trobat;
            }
        } // finalmente si pasa el bucle es porque cumple con las restricciones
        return true;
    }

    private void crearParaula(int numParaules){
        Random r = new Random();
        int numIteracions = r.nextInt(numParaules);
        numIteracions++; // me aseguro de que haga una iteración al menos
        int iteracionsFetes = 0;
        Iterator iterador = diccionariComplet.entrySet().iterator();
        while (iterador.hasNext() && iteracionsFetes < numIteracions) {
            HashMap.Entry element = (HashMap.Entry)iterador.next();
            iteracionsFetes++;
            if (((String)element.getKey()).length() == lengthWord) {
                paraula = (String) element.getKey();
                paraulaBenEscrita = (String) element.getValue();
            }
        }
        pistesAcertades = new UnsortedArrayMapping<>(lengthWord);
        pistesQuasiAcertades = new UnsortedArraySet<>(lengthWord);
        restriccions = new UnsortedArraySet<>(ALFABET.length() - lengthWord);
    }

    public static int getLongitudParaula(){
        return lengthWord;
    }

}

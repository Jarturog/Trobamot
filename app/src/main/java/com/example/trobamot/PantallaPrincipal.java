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
import java.util.PriorityQueue;
import java.util.Random;
import java.util.TreeMap;

/**
 * Pantalla principal on es juga.
 * @author Juan Arturo Abaurrea Calafell i Marta González Juan
 */
public class PantallaPrincipal {

    // context on s'executa
    private final AppCompatActivity context;
    // mapping que no canvia de paraules amb lengthword lletres i les seves versions amb accents
    private HashMap<String, String> diccionariComplet;
    private HashSet<String> diccionariSolucions; // conjunt de solucions possibles dinàmic
    // mapping que relaciona les lletres i el tipus de restricció (positiu si encert, negatiu si altre)
    private TreeMap<Character, Integer> restriccions;
    // alfabet amb les posicions de les lletres de la paraula a esbrinar
    private TreeMap<Character, HashSet<Integer>> alfabetAmbParaula;
    // punter a HashSet buit de les restriccions que serveixen per a distingir-lo dels encerts
    private final static HashSet<Integer> INICIALITZAT = new HashSet<>();
    private final static int FALL = -1, PISTA = -2; // fall per a casella vermella, pista per a groga
    // intentActual itera sobre maxTry, lletraActual sobre lengthWord i solucions és el nombre de solucions possibles
    private int intentActual, lletraActual, solucions;
    private final int widthDisplay, heightDisplay; // dimensions del dispositiu
    private TextView textViewSolucions; // mostra el nombre de solucions que queden
    // lengthWord és el nombre de lletres que tindrà la paraula i maxTry el nombre d'intents que té per esbrinar-la
    private static int lengthWord = 5, maxTry = 3; // perquè no se superposin TextView's el maxTry ha de ser menor que 8
    // colors i l'alfabet a emprar. Si es vol altre ordre de lletres al teclat es pot canviar l'ordre de lletres de l'alfabet
    private final static String grayColor = "#D9E1E8", orangeColor = "#E69138", redColor = "#CC0000",
             greenColor = "#38761D", blackColor = "#000000";

    /**
     * Constructor que inicialitza una nova partida
     * @param context el context on s'executa
     */
    public PantallaPrincipal(AppCompatActivity context){
        DisplayMetrics metrics = new DisplayMetrics(); // Object to store display information
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics); // Get display information
        widthDisplay = metrics.widthPixels; // se assignen els valors per emprar-los després
        heightDisplay = metrics.heightPixels;
        this.context = context;
        lletraActual = 0; // inicialització de valors
        intentActual = 0;
        String alfabet = "ABCÇDEFGHIJKLMNOPQRSTUVWXYZ";
        int nombreParaules = inicialitzarDiccionari(); // s'inicialitza el diccionari
        crearParaula(alfabet, nombreParaules); // paraula que està entre la posició 0 i nombreParaules del diccionari amb paraules amb lengthword lletres
        // en cas de que hagi superposició de ID's es decrementa el maxTry
        // això es deu a que cada tecla té com a ID el valor numèric de la seva lletra
        while (Casella.getBEGIN_IDs_CASELLES() + maxTry * lengthWord >= 'a') {
            MainActivity.missatgeError(context, maxTry + " son massa intents, es decrementen.");
            maxTry--;
        }
        crearTeclat(alfabet); // creació del teclat i dels dos botons
        crearTextSolucions(); // creació del text que informa de la quantitat de solucions possibles
        crearGraella(); // crea la graella on s'escriuran
    }

    /**
     * Inicialitza diccionariComplet i diccionariSolucions amb les paraules de lengthword lletres
     * @return nombre de paraules de lenghtword lletres
     */
    private int inicialitzarDiccionari() {
        try {
            diccionariComplet = new HashMap<>();
            diccionariSolucions = new HashSet<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.paraules)));
            String linia = br.readLine(); // es llegeix del fitxer diccionari
            while (linia != null && !linia.equals("")) { // mentre no final
                String paraulaAmbAccents = "";
                char lletra = linia.charAt(0);
                for (int i = 0; lletra != ';' && i < lengthWord; i++) { // mentre la paraula no sigui de lengthword lletres
                    paraulaAmbAccents += lletra; // acumula
                    linia = linia.substring(1); // seguënt lletra
                    lletra = linia.charAt(0);
                }
                if (lletra == ';' && paraulaAmbAccents.length() == lengthWord) { // si és de la longitud adequada
                    char[] caracters = linia.substring(1).toCharArray();
                    // com el diccionari substitueix la ç per la c a la versió sense accents l'afegeixo de nou
                    for (int i = 0; i < caracters.length; i++) {
                        if (paraulaAmbAccents.charAt(i) == 'ç') {
                            caracters[i] = 'ç';
                        }
                    }
                    // el següent codi afegeix una paraula i si la seva versió sense accents és igual
                    // a altra llavors substitueix l'anterior
                    String paraulaSenseAccents = new String(caracters);
                    diccionariComplet.put(paraulaSenseAccents, paraulaAmbAccents); // s'afegeixen les paraules
                    boolean jaEstava = !diccionariSolucions.add(paraulaSenseAccents);
                    if (!jaEstava) {
                        solucions++; // s'incrementa
                    }
                }
                linia = br.readLine(); // següent paraula
            }
            return solucions; // retorna les solucions
        } catch (IOException e) { // si excepció
            MainActivity.missatgeError(context, "No s'ha pogut inicialitzar el diccionari");
            throw new RuntimeException("No s'ha pogut inicialitzar el diccionari");
        }
    }

    /**
     * Selecciona aleatòriament una paraula del diccionari
     * @param nombreParaules nombre de paraules del diccionari
     */
    private void crearParaula(String alfabet, int nombreParaules){
        String paraula = null;
        Random r = new Random();
        int nombreIteracions = r.nextInt(nombreParaules); // posició aleatòria de la paraula del diccionari
        nombreIteracions++; // m'asseguro que faci una iteració almenys
        int iteracionsFetes = 0;
        Iterator<HashMap.Entry<String, String>> iterador = diccionariComplet.entrySet().iterator(); // iterador
        while (iterador.hasNext() && iteracionsFetes < nombreIteracions) { // mentre no final i no trobat (el no final es per si de cas)
            HashMap.Entry<String, String> element = iterador.next(); // següent element
            iteracionsFetes++; // següent iteració
            paraula = element.getKey(); // agafo la paraula sense accents
        }
        restriccions = new TreeMap<>(); // restriccions verdes, grogues i vermelles
        alfabetAmbParaula = new TreeMap<>(); // alfabet amb les posicions de les lletres de la paraula a esbrinar
        // s'inicialitzen primer les lletres de la paraula a esbrinar amb les posicions de cada una
        for (int pos = 0; pos < lengthWord; pos++) {
            char lletra = Character.toLowerCase(paraula.charAt(pos));
            HashSet<Integer> posicions = alfabetAmbParaula.get(lletra);
            if (posicions == null) {
                posicions = new HashSet<>();
                posicions.add(pos);
                alfabetAmbParaula.put(lletra, posicions);
            } else {
                posicions.add(pos);
            }
        }
        // després s'inicialitzen la resta de les lletres
        for (int pos = 0; pos < alfabet.length(); pos++) {
            char lletra = Character.toLowerCase(alfabet.charAt(pos));
            HashSet<Integer> posicions = alfabetAmbParaula.get(lletra);
            if (posicions == null) { // si la lletra no s'havia inicialitzat abans
                alfabetAmbParaula.put(lletra, INICIALITZAT); // s'inicialitza amb el HashSet buit INICIALITZAT
            }
        }
    }

    /**
     * Crea el teclat i els dos botons
     */
    private void crearTeclat(String alfabet) {
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        // FILES del teclat i el tamany del botó de la lletra en el teclat
        final int FILES = 3, TAMANY_LLETRA = widthDisplay/(alfabet.length()/FILES);
        for (int fila = 0; fila < FILES; fila++) {
            for (int columna = 0; columna < alfabet.length()/FILES; columna++) {
                char lletra = alfabet.charAt(fila * (alfabet.length()/FILES) + columna); // s'agafa la lletra
                Button button = new Button(context); // es crea el botó de la lletra
                button.setId(Character.toLowerCase(lletra)); // s'assigna l'ID
                button.setText(lletra + ""); // s'assigna la lletra
                button.setTextColor(Color.parseColor(blackColor)); // es posa de color negre
                // Tamany dels botons
                ConstraintLayout.LayoutParams paramsBoto = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                paramsBoto.height = TAMANY_LLETRA;
                paramsBoto.width = TAMANY_LLETRA;
                button.setLayoutParams(paramsBoto);
                // Posicionar els botons
                button.setX(widthDisplay - ((alfabet.length()/FILES)-columna) * TAMANY_LLETRA);
                button.setY(heightDisplay - (FILES-fila) * TAMANY_LLETRA);
                constraintLayout.addView(button); // Afegir el botó al layout
                button.setOnClickListener(v -> escriureLletra(lletra)); // Afegir la funcionalitat al botó
            }
        }
        // Crear els botons
        Button buttonEsborrar = new Button(context), buttonEnviar = new Button(context);
        buttonEsborrar.setText("Esborrar");
        buttonEnviar.setText("Enviar");
        // Tamany dels botons
        int buttonWidth = widthDisplay*3/8, buttonHeight = heightDisplay/10;
        ConstraintLayout.LayoutParams paramsBotoEnviar = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        paramsBotoEnviar.height = buttonHeight;
        paramsBotoEnviar.width = buttonWidth;
        buttonEnviar.setLayoutParams(paramsBotoEnviar);
        ConstraintLayout.LayoutParams paramsBotoEsborrar = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        paramsBotoEsborrar.height = buttonHeight;
        paramsBotoEsborrar.width = buttonWidth;
        buttonEsborrar.setLayoutParams(paramsBotoEsborrar);
        // Posicionar els botons
        int offset = widthDisplay/64;
        buttonEnviar.setX(widthDisplay/2 + offset);
        buttonEnviar.setY(heightDisplay - FILES * TAMANY_LLETRA - 2*offset - buttonHeight);
        buttonEsborrar.setX(widthDisplay/2 - (offset + buttonWidth));
        buttonEsborrar.setY(heightDisplay - FILES * TAMANY_LLETRA - 2*offset - buttonHeight);
        // Afegir el botó al layout
        constraintLayout.addView(buttonEsborrar);
        constraintLayout.addView(buttonEnviar);
        // Afegir la funcionalitat al botó
        buttonEsborrar.setOnClickListener(v -> llevarLletra());
        buttonEnviar.setOnClickListener(v -> comprovarParaula());
    }

    /**
     * Crea el text que està entre els botons i la graella que informa de les solucions possibles
     */
    private void crearTextSolucions(){
        textViewSolucions = new TextView(context); // es crea
        textViewSolucions.setText("Hi ha "+solucions+" solucions possibles."); // es posa el text
        int tamanyText = widthDisplay/50; // s'assigna el tamany
        textViewSolucions.setTextSize(tamanyText);
        // Posicionam el textview
        textViewSolucions.setX((widthDisplay/2)-tamanyText*textViewSolucions.getText().length()/2);
        textViewSolucions.setY(((1+maxTry)*widthDisplay/(lengthWord+2))+(widthDisplay/(2*(lengthWord+2))));
        ((ConstraintLayout)context.findViewById(R.id.layout)).addView(textViewSolucions); // l'afegim
    }

    /**
     * Crea la graella on s'escriuen les lletres
     */
    private void crearGraella() {
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        // es selecciona el pinzell, amb les caselles a gris, per això nouPunter = false
        GradientDrawable gd = getPinzell(false);
        for (int fila = 0; fila < maxTry; fila++) { // es crea la graella amb les caselles
            for (int columna = 0; columna < lengthWord; columna++) {
                // Crear el TextView i afegir-lo al layout i es posa + 2 pels espais als bordes
                constraintLayout.addView(new Casella(context, fila, columna, gd, widthDisplay/(lengthWord+2)));
            }
        }
        Casella c = Casella.getCasella(context, 0, 0); // es selecciona la primera casella
        c.setBackground(getPinzell(true)); // i es posa de color taronja perquè és on es comença a escriure
    }

    /**
     * Escriu una lletra a la casella on està el punter i es mou el punter a la següent casella
     * @param lletra la lletra a escriure
     */
    private void escriureLletra(char lletra){
        if (lletraActual >= lengthWord){ // si no hi ha més espais per escriure sense enviar
            MainActivity.missatgeError(context, "Envia per provar altres lletres!");
            return;
        }
        Casella casella = Casella.getCasella(context, intentActual, lletraActual);
        casella.setText(lletra + ""); // s'escriu la lletra
        casella.setBackground(getPinzell(false)); // es lleva el punter
        lletraActual++; // lletra escrita
        if (!(lletraActual >= lengthWord && intentActual >= maxTry-1)){ // si és la darrera casella no pinto de taronja la següent
            casella = Casella.getCasella(context, intentActual, lletraActual);
            casella.setBackground(getPinzell(true)); // es posa el punter a la nova casella
        }
    }

    /**
     * Esborra la lletra i posa el punter a una posició anterior
     */
    private void llevarLletra(){
        if (lletraActual <= 0){ // si no hi ha lletres
            MainActivity.missatgeError(context, "No es pot esborrar lletres si no n'hi ha!");
            return;
        }
        Casella casella;
        if (!(lletraActual >= lengthWord && intentActual >= maxTry-1)){ // si és la darrera casilla no pinto de gris la següent
            casella = Casella.getCasella(context, intentActual, lletraActual);
            casella.setBackground(getPinzell(false));
        }
        lletraActual--; // lletra esborrada
        casella = Casella.getCasella(context, intentActual, lletraActual);
        casella.setText(""); // es deixa buida la casella
        casella.setBackground(getPinzell(true)); // es pinta de taronja
    }

    /**
     * Retorna un pinzell que pinta de taronja o gris depenent de si es mou el punter allí o no
     * @param nouPunter true si es vol pintar de taronja, false si gris
     * @return el pinzell
     */
    private GradientDrawable getPinzell(boolean nouPunter){
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5);
        gd.setStroke(3, Color.parseColor(nouPunter ? orangeColor : grayColor));
        return gd;
    }

    /**
     * Decideix si la paraula és igual a la de l'alfabet
     * @param paraula paraula de la que es vol saber la coincidència
     * @return true si s'ha encertat
     */
    private boolean sonIguals(String paraula) {
        Iterator<TreeMap.Entry<Character, HashSet<Integer>>> iteradorAlfabet = alfabetAmbParaula.entrySet().iterator(); // iterador
        while (iteradorAlfabet.hasNext()) { // mentre no final
            TreeMap.Entry<Character, HashSet<Integer>> element = iteradorAlfabet.next(); // següent element
            char lletra = element.getKey();
            HashSet<Integer> posicions = element.getValue();
            Iterator<Integer> iteradorPosicions = posicions.iterator();
            while (iteradorPosicions.hasNext()) {
                int pos = iteradorPosicions.next();
                if (lletra != paraula.charAt(pos)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Es comprova la paraula i es tracta. Si guanya o perd es passa a la pantalla final
     */
    private void comprovarParaula(){
        String paraulaEscrita = "";
        for (int i = 0; i < lengthWord; i++) {
            paraulaEscrita += Casella.getCasella(context, intentActual, i);
        }
        paraulaEscrita = paraulaEscrita.toLowerCase(); // es passa a minúscules
        if (lletraActual < lengthWord){ // si paraula incompleta
            MainActivity.missatgeError(context, "Paraula incompleta!");
            return;
        } else if (sonIguals(paraulaEscrita)){ // si l'ha esbrinat
            context.startActivity(prepararSeguentPantalla(true)); // ha guanyat
            return;
        } else if (!diccionariComplet.containsKey(paraulaEscrita)){ // es comprova si forma part del catàleg
            MainActivity.missatgeError(context, "Paraula no vàlida!");
            return;
        } else if (intentActual >= maxTry-1){ // si no té més oportunitats
            context.startActivity(prepararSeguentPantalla(false)); // ha perdut
            return;
        }
        // en cas de que arribi aquí es processa les paraules per aconseguir pistes i restriccions i es passa al següent intent
        for (int i = 0; i < paraulaEscrita.length(); i++) { // recòrrer la paraula escrita
            char lletra = paraulaEscrita.charAt(i); // lletra escrita que s'està tractant
            HashSet<Integer> posicionsLletraResposta = alfabetAmbParaula.get(lletra);
            Casella c = Casella.getCasella(context, intentActual, i); // casella que s'està tractant
            Button tecla = context.findViewById(lletra); // tecla que s'està tractant
            // si la tecla no s'ha coloretjat teclaPintada = false
            boolean teclaPintada = tecla.getCurrentTextColor() != Color.parseColor(blackColor);
            if (posicionsLletraResposta.contains(i)) { // si coincideixen posicions
                c.setBackgroundColor(Color.parseColor(greenColor)); // casella verd
                tecla.setTextColor(Color.parseColor(greenColor)); // tecla verd
                restriccions.put(lletra, i); // s'afegeix la posició on s'ha trobat
                continue;
            }// en cas contrari
            boolean dinsParaula = false; // es suposa que la lletra escrita no està dins la paraula
            for (int pos = 0; pos < lengthWord; pos++) { // recòrrer paraulaPerEsbrinar
                if (posicionsLletraResposta.contains(pos)) { // si la lletra escrita està dins la paraula
                    c.setBackgroundColor(Color.parseColor(orangeColor)); // es posa com que està, taronja
                    if (!teclaPintada) { // si la tecla no ha estat pintada
                        tecla.setTextColor(Color.parseColor(orangeColor)); // es pinta
                        restriccions.put(lletra, PISTA); // s'afegeix que se sap que està però no on
                    }
                    dinsParaula = true; // s'ha trobat
                    break; // surt
                }
            }
            if (!dinsParaula) { // si no estava dins la paraula
                c.setBackgroundColor(Color.parseColor(redColor)); // casella vermella
                if (!teclaPintada) { // si no estava pintada la tecla
                    tecla.setTextColor(Color.parseColor(redColor)); // es pinta de vermell
                    restriccions.put(lletra, FALL); // s'afegeix que se sap que no està
                }
            }
        }
        actualitzarSolucions(); // s'actualitzen les solucions
        lletraActual = 0; // torna a començar a escriure per la següent fila
        intentActual++; // següent intent
    }

    /**
     * Es processen les paraules i les pistes per passar-les a la següent pantalla
     * @param guanyat true si ha guanyat, false si ha perdut
     * @return instància de classe Intent per enviar-lo a la pantalla final
     */
    private Intent prepararSeguentPantalla(boolean guanyat) {
        char [] paraula = new char[lengthWord];
        Iterator<TreeMap.Entry<Character, HashSet<Integer>>> iteradorAlfabet = alfabetAmbParaula.entrySet().iterator(); // iterador
        while (iteradorAlfabet.hasNext()) { // mentre no final
            TreeMap.Entry<Character, HashSet<Integer>> element = iteradorAlfabet.next(); // següent element
            char lletra = element.getKey();
            Iterator<Integer> iteradorPosicions = element.getValue().iterator();
            while (iteradorPosicions.hasNext()) {
                paraula[iteradorPosicions.next()] = lletra;
            }
        }
        Intent intent = new Intent(context, PantallaFinal.class); // es crea l'intent
        intent.putExtra(MainActivity.MESSAGE_GUANYAT, guanyat); // s'envia si ha guanyat o no
        intent.putExtra(MainActivity.MESSAGE_PARAULA, diccionariComplet.get(new String(paraula))); // s'envia la paraula ben escrita
        if (guanyat) { // si ha guanyat no fa falta enviar res més
            return intent; // retorna l'intent
        } // si ha perdut cal enviar les pistes i restriccions
        // RESTRICCIONS
        String sRestriccions = "";
        Iterator<TreeMap.Entry<Character, Integer>> iterador = restriccions.entrySet().iterator(); // iterador
        while (iterador.hasNext()) { // mentre quedin restriccions es passen a String i s'afegeixen al text
            TreeMap.Entry<Character, Integer> element = iterador.next(); // següent element
            char lletra = Character.toUpperCase(element.getKey());
            int pos = element.getValue();
            if (pos >= 0) {
                sRestriccions += "ha de contenir la " +  lletra + " a la posició " + (pos + 1) + ", ";
            } else if (pos == PISTA) {
                sRestriccions += "ha de contenir la " + lletra + " a qualque posició no descoberta, ";
            } else if (pos == FALL) {
                sRestriccions += "no ha de contenir la " + lletra + ", ";
            } // no hi ha else
        }
        if (sRestriccions.length() > 1) { // si no està buit es lleven el ", " finals i es posa un punt
            sRestriccions = sRestriccions.substring(0, sRestriccions.length() - 2) + ".";
        } else { // en cas contrari
            sRestriccions = "No s'han descobert";
        }
        intent.putExtra(MainActivity.MESSAGE_RESTRICCIONS, sRestriccions); // s'envien les restriccions
        // POSSIBILITATS
        // s'utilitza un monticle per ordenar alfabèticament les possibles paraules
        PriorityQueue<String> monticle = new PriorityQueue<>(); 
        Iterator<String> iteradorSols = diccionariSolucions.iterator(); // iterador
        while (iteradorSols.hasNext()) { // mentre quedin paraules
            monticle.add(iteradorSols.next()); // es passa del hashSet no ordenat al monticle ordenat
        }
        String possibilitats = "";
        while (!monticle.isEmpty()) { // mentre quedin paraules
            possibilitats += monticle.poll() + ", "; // es passen les possibilitats a String i ordenades
        }
        // s'envien les paraules
        intent.putExtra(MainActivity.MESSAGE_POSSIBILITATS, possibilitats.substring(0, possibilitats.length() - 2) + ".");
        return intent; // es retorna l'intent
    }

    /**
     * Actualitza el diccionari de solucions i el nombre d'elles
     */
    private void actualitzarSolucions() {
        Iterator<String> iterador = diccionariSolucions.iterator(); // iterador per recòrrer
        while (iterador.hasNext()) { // mentre quedin paraules
            String paraula = iterador.next(); // següent paraula
            if(!possibleSolucio(paraula)) { // si no és una possible solució
                iterador.remove(); // es lleva del diccionari
                solucions--; // es resta del contador
            }
        }
        textViewSolucions.setText("Hi ha "+solucions+" solucions possibles."); // s'actualitza el TextView
    }

    /**
     * Processa una paraula amb les pistes i restriccions conegudes per determinar
     * si és una possible solució o no.
     * @param paraula paraula a tractar
     * @return true si és una possible solució, fals en cas contrari.
     */
    private boolean possibleSolucio(String paraula) {
        Iterator<TreeMap.Entry<Character, Integer>> iterador = restriccions.entrySet().iterator(); // iterador
        while (iterador.hasNext()) { // mentre quedin restriccions es fan comprovacions
            TreeMap.Entry<Character, Integer> element = iterador.next(); // següent element
            char lletra = Character.toLowerCase(element.getKey());
            int pos = element.getValue();
            if (pos == PISTA) {
                boolean trobat = false;
                for (int i = 0; i < paraula.length() && !trobat; i++) {
                    if (paraula.charAt(i) == lletra) trobat = true;
                }
                if (!trobat) return false;
            } else if (pos == FALL) {
                for (int i = 0; i < paraula.length(); i++) {
                    if (paraula.charAt(i) == lletra) return false;
                }
            } else if (pos >= 0 && paraula.charAt(pos) != lletra) {
                return false;
            } // no hi ha else
        }
        return true; // si cumpleix les pistes i no té les restriccions és una possible solució
    }

    /**
     * Emprat per calcular l'índex de la casella a la classe Casella
     * @return la longitud de la paraula
     */
    public static int getLongitudParaula(){
        return lengthWord;
    }

}

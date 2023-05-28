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
import java.util.TreeSet;

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
    // mapping que relaciona posicions d'una paraula amb les seves lletres
    private UnsortedArrayMapping<Integer, Character> paraulaPerEsbrinar, pistesEncertades;
    // conjunts que emmagatzemen fets de la paraula que s'ha d'esbrinar
    private TreeSet<Character> pistesQuasiEncertades, restriccions;
    // intentActual itera sobre maxTry, lletraActual sobre lengthWord i solucions és el nombre de solucions possibles
    private int intentActual, lletraActual, solucions;
    private final int widthDisplay, heightDisplay; // dimensions del dispositiu
    private TextView textViewSolucions; // mostra el nombre de solucions que queden
    // lengthWord és el nombre de lletres que tindrà la paraula i maxTry el nombre d'intents que té per esbrinar-la
    private static int lengthWord = 5, maxTry = 6;
    // colors i l'alfabet a emprar. Si es vol altre ordre de lletres al teclat es pot canviar l'ordre de lletres de l'alfabet
    private final static String grayColor = "#D9E1E8", orangeColor = "#E69138", redColor = "#CC0000",
             greenColor = "#38761D", blackColor = "#000000", ALFABET = "ABCÇDEFGHIJKLMNOPQRSTUVWXYZ";

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
        int nombreParaules = inicialitzarDiccionari(); // s'inicialitza el diccionari
        crearParaula(nombreParaules); // paraula que està entre la posició 0 i nombreParaules del diccionari amb paraules amb lengthword lletres
        // en cas de que hagi superposició de ID's es decrementa el maxTry
        // això es deu a que cada tecla té com a ID el valor numèric de la seva lletra
        while (Casella.getBEGIN_IDs_CASELLES() + maxTry * lengthWord >= 'a') {
            MainActivity.missatgeError(context, maxTry + " son massa intents, es decrementen.");
            maxTry--;
        }
        crearTeclat(); // creació del teclat i dels dos botons
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
                    String paraulaSenseAccents = linia.substring(1);
                    diccionariComplet.put(paraulaSenseAccents, paraulaAmbAccents); // s'afegeixen les paraules
                    diccionariSolucions.add(paraulaSenseAccents);
                    solucions++; // s'incrementa
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
    private void crearParaula(int nombreParaules){
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
        pistesEncertades = new UnsortedArrayMapping<>(lengthWord); // posicions relacionades amb les lletres que les ocupen
        pistesQuasiEncertades = new TreeSet<>(); // pistes grogues, on se sap que estan però no la posició
        restriccions = new TreeSet<>(); // pistes vermelles, on se sap que no estan
        paraulaPerEsbrinar = new UnsortedArrayMapping<>(lengthWord); // posicions relacionades amb les lletres que les ocupen
        for (int pos = 0; pos < lengthWord; pos++) { // s'inicialitza la paraula per esbrinar
            paraulaPerEsbrinar.put(pos, paraula.charAt(pos)); // s'afegeixen les lletres a les posicions
        }
    }

    /**
     * Crea el teclat i els dos botons
     */
    private void crearTeclat() {
        ConstraintLayout constraintLayout = context.findViewById(R.id.layout);
        // FILES del teclat i el tamany del botó de la lletra en el teclat
        final int FILES = 3, TAMANY_LLETRA = widthDisplay/(ALFABET.length()/FILES);
        for (int fila = 0; fila < FILES; fila++) {
            for (int columna = 0; columna < ALFABET.length()/FILES; columna++) {
                char lletra = ALFABET.charAt(fila * (ALFABET.length()/FILES) + columna); // s'agafa la lletra
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
                button.setX(widthDisplay - ((ALFABET.length()/FILES)-columna) * TAMANY_LLETRA);
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
     * Es comprova la paraula i es tracta. Si guanya o perd es passa a la pantalla final
     */
    private void comprovarParaula(){
        String paraulaEscrita = "", paraulaCorrecta = "";
        for (int i = 0; i < lengthWord; i++) {
            paraulaEscrita += Casella.getCasella(context, intentActual, i);
        }
        for (int i = 0; i < lengthWord; i++) {
            paraulaCorrecta += paraulaPerEsbrinar.get(i);
        }
        paraulaEscrita = paraulaEscrita.toLowerCase(); // es passa a minúscules
        if (lletraActual < lengthWord){ // si paraula incompleta
            MainActivity.missatgeError(context, "Paraula incompleta!");
            return;
        } else if (paraulaEscrita.compareToIgnoreCase(paraulaCorrecta) == 0){ // si l'ha esbrinat
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
            char lletraParaulaEscrita = paraulaEscrita.charAt(i); // lletra escrita que s'està tractant
            char lletraParaulaEsbrinar = Character.toLowerCase(paraulaPerEsbrinar.get(i)); // lletra correcta que s'està tractant
            Casella c = Casella.getCasella(context, intentActual, i); // casella que s'està tractant
            Button tecla = context.findViewById(lletraParaulaEscrita); // tecla que s'està tractant
            // si la tecla no s'ha coloretjat teclaPintada = false
            boolean teclaPintada = tecla.getCurrentTextColor() != Color.parseColor(blackColor);
            if (lletraParaulaEsbrinar == lletraParaulaEscrita) { // si coincideix lletra
                c.setBackgroundColor(Color.parseColor(greenColor)); // casella verd
                tecla.setTextColor(Color.parseColor(greenColor)); // tecla verd
                pistesEncertades.put(i, lletraParaulaEscrita); // s'afegeix l'encert
                pistesQuasiEncertades.remove(lletraParaulaEscrita); // en cas de que estigués com a quasi encert es lleva
                continue;
            }// en cas contrari
            boolean dinsParaula = false; // es suposa que la lletra escrita no està dins la paraula
            for (int pos = 0; pos < lengthWord; pos++) { // recòrrer paraulaPerEsbrinar
                lletraParaulaEsbrinar = Character.toLowerCase(paraulaPerEsbrinar.get(pos)); // s'actualitza la lletra a tractar
                if (lletraParaulaEsbrinar == lletraParaulaEscrita) { // si la lletra escrita està dins la paraula
                    c.setBackgroundColor(Color.parseColor(orangeColor)); // es posa com que està, taronja
                    if (!teclaPintada) { // si la tecla no ha estat pintada
                        tecla.setTextColor(Color.parseColor(orangeColor)); // es pinta
                        pistesQuasiEncertades.add(lletraParaulaEscrita); // s'afegeix al conjunt de quasi encertades
                    }
                    dinsParaula = true; // s'ha trobat
                    break; // surt
                }
            }
            if (!dinsParaula) { // si no estava dins la paraula
                c.setBackgroundColor(Color.parseColor(redColor)); // casella vermella
                if (!teclaPintada) { // si no estava pintada la tecla
                    tecla.setTextColor(Color.parseColor(redColor)); // es pinta de vermell
                    restriccions.add(lletraParaulaEscrita); // s'afegeix
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
        String paraula = null;
        for (int i = 0; i < lengthWord; i++) {
            paraula += paraulaPerEsbrinar.get(i); // es torna la paraula en String
        }
        Intent intent = new Intent(context, PantallaFinal.class); // es crea l'intent
        intent.putExtra(MainActivity.MESSAGE_GUANYAT, guanyat); // s'envia si ha guanyat o no
        intent.putExtra(MainActivity.MESSAGE_PARAULA, diccionariComplet.get(paraula)); // s'envia la paraula ben escrita
        if (guanyat) { // si ha guanyat no fa falta enviar res més
            return intent; // retorna l'intent
        } // si ha perdut cal enviar les pistes i restriccions
        // ENCERTS
        String sRestriccions = "";
        Object[] pistesEncertadesOrdenades = pistesEncertades.sorted(); // s'ordenen els encerts alfabèticament
        for (int i = 0; i < pistesEncertadesOrdenades.length; i++) { // es passa a String
            UnsortedArrayMapping.Pair element = (UnsortedArrayMapping.Pair)pistesEncertadesOrdenades[i]; // casting
            sRestriccions += "ha de contenir la " + Character.toUpperCase((Character)element.getValue()) + " a la posició " + (((Integer)element.getKey())+1) + ", ";
        }
        // PISTES
        Iterator<Character> iterador = pistesQuasiEncertades.iterator(); // iterador
        while (iterador.hasNext()) { // mentre quedin pistes es passen a String i s'afegeixen al text
            sRestriccions += "ha de contenir la " + Character.toUpperCase(iterador.next()) + " a qualque posició no descoberta, ";
        }
        // RESTRICCIONS
        iterador = restriccions.iterator(); // iterador
        while (iterador.hasNext()) { // mentre quedin restriccions es passen a String i s'afegeixen al text
            sRestriccions += "no ha de contenir la " + Character.toUpperCase(iterador.next()) + ", ";
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
        Iterator<UnsortedArrayMapping.Pair> iteradorMapping = pistesEncertades.iterator(); // iterador del mapping
        while (iteradorMapping.hasNext()) { // mentres quedin encerts
            UnsortedArrayMapping.Pair element = iteradorMapping.next(); // següent encert
            int posicioLletra = (int) element.getKey(); // s'agafa la posició de la lletra
            // si les lletres a la mateixa posició no coincideixen
            if (!pistesEncertades.get(posicioLletra).equals(paraula.charAt(posicioLletra))) {
                return false; // no pot ser una solució
            }
        }
        Iterator<Character> iterador = pistesQuasiEncertades.iterator(); // iterador pistes grogues
        while (iterador.hasNext()) { // mentre quedin pistes
            char lletra = iterador.next(); // s'agafa la pista, que és una lletra que ha d'estar a la paraula
            boolean trobat = false; // no s'ha trobat encara
            for (int i = 0; i < paraula.length(); i++) { // es cerca fent un recorregut
                if (lletra == paraula.charAt(i)) { // si l'ha trobat
                    trobat = true;
                    break; // surt
                }
            }
            if (!trobat) { // si surt del bucle i no ha trobat la lletra
                return false; // com no s'ha trobat la pista no pot ser una solució
            }
        }
        iterador = restriccions.iterator(); // iterador restriccions vermelles
        while (iterador.hasNext()) { // mentre quedin restriccions
            char lletra = iterador.next(); // s'agafa la restricció, que és una lletra que no ha d'estar a la paraula
            boolean trobat = false;
            for (int i = 0; i < paraula.length(); i++) { // es fa un recorregut com abans
                if (lletra == paraula.charAt(i)) { // si l'ha trobat
                    trobat = true;
                    break; // surt
                }
            }
            if (trobat) { // si ha trobat la lletra que no hauria d'haver trobat
                return false; // no és una possible solució
            }
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

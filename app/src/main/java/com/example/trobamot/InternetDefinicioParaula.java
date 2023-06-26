package com.example.trobamot;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Classe auxiliar per tenir més ordenat el projecte.
 * Encarregada de donada una paraula retornar la seva definició
 * @author Juan Arturo Abaurrea Calafell i Marta González Juan
 */
public class InternetDefinicioParaula {

    private String paraula; // paraula de la qual es vol saber la seva definició

    /**
     * Constructor on només es passa la paraula
     * @param paraula paraula de la qual es vol saber la seva definició
     */
    public InternetDefinicioParaula(String paraula) {
        this.paraula = paraula;
    }

    /**
     * Es connecta a Internet i agafa la definició en brut.
     * @return definició en brut de la paraula.
     */
    private String agafaHTML() {
        try {
            URL definicio = new URL(Menu.getOpcion().link + paraula); // web de la definició
            BufferedReader in = new BufferedReader(new InputStreamReader(definicio.openStream()));
            StringBuffer sb = new StringBuffer();
            String line = in.readLine();
            while (line != null) {
                sb.append(line);
                line = in.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Mitjançant un fil aconsegueix la definició d'Internet i la passa a net per poder ser utilitzada fàcilment.
     * @return definició de la paraula.
     */
    public String getDefinicio() {
        String def [] = new String[1]; // s'emmagatzemarà la definició aquí
        Thread thread = new Thread(() -> {
            String json = agafaHTML();
            if (json == null || json == "") {
                def[0] = null;
                return;
            }
            try {
                JSONObject jObject = new JSONObject(json);
                def[0] = jObject.getString("d");
            } catch (JSONException e) {
                def[0] = null;
            }
        });
        thread.start();
        try {
            thread.join(); // s'executa el codi que agafa la definició
        } catch (InterruptedException e) {
            def[0] = null;
        }
        if (def[0] == null || def[0] == "") { // si no té definició
            def[0] = "No té definició";
        }
        return def[0];
    }
}

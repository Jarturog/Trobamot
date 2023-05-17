package com.example.trobamot;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

public class InternetDefinicioParaula {

    private String paraula;
    private Context context;

    public InternetDefinicioParaula(String paraula, Context contextQueLiCrida) {
        this.paraula = paraula;
        context = contextQueLiCrida;
    }

    private String agafaHTML() {
        try {
            URL definicio = new URL("https://www.vilaweb.cat/paraulogic/?diec="+ paraula);
            BufferedReader in = new BufferedReader(new InputStreamReader(definicio.openStream()));
            StringBuffer sb = new StringBuffer();
            String line = in.readLine();
            while (line != null) {
                sb.append(line);
                line = in.readLine();
            }
            return sb.toString();
        } catch (IOException e) {
            MainActivity.missatgeError(context, "No s'ha pogut agafar la definició");
            return null;
        }
    }

    public String getDefinicio() {
        AtomicReference<String> json = null;
        new Thread(() -> json.set(agafaHTML()));
        if (json.get() == null || json.get() == "") {
            return "No s'ha trobat la definició";
        }
        try {
            JSONObject jObject = new JSONObject(json.get());
            String def = jObject.getString("d");
            return def;
        } catch (JSONException e) {
            MainActivity.missatgeError(context, "No s'ha pogut agafar la definició");
            return null;
        }
    }


}

package com.example.trobamot;

import android.content.Context;

import androidx.annotation.Nullable;

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

    @Nullable
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
        String def [] = new String[1];
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
            thread.join();
        } catch (InterruptedException e) {
            def[0] = null;
        }
        if (def[0] == null) {
            MainActivity.missatgeError(context, "No s'ha trobat la definició");
        }
        return def[0];
    }


}

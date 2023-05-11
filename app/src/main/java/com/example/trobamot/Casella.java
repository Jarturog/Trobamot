package com.example.trobamot;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Casella extends androidx.appcompat.widget.AppCompatTextView {

    private static final int OFFSET = 16, BEGIN_IDs_CASELLAS = 9;

    public Casella(Context context, int fila, int columna, GradientDrawable gd, int textViewSize) {
        super(context);
        int espaiOcupat = textViewSize;
        textViewSize -= OFFSET; // para dejar espacio entre casillas
        // Definir la casella
        setBackground(gd);
        setId(getIdCasella(fila, columna));
        setWidth(textViewSize);
        setHeight(textViewSize);
        // Posicionam la casella
        setX(espaiOcupat + columna * (espaiOcupat));
        setY(espaiOcupat + fila * (espaiOcupat));
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        setTextColor(Color.BLACK);
        setTextSize(textViewSize / 5);
    }

    public static Casella getCasella(AppCompatActivity context, int intento, int letra){
        return context.findViewById(getIdCasella(intento, letra));
    }

    private static int getIdCasella(int fila, int columna) {
        return BEGIN_IDs_CASELLAS + columna + fila * PantallaPrincipal.getLongitudParaula();
    }

    @Override
    public String toString(){
        return this.getText().toString();
    }

    public static int getBEGIN_IDs_CASELLAS(){
        return BEGIN_IDs_CASELLAS;
    }
}

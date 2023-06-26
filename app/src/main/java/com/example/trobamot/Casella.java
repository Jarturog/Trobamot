package com.example.trobamot;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Casella de la graella on es posan lletres.
 * @author Juan Arturo Abaurrea Calafell i Marta González Juan
 */
public class Casella extends androidx.appcompat.widget.AppCompatTextView {

    // l'offset és per deixar espai entre caselles i 
    private static final int OFFSET = 16, BEGIN_IDs_CASELLES = 9;

    /**
     * Crea una casella al lloc indicat amb el tamany i pinzell indicats.
     * @param context context on es trobarà
     * @param fila fila que ocuparà de la graella
     * @param columna columna que ocuparà de la graella
     * @param gd pinzell de la casella
     * @param textViewSize tamany de la casella
     */
    public Casella(AppCompatActivity context, int fila, int columna, GradientDrawable gd, int textViewSize) {
        super(context);
        int espaiOcupat = textViewSize;
        textViewSize -= OFFSET; // per deixar espai entre caselles
        // Definir la casella
        setBackground(gd);
        setId(getIdCasella(fila, columna));
        setWidth(textViewSize);
        setHeight(textViewSize);
        // Posicionam la casella
        setX(espaiOcupat + columna * espaiOcupat);
        setY(espaiOcupat + fila * espaiOcupat + MainActivity.getStatusBarHeight(context));
        setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        setTextColor(Color.BLACK);
        setTextSize(textViewSize / 5);
    }

    /**
     * @param context context on està la casella
     * @param intent intent actual, sent 0 el primer intent
     * @param lletra lletra actual, sent 0 la primera lletra de la paraula
     * @return instància Casella que correspon al moment del joc passat per paràmetre
     */
    public static Casella getCasella(AppCompatActivity context, int intent, int lletra){
        return context.findViewById(getIdCasella(intent, lletra));
    }

    /**
     * Utiliza la longitud de la palabra predeterminada para calcular la ID. En caso de que lengthword
     * sea mucho mayor a la predeterminada puede dar problemas.
     * @param fila fila on està la casella començant des de 0
     * @param columna columna on està la casella començant des de 0
     * @return l'ID de la casella a la posició indicada
     */
    private static int getIdCasella(int fila, int columna) {
        return BEGIN_IDs_CASELLES + columna + fila * PantallaPrincipal.lengthword;
    }

    /**
     * @return lletra escrita a la casella.
     */
    @Override
    public String toString(){
        return this.getText().toString();
    }

    /**
     * @return l'ID on comencen les caselles.
     */
    public static int getBEGIN_IDs_CASELLES(){
        return BEGIN_IDs_CASELLES;
    }
}

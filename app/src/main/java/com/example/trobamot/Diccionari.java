package com.example.trobamot;

import java.io.BufferedReader;
import java.io.IOException;

public class Diccionari {
    BufferedReader br;
    public Diccionari(BufferedReader br){
        this.br = br;
    }

    public int getNumParaules(){
        int longitud = 0;
        try{
            BufferedReader brAux = new BufferedReader(br);
            while(brAux.readLine() != null) {
                longitud++;
            }
            brAux.close();
        }catch (IOException e){
            throw new RuntimeException("Error llegint el diccionari: "+e);
        }
        return longitud;
    }
}

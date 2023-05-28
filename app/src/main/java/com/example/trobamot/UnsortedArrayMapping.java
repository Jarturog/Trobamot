package com.example.trobamot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Mapping genèric no ordenat de valors comparables implementat amb un array.
 * @author Juan Arturo Abaurrea Calafell i Marta González Juan
 */
public class UnsortedArrayMapping<K, V extends Comparable<V>> {

    /**
     * Equivalent al Map.Entry de java
     */
    protected class Pair {
        private K key;
        private V valor;
        private Pair(K key, V valor) {this.key = key; this.valor = valor;}
        public K getKey() {return key;}
        public V getValue() {return valor;}
    }

    private final K[] claus; // keys
    private final V[] valors; // values
    private int n;

    public UnsortedArrayMapping(int max) {
        n = 0;
        claus = (K[]) new Object[max];
        valors = (V[]) new Comparable[max];
    }

    public V get(K key) {
        boolean trobat = false;
        int i = 0;
        while (i < n && !trobat) {
            trobat = claus[i++].equals(key);
        }
        return !trobat ? null : valors[i - 1];
    }

    public boolean put(K key, V valor) {
        if (isFull() || get(key) != null) {
            return false;
        }
        valors[n] = valor;
        claus[n++] = key;
        return true;
    }

    public boolean remove(K key) { // no s'empra però cal tenir-lo per considerar-lo un mapping
        if (isEmpty() || get(key) == null) {
            return false;
        }
        boolean trobat = false;
        int i = 0;
        for (; i < n && !trobat; i++) {
            trobat = key.equals(claus[i]);
        }
        if (!trobat) {
            return false;
        }
        claus[i-1] = claus[--n];
        return true;
    }

    public boolean isEmpty() {
        return n <= 0;
    }

    private boolean isFull() {
        return n >= valors.length;
    }

    public Iterator iterator(){ // retorna nova instància de l'iterador personalitzat de la classe
        return new IteratorUnsortedLinkedListMapping();
    }

    private class IteratorUnsortedLinkedListMapping implements Iterator { // iterador de la classe

        private int i; // índex

        private IteratorUnsortedLinkedListMapping () {
            i = 0; // s'inicialitza per recòrrer des de el primer element
        }

        /**
         * @return true si queden més elements, false si no
         */
        @Override
        public boolean hasNext() {
            return i < n;
        }

        /**
         * @return el següent element del mapping
         */
        @Override
        public Object next() {
            return new Pair(claus[i], valors[i++]); // es crea una instància Pair i s'incrementa i
        }
    }

    /**
     * Retorna un array del mapping ordenat amb mergesort per valor.
     * Cost: O(nlogn), que com en aquest cas n és molt petit (lengthWord) no hi ha problemes.
     * @return array d'Object que en realitat són instàncies Pair ordenat per valor.
     * S'ha de fer casting de cada element.
     */
    public Object[] sorted() {
        ArrayList<Pair> a = new ArrayList<>(n); // s'empra un ArrayList perquè implementa un mètode sort() que facilita tot
        for (int i = 0; i < n; i++) { // es fa un recorregut del mapping
            a.add(new Pair(claus[i], valors[i])); // s'afegeix nova instància Pair
        }
        a.sort(Comparator.comparing((Pair p) -> p.valor)); // s'ordena per valor
        return a.toArray(); // es retorna l'array
    }
}


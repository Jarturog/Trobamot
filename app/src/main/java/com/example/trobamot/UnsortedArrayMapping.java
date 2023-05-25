package com.example.trobamot;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

public class UnsortedArrayMapping<K, V extends Comparable<V>> {

        protected class Pair {
            private K key;
            private V valor;
            private Pair(K key, V valor) {this.key = key; this.valor = valor;}
            public K getKey() {return key;}
            public V getValue() {return valor;}
        }

        private final K[] claus;
        private final V[] valors;
        private int n;

        public UnsortedArrayMapping(int max) {
            n = 0;
            claus = (K[]) new Object[max];
            valors = (V[]) new Object[max];
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

        public boolean remove(K key) {
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

        public Iterator iterator(){
            return new IteratorUnsortedLinkedListMapping();
        }

        private class IteratorUnsortedLinkedListMapping implements Iterator {
            private int i;
            private IteratorUnsortedLinkedListMapping () {
                i = 0;
            }

            @Override
            public boolean hasNext() {
                return i < n;
            }

            @Override
            public Object next() {
                return new Pair(claus[i], valors[i++]);
            }
        }

        public Pair[] sorted() {
            Pair arrayOrdenat [] = (Pair[]) new Object[n];
            // quicksort
            for (int i = 0; i < n; i++) {
                arrayOrdenat[i] = new Pair(claus[i], valors[i]);
            }
            ComparadorDeValors c = new ComparadorDeValors();
            Arrays.sort(arrayOrdenat, c);
            return arrayOrdenat;
        }

        /**
         * Comparator
         */
        private class ComparadorDeValors implements Comparator<Pair> {
            @Override
            public int compare(Pair p1, Pair p2) {
                return p1.valor.compareTo(p2.valor);
            }
        }
    }


package com.example.trobamot;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.Iterator;

public class UnsortedArrayMapping<K,V> {

        protected class Pair {
            private K key;
            private V value;
            private Pair(K key, V value) {this.key = key; this.value = value;}
            public K getKey() {return key;}
            public V getValue() {return value;}
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

        public boolean put(K key, V value) {
            if (isFull() || get(key) != null) {
                return false;
            }
            valors[n] = value;
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
    }


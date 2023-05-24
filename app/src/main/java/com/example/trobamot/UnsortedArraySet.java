package com.example.trobamot;

import java.util.Iterator;

public class UnsortedArraySet<E> {

        private final E[] array;
        private int n;

        public UnsortedArraySet(int max) {
            n = 0;
            array = (E[]) new Object[max];
        }

        public boolean contains(Object elem) {
            boolean trobat = false;
            for (int i = 0; i < n && !trobat; i++) {
                trobat = elem.equals(array[i]);
            }
            return trobat;
        }

        public boolean add(Object elem) {
            if (isFull() || contains(elem)) {
                return false;
            }
            array[n++] = (E) elem;
            return true;
        }

        public boolean remove(Object elem) {
            if (isEmpty() || !contains(elem)) {
                return false;
            }
            boolean trobat = false;
            int i = 0;
            while (i < n && !trobat) {
                trobat = elem.equals(array[i++]);
            }
            if (!trobat) {
                return false;
            }
            array[i-1] = array[--n];
            return true;
        }

        public boolean isEmpty() {
            return n <= 0;
        }

        private boolean isFull() {
            return n >= array.length;
        }

        public Iterator iterator() {
            return new IteratorUnsortedArraySet();
        }

        private class IteratorUnsortedArraySet implements Iterator {
            private int i;

            private IteratorUnsortedArraySet() {
                i = 0;
            }

            @Override
            public boolean hasNext() {
                return i < n;
            }

            @Override
            public Object next() {
                return array[i];
            }
        }
    }


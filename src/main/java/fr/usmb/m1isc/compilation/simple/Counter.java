package fr.usmb.m1isc.compilation.simple;

public class Counter {
    int counter = 0;

    public void increment() {
        counter++;
    }

    public String toString() {
        return Integer.toString(counter);
    }
}

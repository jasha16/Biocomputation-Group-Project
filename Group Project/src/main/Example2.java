package main;

public class Example2 {
    boolean a, b, c, d;

    boolean x = !a && !b && (!c || !d || !a) && !d;
}

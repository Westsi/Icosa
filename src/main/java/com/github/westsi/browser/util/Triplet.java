package com.github.westsi.browser.util;

public class Triplet<F, S, T> {
    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public T getThird() {
        return third;
    }

    public void setThird(T third) {
        this.third = third;
    }

    private F first;
    private S second;
    private T third;

    public Triplet(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }



}

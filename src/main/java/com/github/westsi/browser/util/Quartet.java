package com.github.westsi.browser.util;

/**
 * Utility class to group together three pieces of data.
 * @author Westsi
 * @version %I%
 * @param <F> The type of the first piece of data in the triplet
 * @param <S> The type of the second piece of data
 * @param <T> The type of the third piece of data
 */
public class Quartet<F, S, T, O> {
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

    public O getFourth() {
        return fourth;
    }

    public void setFourth(O fourth) {
        this.fourth = fourth;
    }

    private F first;
    private S second;
    private T third;
    private O fourth;

    public Quartet(F first, S second, T third, O fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }



}

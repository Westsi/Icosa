package com.github.westsi.browser.util;

/**
 * Utility class to group together two pieces of data.
 * @author Westsi
 * @version %I%
 * @param <F> The type of the first piece of data in the pair
 * @param <S> The type of the second piece of data
 */
public class Pair<F, S> {
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

    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pair{");
        sb.append("first=").append(first);
        sb.append(", second=").append(second);
        sb.append('}');
        return sb.toString();
    }
}

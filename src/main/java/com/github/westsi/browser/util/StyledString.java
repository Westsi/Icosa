package com.github.westsi.browser.util;

import java.awt.*;

public class StyledString {
    public String string;
    public Font font;

    public StyledString(String string, Font font) {
        this.string = string;
        this.font = font;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StyledString{");
        sb.append("string='").append(string).append('\'');
        sb.append(", font=").append(font);
        sb.append('}');
        return sb.toString();
    }
}

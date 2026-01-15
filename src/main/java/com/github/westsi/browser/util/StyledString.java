package com.github.westsi.browser.util;

import java.awt.*;

/**
 * Utility class to group together a string and its style as a Font for rendering to a UI.
 * @author Westsi
 * @version %I%
 */
public class StyledString {
    public String string;
    public Font font;
    public String color;

    public StyledString(String string, Font font, String color) {
        this.string = string;
        this.font = font;
        this.color = color;
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

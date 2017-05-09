package com.elytradev.concrete.inventory.gui.widget;

public class WPlainPanel extends WPanel {
    public void add(WWidget w, int x, int y) {
        children.add(w);
        w.setLocation(x, y);
        if (w.canResize()) {
            w.setSize(18, 18);
        }
        valid = false;
    }

    public void add(WWidget w, int x, int y, int width, int height) {
        children.add(w);
        w.setLocation(x, y);
        if (w.canResize()) {
            w.setSize(width, height);
        }
    }
}

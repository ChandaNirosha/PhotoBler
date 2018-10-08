package com.example.sys9.ddrblur.adapter;

/**
 * Created by welcome on 7/27/2017.
 */

import android.graphics.Path;

public class DrawPath {
    public boolean color;
    public Path path;

    public DrawPath() {
        this.path = new Path();
        this.color = false;
    }

    public DrawPath(Path p, boolean b) {
        this.path = new Path(p);
        this.color = b;
    }
}
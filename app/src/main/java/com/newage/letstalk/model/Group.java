package com.newage.letstalk.model;

import java.io.Serializable;

public class Group implements Serializable {
    int image;
    String name;

    public Group(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}

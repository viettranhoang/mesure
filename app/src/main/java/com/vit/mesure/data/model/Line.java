package com.vit.mesure.data.model;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.Node;

public class Line extends Node{

    private Anchor anchor;
    private Float lenght;

    public Line() {
    }

    public Line(Anchor anchor, Float lenght) {
        this.anchor = anchor;
        this.lenght = lenght;
    }

    public Line(Anchor anchor) {
        this.anchor = anchor;
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public Float getLenght() {
        return lenght;
    }

    public void setLenght(Float lenght) {
        this.lenght = lenght;
    }
}

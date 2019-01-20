package com.vit.mesure.data.model;

import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Sphere;
import com.google.ar.sceneform.math.Vector3;

public class Line extends Node {

    public static final String TAG = Line.class.getSimpleName();

    private Anchor anchor;
    private float lenght;

    public Line() {
    }

    public Line(Anchor anchor, float lenght) {
        this.anchor = anchor;
        this.lenght = lenght;
        init();
    }

    public Line(Anchor anchor) {
        this.anchor = anchor;
        init();
    }

    private void init() {
        AnchorNode anchorNode = new AnchorNode(anchor);
        this.setCollisionShape(new Sphere(.01f, anchorNode.getLocalPosition()));
    }

    public Anchor getAnchor() {
        return anchor;
    }

    public void setAnchor(Anchor anchor) {
        this.anchor = anchor;
    }

    public float getLenght() {
        return lenght;
    }

    public void setLenght(float lenght) {
        this.lenght = lenght;
    }
}

package com.vit.mesure.data.model;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

public class FaceCameraNode extends Node {

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);

        Vector3 cameraPosition = this.getScene().getCamera().getWorldPosition();
        Vector3 direction = Vector3.subtract(cameraPosition, this.getWorldPosition());

        this.setWorldRotation(Quaternion.lookRotation(direction, Vector3.up()));
    }
}

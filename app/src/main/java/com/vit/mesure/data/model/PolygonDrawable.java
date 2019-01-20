package com.vit.mesure.data.model;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.vit.mesure.R;
import com.vit.mesure.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class PolygonDrawable {

    public static final String TAG = PolygonDrawable.class.getSimpleName();

    private List<Line> mLines = new ArrayList<>();

    private Scene mScene;

    private Context mContext;

    private Line mLine;

    private Line lineTemp;

    private boolean isDone = false;

    public PolygonDrawable(Scene scene, Context context) {
        this.mScene = scene;
        this.mContext = context;
        lineTemp = new Line();
    }

    public float getArea() {
        return .0f;
    }

    public float getPerimeter() {
        float p = .0f;

        if (!isFirstPoint()) {
            for (Line l : mLines) {
                p += l.getLenght();
            }
        }
        return p;
    }

    public void add(Anchor anchor) {
        if (!isDone) {
            mLine = new Line(anchor);
            mLine.setParent(mScene);
            drawPoint(anchor);

            if (!isFirstPoint()) {
                Anchor anchorLast = mLines.get(mLines.size() - 1).getAnchor();
                drawLine(anchorLast, anchor);
                mLine.setLenght(calculateLenght(anchorLast, anchor));
            }

            mLines.add(mLine);
        }
    }

    public void drawTemp(Anchor anchor) {
        mLine = new Line(anchor);
        mLine.setParent(mScene);

        if (!isFirstPoint() && !isDone) {
            mScene.onRemoveChild(lineTemp);
            drawPoint(anchor);
            Anchor anchorLast = mLines.get(mLines.size() - 1).getAnchor();
            drawLine(anchorLast, anchor);
            lineTemp = mLine;


            if (mLines.size() > 2) {
                ArrayList<Node> nodeTest = mScene.overlapTestAll(lineTemp);
                for (Node node : nodeTest) {
                    if (node.getCollisionShape().equals(mLines.get(0).getCollisionShape())) {
                        Log.i(TAG, "drawTemp: ");
                        mScene.onRemoveChild(lineTemp);
                        add(mLines.get(0).getAnchor());
                        isDone = true;
                        showP();
                    }
                }
            }

        }

    }

    public void undo() {
        isDone = false;
        if (!isFirstPoint()) {
            mScene.onRemoveChild(mLines.get(mLines.size() - 1));
            mLines.remove(mLines.size() - 1);
        } else {
            mScene.onRemoveChild(lineTemp);
        }
    }

    public boolean isDone() {
        return isDone;
    }

    private void showP() {
        Toast.makeText(mContext, "P = " + Utils.lenghtToString(getPerimeter()), Toast.LENGTH_SHORT).show();
    }

    private boolean isFirstPoint() {
        return mLines.size() == 0 ? true : false;
    }

    private float calculateLenght(Anchor anchor1, Anchor anchor2) {
        Pose form = anchor1.getPose();
        Pose to = anchor2.getPose();

        float dx = form.tx() - to.tx();
        float dy = form.ty() - to.ty();
        float dz = form.tz() - to.tz();

        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private void drawPoint(Anchor anchor) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(mLine);

        MaterialFactory.makeOpaqueWithColor(mContext, new Color(0.33f, 0.87f, 0f))
                .thenAccept(material -> {
                    ModelRenderable sphere = ShapeFactory.makeSphere(.005f, Vector3.zero(), material);
                    Node node = new Node();
                    node.setParent(anchorNode);
                    node.setRenderable(sphere);
                    node.setLocalPosition(Vector3.zero());
                });
    }

    private void drawLine(Anchor anchor1, Anchor anchor2) {
        AnchorNode anchorNode1 = new AnchorNode(anchor1);
        AnchorNode anchorNode2 = new AnchorNode(anchor2);
        anchorNode1.setParent(mLine);
        anchorNode2.setParent(mLine);

        Vector3 from = anchorNode1.getWorldPosition();
        Vector3 to = anchorNode2.getWorldPosition();

        Vector3 difference = Vector3.subtract(from, to);
        Vector3 directionFromTopToBottom = difference.normalized();
        Quaternion rotationFromAToB =
                Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());

        MaterialFactory.makeOpaqueWithColor(mContext, new Color(0.33f, 0.87f, 0f))
                .thenAccept(material -> {
                    ModelRenderable cube = ShapeFactory.makeCube(new Vector3(.005f, .005f, difference.length()),
                            Vector3.zero(), material);

                    Node lineNode = new Node();
                    lineNode.setParent(anchorNode2);
                    lineNode.setRenderable(cube);
                    lineNode.setWorldPosition(Vector3.add(from, to).scaled(.5f));
                    lineNode.setWorldRotation(rotationFromAToB);

                    drawText(Utils.lenghtToString(calculateLenght(anchor1, anchor2)), lineNode);

                });
    }

    private void drawText(String text, Node parent) {
        ViewRenderable.builder()
                .setView(mContext, R.layout.renderable_text)
                .build()
                .thenAccept(viewRenderable -> {
                    TextView textLenght = (TextView) viewRenderable.getView();
                    textLenght.setText(text);
                    viewRenderable.setShadowCaster(false);

                    FaceCameraNode node = new FaceCameraNode();
                    node.setParent(parent);
                    node.setRenderable(viewRenderable);
                    node.setLocalRotation(Quaternion.axisAngle(new Vector3(0f, 1f, 0f), 90f));
                    node.setLocalPosition(new Vector3(0f, 0.02f, 0f));
                });
    }
}

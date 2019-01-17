package com.vit.mesure;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ux.ArFragment;
import com.vit.mesure.data.model.LineDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.image_center)
    ImageView mImageCenter;

    private ArFragment mArFragment;

    private boolean isTracking;
    private boolean isHitting;

    private LineDrawable mLineDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mImageCenter = findViewById(R.id.image_center);
        mArFragment = (ArFragment)
                getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);

        mLineDrawable = new LineDrawable(mArFragment.getArSceneView().getScene(), this);

        mArFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            mArFragment.onUpdate(frameTime);
            onUpdate();
        });
    }

    @OnClick(R.id.image_undo)
    void onClickUndo() {
        mLineDrawable.undo();
    }

    private void onUpdate() {
        updateTracking();
        if (isTracking) {
            boolean hitTestChanged = updateHitTest();
            if (isHitting) {
                mImageCenter.setVisibility(View.VISIBLE);
                Frame frame = mArFragment.getArSceneView().getArFrame();
                android.graphics.Point pt = getScreenCenter();
                List<HitResult> hits;
                if (frame != null) {
                    hits = frame.hitTest(pt.x, pt.y);
                    Anchor anchor = hits.get(0).createAnchor();
                    mLineDrawable.drawTemp(anchor);

                    mArFragment.getArSceneView().setOnClickListener(v -> {
                        mLineDrawable.add(anchor);
                    });
                }
            } else {
                mImageCenter.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean updateHitTest() {
        Frame frame = mArFragment.getArSceneView().getArFrame();
        android.graphics.Point pt = getScreenCenter();
        List<HitResult> hits;
        boolean wasHitting = isHitting;
        isHitting = false;
        if (frame != null) {
            hits = frame.hitTest(pt.x, pt.y);
            for (HitResult hit : hits) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane &&
                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    isHitting = true;
                    break;
                }
            }
        }
        return wasHitting != isHitting;
    }

    private boolean updateTracking() {
        Frame frame = mArFragment.getArSceneView().getArFrame();
        boolean wasTracking = isTracking;
        isTracking = frame != null &&
                frame.getCamera().getTrackingState() == TrackingState.TRACKING;
        return isTracking != wasTracking;
    }


    private android.graphics.Point getScreenCenter() {
        View vw = findViewById(android.R.id.content);
        return new android.graphics.Point(vw.getWidth() / 2, vw.getHeight() / 2);
    }
}

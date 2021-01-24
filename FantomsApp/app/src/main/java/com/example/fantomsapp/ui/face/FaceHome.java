package com.singh.fanar.ui.face;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Frame;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.google.ar.sceneform.ux.TransformableNode;
import com.singh.splash_screen.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FaceHome extends AppCompatActivity {
    private ModelRenderable modelRenderable;
    private Texture texture;
    private boolean isAdded = false;
    private final HashMap<AugmentedFace, AugmentedFaceNode> faceNodeMap = new HashMap<>();
    ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_face);

        CameraFace cameraFace = (CameraFace) getSupportFragmentManager().findFragmentById(R.id.carFragment);

                         final String gtlf ="https://poly.googleusercontent.com/downloads/c/fp/1605550615230672/b-FBTyZ8BX8/1A6iXJcREA6/plaid.gltf";

        ModelRenderable.builder()
                .setSource(this, R.raw.plaid)
                .build()
                .thenAccept(rendarable -> {
                    this.modelRenderable = rendarable;
                    this.modelRenderable.setShadowCaster(false);
                    this.modelRenderable.setShadowReceiver(false);

                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "error loading model", Toast.LENGTH_SHORT).show();
                    return null;
                });

        // Load the face mesh texture.(2D texture on face)
        // Save the texture(.png file) in drawable folder.
        Texture.builder()
                .setSource(this, R.drawable.image0)
                .build()
                .thenAccept(textureModel -> this.texture = textureModel)
                .exceptionally(throwable -> {
                    Toast.makeText(this, "cannot load texture", Toast.LENGTH_SHORT).show();
                    return null;
                });

        assert cameraFace  != null;


        cameraFace.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        cameraFace.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if (modelRenderable == null || texture == null) {
                return;
            }
            Frame frame = cameraFace.getArSceneView().getArFrame();
            assert frame != null;


            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);

            // Make new AugmentedFaceNodes for any new faces.
            for (AugmentedFace augmentedFace : augmentedFaces) {
                if (isAdded) return;

                AugmentedFaceNode augmentedFaceMode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceMode.setParent(cameraFace.getArSceneView().getScene());
                //augmentedFaceMode.setFaceRegionsRenderable(modelRenderable);
                augmentedFaceMode.setFaceMeshTexture(texture);
                faceNodeMap.put(augmentedFace, augmentedFaceMode);
                isAdded = true;

                Iterator<Map.Entry<AugmentedFace, AugmentedFaceNode>> iterator = faceNodeMap.entrySet().iterator();
                Map.Entry<AugmentedFace, AugmentedFaceNode> entry = iterator.next();
                AugmentedFace face = entry.getKey();
                while (face.getTrackingState() == TrackingState.STOPPED) {
                    AugmentedFaceNode node = entry.getValue();
                    node.setParent(null);


                    /*TransformableNode nodet = new TransformableNode(cameraFace.getTransformationSystem());

                    AnchorNode anchorNode = new AnchorNode();

                    nodet.setRenderable(modelRenderable);
                    nodet.setParent(anchorNode);
                    arFragment.getArSceneView().getScene().addChild(anchorNode);
                    nodet.select();

                     */

                    iterator.remove();
                }
            }
        });
    }
}

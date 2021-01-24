package com.singh.fanar.ui.face;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import java.util.EnumSet;
import java.util.Set;

public class CameraFace extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);

        // Configure 3D Face Mesh
        config.setAugmentedFaceMode(Config.AugmentedFaceMode.MESH3D);
        this.getArSceneView().setupSession(session);
        return config;
    }

    @Override
    protected Set<Session.Feature> getSessionFeatures() {
        // Configure Front Camera
        return EnumSet.of(Session.Feature.FRONT_CAMERA);
    }

    // Override to turn off planeDiscoveryController.
    // Plane traceable are not supported with the front camera.
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);
        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);
        return frameLayout;
    }
}
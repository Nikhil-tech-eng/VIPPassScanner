package com.nikhil.vippassscanner.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nikhil.vippassscanner.R;


import android.annotation.SuppressLint;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Handler;
import android.os.Looper;

import android.media.MediaPlayer;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.firestore.FieldValue;

public class ScannerActivity extends AppCompatActivity {

    private ExecutorService cameraExecutor;

    private boolean isScanning = false;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private FirebaseFirestore db;

    private MediaPlayer mediaPlayer;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        previewView = findViewById(R.id.previewView);

        cameraExecutor = Executors.newSingleThreadExecutor();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        mediaPlayer = MediaPlayer.create(this, R.raw.success_beep);



        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("QR Scanner");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkCameraPermission();
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

//            Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();

            startCamera();

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();

                startCamera();

            } else {

                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void startCamera() {

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder().build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            processImageProxy(imageProxy);
        });

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.unbindAll();

        cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalysis
        );
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void processImageProxy(ImageProxy imageProxy) {

        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees()
        );

        BarcodeScanner scanner = BarcodeScanning.getClient();

        scanner.process(image)
                .addOnSuccessListener(barcodes -> {

                    if (isScanning) {
                        imageProxy.close();
                        return;
                    }

                    for (Barcode barcode : barcodes) {

                        String qrText = barcode.getRawValue();

                        if (qrText != null) {

                            isScanning = true;

                            runOnUiThread(() -> {
                                validatePass(qrText);
                            });

                            break;
                        }
                    }

                    imageProxy.close();
                })
                .addOnFailureListener(e -> imageProxy.close());
    }

    private void validatePass(String passCode) {

        db.collection("passes")
                .document(passCode)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(this,
                                "❌ INVALID PASS",
                                Toast.LENGTH_SHORT).show();

                        finishScanner();
                        return;
                    }

                    Boolean used = documentSnapshot.getBoolean("used");

                    if (Boolean.TRUE.equals(used)) {

                        Toast.makeText(this,
                                "⚠ PASS ALREADY USED",
                                Toast.LENGTH_SHORT).show();

                        finishScanner();
                        return;
                    }

                    Map<String, Object> updates = new HashMap<>();

                    updates.put("used", true);

                    updates.put("scannedBy",
                            auth.getCurrentUser() != null
                                    ? auth.getCurrentUser().getEmail()
                                    : "Unknown");

                    updates.put("scannedAt", FieldValue.serverTimestamp());

                    Map<String, Object> history = new HashMap<>();

                    history.put("passCode", passCode);
                    history.put("status", "VALID");

                    history.put("scannedBy",
                            auth.getCurrentUser() != null
                                    ? auth.getCurrentUser().getEmail()
                                    : "Unknown");

                    history.put("scannedAt", FieldValue.serverTimestamp());

                    db.collection("scanHistory")
                            .add(history);

                    documentSnapshot.getReference()
                            .update(updates)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(this,
                                        "✅ ACCESS GRANTED",
                                        Toast.LENGTH_SHORT).show();

                                if (mediaPlayer != null) {
                                    mediaPlayer.start();
                                }

                                finishScanner();

                            })
                            .addOnFailureListener(e -> {

                                Toast.makeText(this,
                                        "UPDATE FAILED",
                                        Toast.LENGTH_LONG).show();

                                isScanning = false;

                            });

                })

                .addOnFailureListener(e -> {

                    Toast.makeText(this,
                            "DATABASE ERROR",
                            Toast.LENGTH_SHORT).show();

                    finishScanner();

                });

    }

    private void finishScanner() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            finish();

        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}


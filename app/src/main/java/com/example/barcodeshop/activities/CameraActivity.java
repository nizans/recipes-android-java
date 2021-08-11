package com.example.barcodeshop.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.barcodeshop.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;


    PreviewView previewView;
    FloatingActionButton btnCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException ignored) {

            }
        }, ContextCompat.getMainExecutor(this));





    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageCapture imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(previewView.getDisplay().getRotation())
                        .build();

//        ImageAnalysis imageAnalysis =
//                new ImageAnalysis.Builder()
//                        .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
//                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                        .build();
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::scan);

        btnCapture.setOnClickListener(v -> {
            File file = new File(getFilesDir(), "temp_recipe_image_" + System.currentTimeMillis()+".jpg");
            ImageCapture.OutputFileOptions outputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, Runnable::run, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull @NotNull ImageCapture.OutputFileResults outputFileResults) {
                    Intent data = new Intent();
                    data.setData(Uri.fromFile(file));
                    setResult(RESULT_OK, data);
                    finish();
                }
                @Override
                public void onError(@NonNull @NotNull ImageCaptureException exception) {
                    exception.printStackTrace();
                    Log.d(TAG, "onError: IMAGE CAPTURE ERROR");
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
        });

        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview);
    }

    public void scan(ImageProxy imageProxy) {
        @SuppressLint("UnsafeOptInUsageError") Image mediaImage = imageProxy.getImage();

        if(mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage,
                    imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanner scanner = BarcodeScanning.getClient();
            scanner.process(image)
                    .addOnSuccessListener(this::printBarcodes)
                    .addOnFailureListener(e -> Log.i(TAG, "scan: FAIL" + Arrays.toString(e.getStackTrace())));
        } else {
            Log.i(TAG, "scan: IMAGE IS NULL");
        }
    }



    public void printBarcodes(List<Barcode> barcodes) {
        for (Barcode barcode: barcodes) {
            String rawValue = barcode.getRawValue();
            int valueType = barcode.getValueType();
            Intent data = new Intent();
            data.setData(Uri.parse(rawValue));
            if (valueType == Barcode.TYPE_PRODUCT) {
                Log.i(TAG, "printBarcodes: barcode value: " + rawValue);
                setResult(RESULT_OK, data);
                finish();
            } else {
                Log.i(TAG, "printBarcodes: barcode type unidentified");
                setResult(RESULT_CANCELED, data);
                finish();
            }
        }
    }
}


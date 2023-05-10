package com.example.mybudgetapp;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BudgetPredictor {
    private Interpreter interpreter;

    public BudgetPredictor(AssetManager assetManager) throws IOException {
        // Load the TensorFlow Lite model from the asset file
        MappedByteBuffer modelBuffer = loadModelFile(assetManager, "trained_model.tflite");

        // Create an instance of the Interpreter
        interpreter = new Interpreter(modelBuffer);
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float predictBudget(float monthlyExpense) {
        // Create an input float array of size 1
        float[][] input = new float[1][1];
        input[0][0] = monthlyExpense;

        // Create an output float array of size 1
        float[][] output = new float[1][1];

        // Run inference using the TensorFlow Lite model
        interpreter.run(input, output);

        // Return the predicted budget
        return output[0][0];
    }
}

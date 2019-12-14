package com.example.stree;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class ExerciseActivity extends AppCompatActivity {

    PDFView exercises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        exercises=(PDFView)findViewById(R.id.exercise);

        exercises.fromAsset("gym.pdf").load();


    }
}
package com.example.duplimage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StartScreen extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duplimage_1);

//        Intent manageAllFiles = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//        startActivity(manageAllFiles);
        Button btn_getstarted = findViewById(R.id.btn_getstarted);

        btn_getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartScreen.this, MainActivity.class));
            }
        });
    }
}

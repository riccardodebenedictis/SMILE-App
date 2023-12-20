package it.cnr.istc.pst.smile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class InitActivity extends AppCompatActivity {

    ImageButton colostomia_button;
    ImageButton ileostomia_button;
    ImageButton urostomia_button;
    Button other_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        colostomia_button = findViewById(R.id.colostomia_button);
        ileostomia_button = findViewById(R.id.ileostomia_button);
        urostomia_button = findViewById(R.id.urostomia_button);
        other_button = findViewById(R.id.other_stomia_button);

        colostomia_button.setOnClickListener(v -> {
            finish();
        });
        ileostomia_button.setOnClickListener(v -> {
            finish();
        });
        urostomia_button.setOnClickListener(v -> {
            finish();
        });
        other_button.setOnClickListener(v -> {
            finish();
        });
    }
}
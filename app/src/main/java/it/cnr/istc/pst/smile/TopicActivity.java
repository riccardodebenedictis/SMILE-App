package it.cnr.istc.pst.smile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class TopicActivity extends AppCompatActivity {

    ImageButton diet_button_button;
    ImageButton physical_activity_button;
    ImageButton bag_replacement_button;
    ImageButton infection_and_risks_button;
    ImageButton drugs_and_creams_button;
    Button other_topic_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        diet_button_button = findViewById(R.id.diet_button);
        physical_activity_button = findViewById(R.id.physical_activity_button);
        bag_replacement_button = findViewById(R.id.bag_replacement_button);
        infection_and_risks_button = findViewById(R.id.infection_and_risks_button);
        drugs_and_creams_button = findViewById(R.id.drugs_and_creams_button);
        other_topic_button = findViewById(R.id.other_topic_button);

        diet_button_button.setOnClickListener(v -> {
            finish();
        });
        physical_activity_button.setOnClickListener(v -> {
            finish();
        });
        bag_replacement_button.setOnClickListener(v -> {
            finish();
        });
        infection_and_risks_button.setOnClickListener(v -> {
            finish();
        });
        drugs_and_creams_button.setOnClickListener(v -> {
            finish();
        });
        other_topic_button.setOnClickListener(v -> {
            finish();
        });
    }
}
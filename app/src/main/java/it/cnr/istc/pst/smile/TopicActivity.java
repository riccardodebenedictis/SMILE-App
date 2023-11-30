package it.cnr.istc.pst.smile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class TopicActivity extends AppCompatActivity {

    Button diet_button_button;
    Button physical_activity_button;
    Button bag_replacement_button;
    Button infection_and_risks_button;
    Button drugs_and_creams_button;
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

        other_topic_button.setOnClickListener(v -> {
            finish();
        });
    }
}
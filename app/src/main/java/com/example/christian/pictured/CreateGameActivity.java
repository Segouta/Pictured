package com.example.christian.pictured;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

public class CreateGameActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        MultiAutoCompleteTextView textView;
        textView = findViewById(R.id.edit);
        textView.setAdapter(adapter);
        textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
    }

    private static final String[] COUNTRIES = new String[] {
            "Sven", "Britt", "Susanne", "Natasja", "David", "Justo", "Nathalie", "friend1", "friend2", "friend3", "friend4"
    };
}
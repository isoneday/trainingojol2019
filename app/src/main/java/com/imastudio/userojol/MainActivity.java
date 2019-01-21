package com.imastudio.userojol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.imastudio.userojol.activity.GoRideActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);
    }

    public void onHistory(View view) {
    }

    public void onGoride(View view) {
        startActivity(new Intent(this,GoRideActivity.class));
    }
}

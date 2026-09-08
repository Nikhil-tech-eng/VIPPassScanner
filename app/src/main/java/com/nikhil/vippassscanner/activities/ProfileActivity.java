package com.nikhil.vippassscanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nikhil.vippassscanner.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvEmail, tvUid;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvEmail = findViewById(R.id.tvEmail);
        tvUid = findViewById(R.id.tvUid);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            tvEmail.setText("Email : " + currentUser.getEmail());
            tvUid.setText("User ID : " + currentUser.getUid());
        }

        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
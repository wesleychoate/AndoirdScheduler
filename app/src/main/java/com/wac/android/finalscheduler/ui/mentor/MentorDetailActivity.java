package com.wac.android.finalscheduler.ui.mentor;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.material.textfield.TextInputLayout;

import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.util.SharedViewModel;
import com.wac.android.finalscheduler.util.SharedViewModelFactory;

public class MentorDetailActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;

    public static final String MENTOR_REPLY = "com.wac.android.finalscheduler.mentorlistsql.REPLY";

    private TextInputLayout name, phone, email;
    private Mentor updateMentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_detail);
        Intent inboundIntent = getIntent();
        updateMentor = inboundIntent.getParcelableExtra("mentor");

        SharedViewModelFactory svmf = new SharedViewModelFactory(SharedViewModel.getInstance(this.getApplication()));
        sharedViewModel = new ViewModelProvider(this, svmf).get(SharedViewModel.class);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (updateMentor != null)
                actionBar.setTitle("Update Mentor");
            else
                actionBar.setTitle("Create Mentor");
        }

        name = findViewById(R.id.nameField);
        phone = findViewById(R.id.numberField);
        email = findViewById(R.id.emailField);

        if (updateMentor != null) {
            name.getEditText().setText(updateMentor.getName());
            phone.getEditText().setText(updateMentor.getPhone());
            email.getEditText().setText(updateMentor.getEmail());
        }

        final Button button = findViewById(R.id.mentorSaveButton);
        button.setOnClickListener(view -> {
            if (validateMentor()) {
                Intent replyIntent = new Intent();
                Mentor mentor = new Mentor(name.getEditText().getText().toString(), phone.getEditText().getText().toString(), email.getEditText().getText().toString());
                if (updateMentor != null) {
                    mentor.setId(updateMentor.getId());
                }
                replyIntent.putExtra(MENTOR_REPLY, mentor);
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateMentor() {
        resetHelperText();
        if (TextUtils.isEmpty(name.getEditText().getText())) {
            name.setHelperText("Name is required.");
            return false;
        }
        if (TextUtils.isEmpty(phone.getEditText().getText())) {
            phone.setHelperText("Phone number is required.");
            return false;
        }
        if (TextUtils.isEmpty(email.getEditText().getText())) {
            email.setHelperText("Email is required.");
            return false;
        }

        return true;
    }

    private void resetHelperText() {
        name.setHelperText("");
        phone.setHelperText("");
        email.setHelperText("");
    }
}
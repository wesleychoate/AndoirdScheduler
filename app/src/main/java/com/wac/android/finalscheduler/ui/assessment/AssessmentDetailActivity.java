package com.wac.android.finalscheduler.ui.assessment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Assessment;
import com.wac.android.finalscheduler.util.NotificationHelper;
import com.wac.android.finalscheduler.util.NotificationScheduler;
import com.wac.android.finalscheduler.util.SharedViewModel;
import com.wac.android.finalscheduler.util.SharedViewModelFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AssessmentDetailActivity extends AppCompatActivity {

    SharedViewModel sharedViewModel;

    public static final String MENTOR_REPLY = "com.wac.android.finalscheduler.assessmentlistsql.REPLY";
    private DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private String pageTitle = "Create Assessment";

    private TextInputLayout name, dueDate;
    private SwitchMaterial reminder;
    private RadioGroup assessmentType;
    private MaterialRadioButton objButton, perfButton;
    private Button saveButton;

    private Assessment existingAssessment;
    private Integer courseId;
    private int existingPosition;

    private NotificationScheduler notificationScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // boilerplate setup
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);
        ActionBar actionBar = getSupportActionBar();
        SharedViewModelFactory svmf = new SharedViewModelFactory(SharedViewModel.getInstance(this.getApplication()));
        sharedViewModel = new ViewModelProvider(this, svmf).get(SharedViewModel.class);
        notificationScheduler = new NotificationScheduler(this);

        Intent inboundIntent = getIntent();

        // incoming info, for edit vs new
        existingAssessment = inboundIntent.getParcelableExtra("assessment");
        courseId = inboundIntent.getIntExtra("courseId", -1);
        existingPosition = inboundIntent.getIntExtra("position", -1);

        // get the fields
        name = findViewById(R.id.assessmentTitle);
        dueDate = findViewById(R.id.dueDateField);
        assessmentType = findViewById(R.id.assessmentType);
        reminder = findViewById(R.id.assessmentNotification);
        objButton = findViewById(R.id.radio_button_objective);
        perfButton = findViewById(R.id.radio_button_performance);

        // setup title bar and prepopulate fields if edit
        if (existingAssessment != null) {
            WorkInfo.State testing = null;
            if (existingAssessment.getJobId() != null)
                testing = notificationScheduler.getNotificationState(existingAssessment.getJobId());
            Log.i("assessment", "State of notification: " + testing );
            courseId = existingAssessment.getCourseId();
            pageTitle = "Update Assessment";
            name.getEditText().setText(existingAssessment.getTitle());
            reminder.setChecked(existingAssessment.getDueDateAlert());
            if ("objective".equalsIgnoreCase(existingAssessment.getType()))
                objButton.setChecked(true);
            else
                perfButton.setChecked(true);
            if (existingAssessment.getDueDate() != null)
                dueDate.getEditText().setText(f.format(existingAssessment.getDueDate()));
        }

        if (actionBar != null) {
            actionBar.setTitle(pageTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        createSaveButton();

    }

    private void resetHelperText() {
        name.setHelperText("");
        dueDate.setHelperText("mm/dd/yyyy");
    }

    private boolean validateAssessment() {
        if (TextUtils.isEmpty(name.getEditText().getText())) {
            name.setHelperText("Name is required.");
            return false;
        }

        LocalDate tempDueDate;
        if (TextUtils.isEmpty(dueDate.getEditText().getText())) {
            dueDate.setHelperText("Due date is required. mm/dd/yyyy format.");
            return false;
        } else {
            try {
                tempDueDate = f.parse(dueDate.getEditText().getText().toString(), LocalDate::from);
            } catch (DateTimeParseException e) {
                dueDate.setHelperText("Invalid date. mm/dd/yyyy required format.");
                return false;
            }
        }

        // now confirm that the due date is in the future if an alarm is being set
        LocalDate now = LocalDate.now();
        if (reminder.isChecked() && (now.isEqual(tempDueDate) || now.isAfter(tempDueDate))) {
            dueDate.setHelperText("Must be in future to turn on notification.");
            return false;
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createSaveButton() {
        saveButton = findViewById(R.id.asmSaveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                resetHelperText();

                if (validateAssessment()) {
                    UUID jobId = null;
                    if (reminder.isChecked()) {
                        jobId = notificationScheduler.scheduleNotification(LocalDate.parse(dueDate.getEditText().getText(), f),
                                "Scheduler Notification", "Assessment Due Date Alert.");
                    }

                    // This is a case where the assessment is being edited, and was set to send a reminder previous to this edit.
                    // So we need to cancel that reminder.
                    if (existingAssessment != null && existingAssessment.getJobId() != null) {
                        notificationScheduler.cancelNotification(existingAssessment.getJobId());
                    }

                    int type = assessmentType.getCheckedRadioButtonId();
                    MaterialRadioButton selectedType = findViewById(type);

                    Assessment savedAssessment = new Assessment(name.getEditText().getText().toString(),
                            courseId,
                            LocalDate.parse(dueDate.getEditText().getText(), f),
                            reminder.isChecked(),
                            (selectedType == objButton ? "objective" : "performance"),
                            jobId);

                    if (existingAssessment == null) {
                        //sharedViewModel.createAssessment(savedAssessment);
                    } else {
                        savedAssessment.setId(existingAssessment.getId());
                        //sharedViewModel.updateAssessment(savedAssessment);
                    }

                    Intent replyIntent = new Intent();
                    replyIntent.putExtra("assessment", savedAssessment);
                    replyIntent.putExtra("position", existingPosition);
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
            }
        });
    }
}
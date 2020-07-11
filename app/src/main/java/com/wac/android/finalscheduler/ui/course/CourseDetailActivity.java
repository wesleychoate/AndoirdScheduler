package com.wac.android.finalscheduler.ui.course;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.wac.android.finalscheduler.ui.assessment.AssessmentDetailActivity;
import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Assessment;
import com.wac.android.finalscheduler.entities.Course;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.ui.assessment.AssessmentListAdapter;
import com.wac.android.finalscheduler.ui.assessment.AssessmentSwipeDelete;
import com.wac.android.finalscheduler.util.ItemClickListener;
import com.wac.android.finalscheduler.util.NotificationScheduler;
import com.wac.android.finalscheduler.util.SharedViewModel;
import com.wac.android.finalscheduler.util.SharedViewModelFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.wac.android.finalscheduler.ui.mentor.MentorDetailActivity.MENTOR_REPLY;

public class CourseDetailActivity extends AppCompatActivity implements ItemClickListener {
    SharedViewModel sharedViewModel;

    private ConstraintLayout container;
    private AutoCompleteTextView cm;
    private AutoCompleteTextView courseStatus;
    private TextInputLayout startDate, endDate, note, name, mentor, status;
    private Button saveButton, addAssessment, deleteCourseButton;
    private ImageButton shareNoteButton;
    private SwitchMaterial startRem, endRem;

    private int termId;
    private Course existingCourse;

    private String pageTitle = "Create Course";

    private String[] STATUSES = new String[] {"In Progress", "Complete", "Dropped", "Plan To Take"};
    private ArrayAdapter<String> mentorAdapter;

    LiveData<List<Mentor>> mentors;
    List<String> mentorList;
    List<Integer> mentorIds;

    private ActionBar actionBar;

    private DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private Integer selectedMentorId;

    private AssessmentListAdapter assessmentListAdapter;
    private RecyclerView assessmentList;
    public static final int NEW_ASSESSMENT_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPD_ASSESSMENT_ACTIVITY_REQUEST_CODE = 2;

    private NotificationScheduler notificationScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        actionBar = getSupportActionBar();

        Intent inboundIntent = getIntent();
        termId = inboundIntent.getIntExtra("termId", -1);
        existingCourse = inboundIntent.getParcelableExtra("course");

        SharedViewModelFactory svmf = new SharedViewModelFactory(SharedViewModel.getInstance(getApplication()));
        sharedViewModel = new ViewModelProvider(this, svmf).get(SharedViewModel.class);
        notificationScheduler = new NotificationScheduler(this);

        container = findViewById(R.id.courseDetailContainer);
        name = findViewById(R.id.courseTitle);
        courseStatus = findViewById(R.id.courseStatus);
        cm = findViewById(R.id.courseMentor);
        startDate = findViewById(R.id.courseStartDate);
        endDate = findViewById(R.id.courseEndDate);
        note = findViewById(R.id.courseNotes);
        mentor = findViewById(R.id.courseMentorContainer);
        status = findViewById(R.id.courseStatusContainer);
        shareNoteButton = findViewById(R.id.shareButton);
        shareNoteButton.setEnabled(false);
        startRem = findViewById(R.id.courseStartDateNotification);
        endRem = findViewById(R.id.courseEndDateNotification);

        note.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shareNoteButton.setEnabled(s != null && s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setupCourseStatus(existingCourse);
        setupCourseMentor(existingCourse);
        setupAssessmentList();
        saveButtonMaker();

        if (existingCourse != null) {
            pageTitle = "Update Course";
            termId = existingCourse.getTermId();
            name.getEditText().setText(existingCourse.getTitle());
            if (existingCourse.getNote() != null) {
                note.getEditText().setText(existingCourse.getNote());
                shareNoteButton.setEnabled(true);
            }
            if (existingCourse.getStartDate() != null)
                startDate.getEditText().setText(f.format(existingCourse.getStartDate()));
            if (existingCourse.getEndDate() != null)
                endDate.getEditText().setText(f.format(existingCourse.getEndDate()));
            startRem.setChecked(existingCourse.getStartDateAlert());
            endRem.setChecked(existingCourse.getEndDateAlert());
            addAssessmentButtonMaker();
            deleteButtonMaker();
        }

        if (actionBar != null) {
            actionBar.setTitle(pageTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupCourseStatus(Course course) {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        STATUSES);
        courseStatus = findViewById(R.id.courseStatus);
        courseStatus.setAdapter(adapter);
        if (course != null && course.getStatus() != null)
            courseStatus.setText(course.getStatus(), false);
    }

    private void setupCourseMentor(Course course) {
        mentors = sharedViewModel.getMentors();
        mentorList = new ArrayList<>();
        mentorIds = new ArrayList<>();

        mentorAdapter =
                new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, mentorList);
        cm.setAdapter(mentorAdapter);

        sharedViewModel.getMentors().observe(this, new Observer<List<Mentor>>() {
            @Override
            public void onChanged(@Nullable final List<Mentor> mentors) {
                for (Mentor mentor : mentors) {
                    if (course != null && course.getMentorId() == mentor.getId()) {
                        cm.setText(mentor.getName(), false);
                        selectedMentorId = mentor.getId();
                    }
                    mentorList.add(mentor.getName());
                    mentorIds.add(mentor.getId());
                }
                mentorAdapter.notifyDataSetChanged();
            }
        });

        cm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                selectedMentorId = mentorIds.get(position);
            }
        });
    }

    private void saveButtonMaker() {
        if (saveButton == null) { // initial work on button
            saveButton = new MaterialButton(this);
            saveButton.setText((existingCourse == null ? "Save Then Add Assessments" : "Save Course"));
            saveButton.setId(View.generateViewId());

            container.addView(saveButton);

            ConstraintSet constraints = new ConstraintSet();
            constraints.clone(container);
            constraints.connect(saveButton.getId(), ConstraintSet.TOP, note.getId(), ConstraintSet.BOTTOM, 20);
            constraints.connect(saveButton.getId(), ConstraintSet.START, container.getId(), ConstraintSet.START, 20);
            constraints.applyTo(container);
        }

        saveButton.setOnClickListener(view -> {
            resetHelperText(); // clear any prior errors

            if (validateCourse()) {

                UUID jobIdSDN = null, jobIdEDN = null;

                if(startRem.isChecked())
                    jobIdSDN = notificationScheduler
                            .scheduleNotification(LocalDate.parse(startDate.getEditText().getText(), f),
                                    "Scheduler Notification", "Course Start Alert");
                if (existingCourse != null && existingCourse.getStartJobId() != null)
                    notificationScheduler.cancelNotification(existingCourse.getStartJobId());

                if(endRem.isChecked())
                    jobIdEDN = notificationScheduler
                            .scheduleNotification(LocalDate.parse(endDate.getEditText().getText(), f),
                                    "Scheduler Notification", "Course End Alert");
                if (existingCourse != null && existingCourse.getEndJobId() != null)
                    notificationScheduler.cancelNotification(existingCourse.getEndJobId());

                Course courseToSave = new Course(name.getEditText().getText().toString(),
                        LocalDate.parse(startDate.getEditText().getText(), f),
                        LocalDate.parse(endDate.getEditText().getText(), f),
                        status.getEditText().getText().toString(),
                        termId,
                        startRem.isChecked(),
                        endRem.isChecked(),
                        selectedMentorId,
                        note.getEditText().getText().toString(),
                        jobIdSDN, jobIdEDN);

                if (existingCourse == null) { // new term
                    long newCourseId = sharedViewModel.createCourse(courseToSave);
                    courseToSave.setId((int) newCourseId);
                    saveButton.setText("Save Course");
                    if (actionBar != null) actionBar.setTitle("Update Course");
                } else { // update existing course and close
                    courseToSave.setId(existingCourse.getId());
                    sharedViewModel.updateCourse(courseToSave);
                    Intent replyIntent = new Intent();
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
                existingCourse = courseToSave;
                addAssessmentButtonMaker();
                deleteButtonMaker();
            }
        });
    }

    private void addAssessmentButtonMaker() {
        addAssessment = new MaterialButton(this);
        addAssessment.setText("Add Assessment");
        addAssessment.setId(View.generateViewId());

        addAssessment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AssessmentDetailActivity.class);
                intent.putExtra("courseId", existingCourse.getId());
                startActivityForResult(intent, NEW_ASSESSMENT_ACTIVITY_REQUEST_CODE);
            }
        });
        container.addView(addAssessment);

        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(container);
        constraints.connect(addAssessment.getId(), ConstraintSet.START, container.getId(), ConstraintSet.START, 20);
        constraints.connect(addAssessment.getId(), ConstraintSet.TOP, saveButton.getId(), ConstraintSet.BOTTOM, 20);
        constraints.applyTo(container);
    }

    private void setupAssessmentList() {
        assessmentListAdapter = new AssessmentListAdapter(this);
        assessmentList = new RecyclerView(this);
        assessmentList.setId(View.generateViewId());
        assessmentList.setHasFixedSize(true);
        assessmentList.setLayoutManager(new LinearLayoutManager(this));
        assessmentList.setAdapter(assessmentListAdapter);

        assessmentListAdapter.setClickListener(this);

        new ItemTouchHelper(new AssessmentSwipeDelete(this, sharedViewModel, assessmentListAdapter, 0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)).attachToRecyclerView(assessmentList);

        sharedViewModel.getAssessments().observe(this, assessments -> {
            int courseId = (existingCourse == null ? -1 : existingCourse.getId());
            assessmentListAdapter.setAssessments(assessments.stream()
                    .filter(assessment -> assessment.getCourseId() == courseId)
                    .collect(Collectors.toList()));
            //assessmentListAdapter.setAssessments(assessments);
            if (container.getViewById(assessmentList.getId()) == null && existingCourse != null) {
                container.addView(assessmentList);

                assessmentList.setLayoutParams(new ConstraintLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                ConstraintSet constraints = new ConstraintSet();
                constraints.clone(container);
                constraints.connect(assessmentList.getId(), ConstraintSet.START, container.getId(), ConstraintSet.START, 20);
                constraints.connect(assessmentList.getId(), ConstraintSet.TOP, addAssessment.getId(), ConstraintSet.BOTTOM, 20);
                constraints.applyTo(container);
            }});
    }

    private void resetHelperText() {
        name.setHelperText("");
        startDate.setHelperText("mm/dd/yyyy");
        endDate.setHelperText("mm/dd/yyyy");
        mentor.setHelperText("");
        status.setHelperText("");
    }

    private boolean validateCourse() {
        if (TextUtils.isEmpty(name.getEditText().getText())) {
            name.setHelperText("Course Name is required");
            return false;
        }

        LocalDate tempStartDate;
        if (TextUtils.isEmpty(startDate.getEditText().getText())) {
            startDate.setHelperText("Start date is required. mm/dd/yyyy format.");
            return false;
        } else {
            try {
                tempStartDate = f.parse(startDate.getEditText().getText().toString(), LocalDate::from);
            } catch (DateTimeParseException e) {
                startDate.setHelperText("Invalid date. mm/dd/yyyy required format.");
                return false;
            }
        }

        LocalDate tempEndDate;
        if (TextUtils.isEmpty(endDate.getEditText().getText())) {
            endDate.setHelperText("Projected end date is required. mm/dd/yyyy format.");
            return false;
        } else {
            try {
                tempEndDate = f.parse(endDate.getEditText().getText().toString(), LocalDate::from);
            } catch (DateTimeParseException e) {
                endDate.setHelperText("Invalid date. mm/dd/yyyy required format.");
                return false;
            }
        }

        if (TextUtils.isEmpty(status.getEditText().getText())) {
            status.setHelperText("Status is required.");
        }

        if (TextUtils.isEmpty(mentor.getEditText().getText())) {
            mentor.setHelperText("Mentor is required.");
        }

        LocalDate now = LocalDate.now();
        if (startRem.isChecked() && (now.isEqual(tempStartDate) || now.isAfter(tempStartDate))) {
            startDate.setHelperText("Must be in future to turn on notification.");
            return false;
        }
        if (endRem.isChecked() && (now.isEqual(tempEndDate) || now.isAfter(tempEndDate))) {
            endDate.setHelperText("Must be in future to turn on notification.");
            return false;
        }

        if (tempEndDate.isBefore(tempStartDate)) {
            endDate.setHelperText("End date must be after start date.");
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

    @Override
    public void onClick(View view, int position) {
        Assessment clickedAssessment = assessmentListAdapter.getAssessmentAtPosition(position);
        Intent intent = new Intent(this, AssessmentDetailActivity.class);
        intent.putExtra("assessment", clickedAssessment);
        intent.putExtra("position", position);
        startActivityForResult(intent, UPD_ASSESSMENT_ACTIVITY_REQUEST_CODE);
    }

    public void shareNote(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, note.getEditText().getText());
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Course Notes");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void deleteButtonMaker() {
        if (deleteCourseButton == null) { // first time button creation
            deleteCourseButton = new MaterialButton(this);
            deleteCourseButton.setText("Delete Course");
            deleteCourseButton.setId(View.generateViewId());

            container.addView(deleteCourseButton); // add the delete button to the container

            ConstraintSet constraints = new ConstraintSet();
            constraints.clone(container);
            constraints.connect(deleteCourseButton.getId(), ConstraintSet.TOP, note.getId(), ConstraintSet.BOTTOM, 20);
            constraints.connect(deleteCourseButton.getId(), ConstraintSet.START, saveButton.getId(), ConstraintSet.END, 20);
            constraints.applyTo(container);
        }

        deleteCourseButton.setEnabled(existingCourse != null);

        deleteCourseButton.setOnClickListener(view -> {
            Log.d("course", "Deleting course.");
            sharedViewModel.deleteCourse(existingCourse);
            Intent replyIntent = new Intent();
            setResult(RESULT_OK, replyIntent);
            finish();
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);
            Assessment assessmentToSave = data.getParcelableExtra("assessment");
            if (assessmentToSave.getId() != null && assessmentToSave.getId() > 0) {
                sharedViewModel.updateAssessment(assessmentToSave);
            } else {
                sharedViewModel.createAssessment(assessmentToSave);
            }

            if (position > 0) {
              assessmentListAdapter.refreshAssessments();
            }
        }
    }
}
package com.wac.android.finalscheduler.ui.term;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Course;
import com.wac.android.finalscheduler.entities.Term;
import com.wac.android.finalscheduler.ui.course.CourseDetailActivity;
import com.wac.android.finalscheduler.ui.course.CourseListAdapter;
import com.wac.android.finalscheduler.ui.course.CourseSwipeDelete;
import com.wac.android.finalscheduler.util.ItemClickListener;
import com.wac.android.finalscheduler.util.SharedViewModel;
import com.wac.android.finalscheduler.util.SharedViewModelFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

public class TermDetailActivity extends AppCompatActivity implements ItemClickListener {
    private SharedViewModel sharedViewModel;

    private DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private String pageTitle = "Create Term";

    private TextInputLayout termTitle, termStartDate, termEndDate;
    private ConstraintLayout container;

    private MaterialButton saveButton, deleteButton, addCourse;

    private Term existingTerm;

    private CourseListAdapter courseListAdapter;
    private RecyclerView courseList;

    ActionBar actionBar;

    public static final int NEW_COURSE_ACTIVITY_REQUEST_CODE = 10;
    public static final int UPD_COURSE_ACTIVITY_REQUEST_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_detail);

        SharedViewModelFactory svmf = new SharedViewModelFactory(SharedViewModel.getInstance(this.getApplication()));
        sharedViewModel = new ViewModelProvider(this, svmf).get(SharedViewModel.class);

        actionBar = getSupportActionBar();
        Intent inboundIntent = getIntent();

        existingTerm = inboundIntent.getParcelableExtra("term");

        termTitle = findViewById(R.id.termTitle);
        termStartDate = findViewById(R.id.termStartDate);
        termEndDate = findViewById(R.id.termEndDate);

        container = findViewById(R.id.termConstraintLayout);

        saveButtonMaker();

        if (existingTerm != null) {
            pageTitle = "Update Term";
            termTitle.getEditText().setText(existingTerm.getTitle());
            if (existingTerm.getStartDate() != null)
                termStartDate.getEditText().setText(f.format(existingTerm.getStartDate()));
            if (existingTerm.getEndDate() != null)
                termEndDate.getEditText().setText(f.format(existingTerm.getEndDate()));
            addCourseButtonMaker();
            makeTheCourseList();
            deleteButtonMaker();
        }

        if (actionBar != null) {
            actionBar.setTitle(pageTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void saveButtonMaker() {
        if (saveButton == null) { // initial work on button
            saveButton = new MaterialButton(this);
            saveButton.setText((existingTerm == null ? "Save Then Add Courses" : "Save Term"));
            saveButton.setId(View.generateViewId());

            container.addView(saveButton);

            ConstraintSet constraints = new ConstraintSet();
            constraints.clone(container);
            constraints.connect(saveButton.getId(), ConstraintSet.TOP, termEndDate.getId(), ConstraintSet.BOTTOM, 20);
            constraints.connect(saveButton.getId(), ConstraintSet.START, container.getId(), ConstraintSet.START, 20);
            constraints.applyTo(container);
        }

        saveButton.setOnClickListener(view -> {
            Log.d("term", "Clicked on save term button. Term exists: " + (existingTerm != null));
            resetHelperText(); // clear any prior errors
            if (validateTerm()) {
                Term termToSave = new Term(termTitle.getEditText().getText().toString(),
                        LocalDate.parse(termStartDate.getEditText().getText(), f),
                        LocalDate.parse(termEndDate.getEditText().getText(), f));
                if (existingTerm == null) { // new term
                    long newTermId = sharedViewModel.createTerm(termToSave);
                    termToSave.setId((int) newTermId);
                    saveButton.setText("Save Term");
                    if (actionBar != null) actionBar.setTitle("Update Term");
                } else { // update existing term and close
                    termToSave.setId(existingTerm.getId());
                    sharedViewModel.updateTerm(termToSave);
                    Intent replyIntent = new Intent();
                    setResult(RESULT_OK, replyIntent);
                    finish();
                }
                existingTerm = termToSave;
                addCourseButtonMaker();
                makeTheCourseList();
                deleteButtonMaker();
            }
        });
    }

    private void deleteButtonMaker() {
        if (deleteButton == null) { // initial work on button
            deleteButton = new MaterialButton(this);
            deleteButton.setText("Delete Term");
            deleteButton.setId(View.generateViewId());

            container.addView(deleteButton);

            ConstraintSet constraints = new ConstraintSet();
            constraints.clone(container);
            constraints.connect(deleteButton.getId(), ConstraintSet.START, saveButton.getId(), ConstraintSet.END, 20);
            constraints.connect(deleteButton.getId(), ConstraintSet.TOP, termEndDate.getId(), ConstraintSet.BOTTOM, 20);
            constraints.applyTo(container);
        }

        deleteButton.setEnabled(existingTerm != null);

        deleteButton.setOnClickListener(view -> {
            if (courseListAdapter.getItemCount() > 0) {
                Context context = getApplicationContext();
                CharSequence text = "Delete Failed: Must Remove Courses First";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                Log.d("term", "Clicked on delete term button.");
                sharedViewModel.deleteTerm(existingTerm);
                Intent replyIntent = new Intent();
                setResult(RESULT_OK, replyIntent);
                finish();
            }
        });
    }

    private void makeTheCourseList() {
        courseListAdapter = new CourseListAdapter(this);
        courseList = new RecyclerView(this);
        courseList.setId(View.generateViewId());
        courseList.setHasFixedSize(false);
        courseList.setLayoutManager(new LinearLayoutManager(this));
        courseList.setAdapter(courseListAdapter);

        courseListAdapter.setClickListener(this);

        GradientDrawable border = new GradientDrawable();
        border.setColor(0xFF00000); //black background
        border.setStroke(1, 0xFFFFFFFF); //white border with full opacity
        courseList.setBackground(border);

        // For swipes to delete
        new ItemTouchHelper(new CourseSwipeDelete(this, sharedViewModel, courseListAdapter, 0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)).attachToRecyclerView(courseList);

        sharedViewModel.getCourses().observe(this, courses -> {
            courseListAdapter.setCourses(courses.stream()
                    .filter(course -> course.getTermId() == existingTerm.getId()).collect(Collectors.toList()));
        });

        container.addView(courseList);

        courseList.setLayoutParams(new ConstraintLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(container);
        constraints.connect(courseList.getId(), ConstraintSet.START, container.getId(), ConstraintSet.START, 20);
        constraints.connect(courseList.getId(), ConstraintSet.TOP, addCourse.getId(), ConstraintSet.BOTTOM, 20);
        constraints.applyTo(container);
    }

    private void addCourseButtonMaker() {
        addCourse = new MaterialButton(this);
        addCourse.setText("Add Course");
        addCourse.setId(View.generateViewId());

        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseDetailActivity.class);
                intent.putExtra("termId", existingTerm.getId());
                startActivityForResult(intent, NEW_COURSE_ACTIVITY_REQUEST_CODE);
            }
        });
        container.addView(addCourse);

        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(container);
        constraints.connect(addCourse.getId(), ConstraintSet.TOP, saveButton.getId(), ConstraintSet.BOTTOM, 20);
        constraints.connect(addCourse.getId(), ConstraintSet.START, container.getId(), ConstraintSet.START, 20);
        constraints.applyTo(container);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetHelperText() {
        termTitle.setHelperText("");
        termStartDate.setHelperText("mm/dd/yyyy");
        termEndDate.setHelperText("mm/dd/yyyy");
    }

    private boolean validateTerm() {
        if (TextUtils.isEmpty(termTitle.getEditText().getText())) {
            termTitle.setHelperText("Title is required");
            return false;
        }

        LocalDate tsd;
        if (TextUtils.isEmpty(termStartDate.getEditText().getText())) {
            termStartDate.setHelperText("Start date is required. mm/dd/yyyy format.");
            return false;
        } else {
            try {
                tsd = f.parse(termStartDate.getEditText().getText().toString(), LocalDate::from);
            } catch (DateTimeParseException e) {
                termStartDate.setHelperText("Invalid date. mm/dd/yyyy required format.");
                return false;
            }
        }

        LocalDate ted;
        if (TextUtils.isEmpty(termEndDate.getEditText().getText())) {
            termEndDate.setHelperText("End date is required. mm/dd/yyyy format.");
            return false;
        } else {
            try {
                ted = f.parse(termEndDate.getEditText().getText().toString(), LocalDate::from);
            } catch (DateTimeParseException e) {
                termEndDate.setHelperText("Invalid date. mm/dd/yyyy required format.");
                return false;
            }
        }

        if (ted.isBefore(tsd)) {
            termEndDate.setHelperText("End date must be after start date.");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View view, int position) {
        Course clickedCourse = courseListAdapter.getCourseAtPosition(position);
        Log.i("Editing", "Clicked on course " + clickedCourse.getTitle());
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("course", clickedCourse);
        startActivityForResult(intent, UPD_COURSE_ACTIVITY_REQUEST_CODE);
    }
}
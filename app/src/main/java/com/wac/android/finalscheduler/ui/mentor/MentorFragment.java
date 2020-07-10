package com.wac.android.finalscheduler.ui.mentor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.adapter.MentorListAdapter;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.util.*;

import static android.app.Activity.RESULT_OK;
import static com.wac.android.finalscheduler.ui.mentor.MentorDetailActivity.MENTOR_REPLY;

public class MentorFragment extends Fragment implements ItemClickListener {

    private SharedViewModel mentorViewModel;
    private MentorListAdapter mentorListAdapter;

    public static final int NEW_MENTOR_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPD_MENTOR_ACTIVITY_REQUEST_CODE = 2;

    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedViewModelFactory svmf = new SharedViewModelFactory(SharedViewModel.getInstance(getActivity().getApplication()));
        mentorViewModel = new ViewModelProvider(this, svmf).get(SharedViewModel.class);

        View view = inflater.inflate(R.layout.fragment_mentor, container, false);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.recyclerview);
        mentorListAdapter = new MentorListAdapter(view.getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(mentorListAdapter);

        // List for clicks
        mentorListAdapter.setClickListener(this);

        // For swipes to delete
        new ItemTouchHelper(new MentorSwipeDelete(view.getContext(), mentorViewModel, mentorListAdapter, 0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)).attachToRecyclerView(recyclerView);

        mentorViewModel.getMentors().observe(getViewLifecycleOwner(), mentors -> {
            // Update the cached copy of the words in the adapter.
            mentorListAdapter.setMentors(mentors);
        });

        mentorViewModel.getCourses().observe(getViewLifecycleOwner(), courses -> {
            mentorListAdapter.setCourses(courses);
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), MentorDetailActivity.class);
            startActivityForResult(intent, NEW_MENTOR_ACTIVITY_REQUEST_CODE);
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Mentor mentor = (data.getParcelableExtra(MENTOR_REPLY));
            if (mentor != null) {
                if (requestCode == NEW_MENTOR_ACTIVITY_REQUEST_CODE) {
                    mentorViewModel.createMentor(mentor);
                } else if (requestCode == UPD_MENTOR_ACTIVITY_REQUEST_CODE) {
                    mentorViewModel.updateMentor(mentor);
                }
            }
        } else {

        }
    }

    @Override
    public void onClick(View view, int position) {
        final Mentor m = mentorListAdapter.getMentorAtPosition(position);
        Log.i("Editing", m.getName());
        Intent intent = new Intent(getActivity(), MentorDetailActivity.class);
        intent.putExtra("mentor", m);
        startActivityForResult(intent, UPD_MENTOR_ACTIVITY_REQUEST_CODE);
    }
}

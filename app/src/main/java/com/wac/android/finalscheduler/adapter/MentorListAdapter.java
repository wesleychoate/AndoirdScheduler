package com.wac.android.finalscheduler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Course;
import com.wac.android.finalscheduler.entities.Mentor;
import com.wac.android.finalscheduler.util.ItemClickListener;

import java.util.List;

public class MentorListAdapter extends RecyclerView.Adapter<MentorItemHolder> {
    private final LayoutInflater inflater;
    private List<Mentor> mentors;
    private List<Course> courses;

    private Mentor recentlyDeletedMentor;
    private int recentlyDeletedMentorPosition;
    private Context context;
    private ItemClickListener clickListener;

    public MentorListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MentorItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mentor, parent, false);
        return new MentorItemHolder(v, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MentorItemHolder holder, int position) {
        if (mentors != null) {
            Mentor cur = mentors.get(position);
            holder.mentorNameView.setText(cur.getName());
        } else {
            holder.mentorNameView.setText("No mentors defined.");
        }
    }

    public void setMentors(List<Mentor> mentors) {
        this.mentors = mentors;
        notifyDataSetChanged();
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }
    public void refreshMentors() {
        notifyDataSetChanged();
    }

    public long courseCountForMentor(Integer mentorId) {
        return this.courses.stream()
                .filter(course -> course.getMentorId() == mentorId)
                .count();
    }
    @Override
    public int getItemCount() {
        return mentors != null ? mentors.size() : 0;
    }

    public Mentor getMentorAtPosition(int position) {
        return mentors.get(position);
    }

    public void deleteMentor(int position) {
        recentlyDeletedMentor = mentors.get(position);
        recentlyDeletedMentorPosition = position;
        mentors.remove(position);
        notifyItemRemoved(position);
    }
}

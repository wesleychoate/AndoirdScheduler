package com.wac.android.finalscheduler.ui.course;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Course;
import com.wac.android.finalscheduler.entities.Term;
import com.wac.android.finalscheduler.util.ItemClickListener;

import org.w3c.dom.Text;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder> {
    private final LayoutInflater inflater;
    private List<Course> courses;
    private Context context;
    private ItemClickListener clickListener;
    private DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public CourseListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CourseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseListViewHolder holder, int position) {
        if (courses != null) {
            Course course = courses.get(position);
            holder.getNameStatusView().setText(course.getTitle() + " - " + course.getStatus());
        } else {
            holder.getNameStatusView().setText("No courses defined.");
        }
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    public Course getCourseAtPosition(int position) {
        return courses.get(position);
    }

    public void deleteTerm(int position) {
        courses.remove(position);
        notifyItemRemoved(position);
    }

    class CourseListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView courseNameStatusView;

        public CourseListViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameStatusView = itemView.findViewById(R.id.item_course_title_status);
            courseNameStatusView.setOnClickListener(this);
        }

        public TextView getNameStatusView() {
            return courseNameStatusView;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }
}

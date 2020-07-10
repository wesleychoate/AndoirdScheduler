package com.wac.android.finalscheduler.ui.assessment;

import android.content.ClipData;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Assessment;
import com.wac.android.finalscheduler.util.ItemClickListener;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssessmentListAdapter extends RecyclerView.Adapter<AssessmentListAdapter.AssessmentListViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Assessment> assessments;
    private ItemClickListener clickListener;
    private DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public AssessmentListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @NonNull
    @Override
    public AssessmentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_assessment, parent, false);
        return new AssessmentListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AssessmentListViewHolder holder, int position) {
        if (assessments != null && assessments.size() > 0) {
            Assessment assessment = assessments.get(position);
            holder.getAssessmentNameView().setText(assessment.getTitle());
        } else {
            holder.getAssessmentNameView().setText("No assessment.");
        }
    }

    public void setAssessments(List<Assessment> assessments) {
        this.assessments = assessments;
        notifyDataSetChanged();
    }

    public Assessment getAssessmentAtPosition(int position) { return assessments.get(position); }

    public void refreshAssessments() {
       notifyDataSetChanged();
    }

    public void deleteAssessment(int position) {
        assessments.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return assessments != null ? assessments.size() : 0;
    }

    class AssessmentListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView assessmentNameView;

        public AssessmentListViewHolder(@NonNull View itemView) {
            super(itemView);
            assessmentNameView = itemView.findViewById(R.id.item_assessment_name);
            assessmentNameView.setOnClickListener(this);
        }

        public TextView getAssessmentNameView() { return assessmentNameView; }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }
}

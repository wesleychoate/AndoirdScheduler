package com.wac.android.finalscheduler.adapter;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.util.ItemClickListener;

public class MentorItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected TextView mentorNameView;
    RelativeLayout parent;
    private View.OnClickListener onItemClickListener;
    private ItemClickListener clickListener;

    public MentorItemHolder(@NonNull View itemView, ItemClickListener clickListener) {
        super(itemView);
        itemView.setTag(this);
        mentorNameView = itemView.findViewById(R.id.item_mentor_name);
        parent = itemView.findViewById(R.id.parent);
        itemView.setOnClickListener(this);
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view, getAdapterPosition());
    }

    public TextView getView() {
        return mentorNameView;
    }
}

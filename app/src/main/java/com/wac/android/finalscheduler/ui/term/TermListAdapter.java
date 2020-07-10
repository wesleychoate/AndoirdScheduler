package com.wac.android.finalscheduler.ui.term;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Term;
import com.wac.android.finalscheduler.util.ItemClickListener;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TermListAdapter extends RecyclerView.Adapter<TermListAdapter.TermListViewHolder> {
    private final LayoutInflater inflater;
    private List<Term> terms;
    private Context context;
    private ItemClickListener clickListener;
    private DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public TermListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @NonNull
    @Override
    public TermListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_term, parent, false);
        return new TermListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TermListViewHolder holder, int position) {
        if (terms != null) {
            Term term = terms.get(position);
            StringBuilder termString = new StringBuilder();
            termString.append(term.getTitle() + "\n");
            termString.append(f.format(term.getStartDate()) + " - ");
            termString.append(f.format(term.getEndDate()));
            holder.getView().setText(termString.toString());
        } else {
            holder.getView().setText("No terms defined.");
        }
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return terms != null ? terms.size() : 0;
    }

    public Term getTermAtPosition(int position) {
        return terms.get(position);
    }

    public void deleteTerm(int position) {
        terms.remove(position);
        notifyItemRemoved(position);
    }

    class TermListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView termNameView;

        public TermListViewHolder(@NonNull View itemView) {
            super(itemView);
            termNameView = itemView.findViewById(R.id.item_term_title);
            termNameView.setOnClickListener(this);
        }

        public TextView getView() {
            return termNameView;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getAdapterPosition());
        }
    }
}

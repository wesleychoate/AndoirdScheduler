package com.wac.android.finalscheduler.ui.term;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wac.android.finalscheduler.R;
import com.wac.android.finalscheduler.entities.Term;
import com.wac.android.finalscheduler.util.ItemClickListener;
import com.wac.android.finalscheduler.util.SharedViewModel;
import com.wac.android.finalscheduler.util.SharedViewModelFactory;

public class TermListFragment extends Fragment implements ItemClickListener {

    private SharedViewModel sharedViewModel;
    private RecyclerView termListView;
    private TermListAdapter termListAdapter;

    public static final int NEW_TERM_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPD_TERM_ACTIVITY_REQUEST_CODE = 2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedViewModelFactory svmf = new SharedViewModelFactory(SharedViewModel.getInstance(getActivity().getApplication()));
        sharedViewModel = new ViewModelProvider(this, svmf).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_termlist, container, false);

        termListView = root.findViewById(R.id.termlistcontainer);
        termListAdapter = new TermListAdapter(root.getContext());
        termListView.setHasFixedSize(true);
        termListView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        termListView.setAdapter(termListAdapter);

        termListAdapter.setClickListener(this);

        sharedViewModel.getTerms().observe(getViewLifecycleOwner(), terms -> {
            termListAdapter.setTerms(terms);
        });

        FloatingActionButton fab = root.findViewById(R.id.termlist_fab);
        fab.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), TermDetailActivity.class);
            startActivityForResult(intent, NEW_TERM_ACTIVITY_REQUEST_CODE);
        });

        return root;
    }

    @Override
    public void onClick(View view, int position) {
        Term clickedTerm = termListAdapter.getTermAtPosition(position);
        Log.i("Editing", "Clicked on term " + clickedTerm.getTitle());
        Intent intent = new Intent(getActivity(), TermDetailActivity.class);
        intent.putExtra("term", clickedTerm);
        startActivityForResult(intent, UPD_TERM_ACTIVITY_REQUEST_CODE);
    }
}
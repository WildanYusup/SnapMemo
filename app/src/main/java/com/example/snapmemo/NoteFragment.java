package com.example.snapmemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

public class NoteFragment extends Fragment {

    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        addNoteBtn = view.findViewById(R.id.add_note_btn);
        recyclerView = view.findViewById(R.id.recyler_view);

        addNoteBtn.setOnClickListener((v) -> startActivity(new Intent(getActivity(), NoteDetailsActivity.class)));
        setupRecyclerView();
        return view;
    }


    void setupRecyclerView(){
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        noteAdapter = new NoteAdapter(options, requireContext());
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    public void onStop(){
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    public void onResume(){
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}
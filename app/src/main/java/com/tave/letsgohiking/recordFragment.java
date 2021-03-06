package com.tave.letsgohiking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import static com.tave.letsgohiking.RecordAdapter.items;


public class recordFragment extends Fragment {

    public static RecordAdapter adapter = new RecordAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record, container, false);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setHasFixedSize(true);

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    //fragment 갱신
    private void refresh(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        //items= recyclerview에 들어가는 아이템 arraylist

//        items.get(position);
//        int all_runningNum = items.size();
//        EditText all_Running = getView().findViewById(R.id.all_Running);
//        all_Running.setText("총 러닝횟수: " + all_runningNum);

        TextView textView_length= getView().findViewById(R.id.textView_length);
        String all_RecordKm;
        EditText all_Record= getView().findViewById(R.id.all_Record);
        all_Record.setText("총 러닝 거리: ");

    }
}





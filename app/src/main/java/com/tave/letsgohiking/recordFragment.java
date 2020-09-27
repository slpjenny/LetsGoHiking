package com.tave.letsgohiking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class recordFragment extends Fragment {
    RecordAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

//        recyclerView.setHasFixedSize(true);

        adapter = new RecordAdapter();

        adapter.addItem(new recordObject("20200926", "1번째 기록", "5km", "2시간"));  //date는 안나옴. 다른것만 배치 이상하게 뜸.
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext())); //이거 없애면 아예 리싸이클러뷰 안나타남.

        return view;

    }

}





package com.tave.letsgohiking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext())); //이거 없애면 리싸이클러뷰 안나타남.
        recyclerView.setHasFixedSize(true);

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //items= recyclerview에 들어가는 아이템 arraylist
//        int all_runningNum;



    }

    //fragment 갱신
    private void refresh(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        //setText UI가 업데이트 되지 않고있다.
        int all_runningNum = items.size();
        EditText all_Running = getView().findViewById(R.id.all_Running);
        all_Running.setText("총 러닝횟수" + " " + all_runningNum);

        Log.d("runnin", String.valueOf(all_runningNum));

    }
}





package com.tave.letsgohiking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecordAdapter extends  RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    ArrayList<recordObject> items = new ArrayList<recordObject>();

    public void addItem(recordObject item) {
        items.add(item);
    }

    public void setItems(ArrayList<recordObject> items) {
        this.items = items;
    }

    public recordObject getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, recordObject item) {
        items.set(position, item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.record_item, parent, false);

        return new RecordAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        recordObject item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView_date;
        TextView textView_title;
        TextView textView_length;
        TextView textView_time;

        public ViewHolder(final View itemView) {
            super(itemView);

            textView_date = itemView.findViewById(R.id.textView_date);
            textView_title = itemView.findViewById(R.id.textView_title);
            textView_length = itemView.findViewById(R.id.textView_length);
            textView_time = itemView.findViewById(R.id.textView_time);


            //viewholder 안에서 전달받은 뷰를 클릭했을떄~ listener쪽으로 전달할 수 있다.
            //각각의item뷰가 클릭되었을때~ 인터페이스로 만든 함수 호출

        }

        public void setItem(recordObject item) {
            textView_date.setText(item.getDate());
            textView_title.setText(item.getTitle());
            textView_length.setText(item.getLength());
            textView_time.setText(item.getTime());

        }

    }

}
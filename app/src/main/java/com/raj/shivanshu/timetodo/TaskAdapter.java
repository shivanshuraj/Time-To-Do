package com.raj.shivanshu.timetodo;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.raj.shivanshu.timetodo.database.AppDatabase;
import com.raj.shivanshu.timetodo.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shivanshu Raj on 20-08-2022.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    private static final String TAG = "TaskAdapter";
    LayoutInflater mInflater;
    List<TaskEntry> tasks;
    Context context;
    ItemClickListener mClickListener;
    boolean multiSelectState;
    List<TaskEntry> selectedTasks = new ArrayList<>();

    public TaskAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called");
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");
        TaskEntry currentTaskEntry = tasks.get(position);
        holder.titleTv.setText(currentTaskEntry.getTitle());
        Date date = currentTaskEntry.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        holder.dateTv.setText(simpleDateFormat.format(date));
        holder.priorityView.setColorFilter(getColorFromPriority(currentTaskEntry.getPriority()));
        if (currentTaskEntry.isSelected()) {
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.secondaryColor));
        } else {
            //danger. color has wrong reference
            holder.rootLayout.setBackgroundColor(context.getResources().getColor(R.color.primaryTextColor));

        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        if (tasks == null) {
            return 0;
        }
        return tasks.size();
    }

    public List<TaskEntry> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskEntry> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    private int getColorFromPriority(int priority) {
        int color = Color.rgb(204, 0, 0);

        switch (priority) {
            case 1:
                color = Color.rgb(204, 0, 0);
                break;
            case 2:
                color = Color.rgb(255, 109, 0);
                break;
            case 3:
                color = Color.rgb(255, 214, 0);
        }
        return color;
    }

    public void setClickListener(ItemClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int itemId);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView titleTv;
        TextView dateTv;
        ImageView priorityView;
        LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.task_title_tv);
            dateTv = itemView.findViewById(R.id.date_text_view);
            priorityView = itemView.findViewById(R.id.priority_view);
            rootLayout = itemView.findViewById(R.id.rootLinearLayout);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        AppDatabase database =AppDatabase.getInstance(context);

        @Override
        public void onClick(View v) {
            TaskEntry currentTaskEntry = tasks.get(getBindingAdapterPosition());
            if (multiSelectState) {
                if (!(currentTaskEntry.isSelected())) {
                    currentTaskEntry.setSelected(true);
                    notifyDataSetChanged();
                    Log.d(TAG, "onClick: " + currentTaskEntry.getTitle() + " is selected");
                    selectedTasks.add(currentTaskEntry);

                } else {
                    currentTaskEntry.setSelected(false);
                    notifyDataSetChanged();
                    Log.d(TAG, "onClick: " + currentTaskEntry.getTitle() + " is deselected");
                    selectedTasks.remove(currentTaskEntry);


                    if (selectedTasks.size() == 0) {
                        multiSelectState = false;
                    }
                }
            } else if (mClickListener != null) {
                int elementId = tasks.get(getBindingAdapterPosition()).getId();
                mClickListener.onItemClick(elementId);
            }


        }

        @Override
        public boolean onLongClick(View v) {
            multiSelectState = true;
            TaskEntry currentTaskEntry = tasks.get(getAbsoluteAdapterPosition());
            currentTaskEntry.setSelected(true);
            selectedTasks.add(currentTaskEntry);
            notifyDataSetChanged();
            return true;
        }
    }

    //to be used later...
//    public void deleteSelectedItems() {
//        for (int i = 0; i < arrayList.size(); i++) {
//            for (int j = 0; j < selectedItems.size(); j++) {
//                if(arrayList.get(i)==selectedItems.get(j)){
//                    arrayList.remove(i);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }

}

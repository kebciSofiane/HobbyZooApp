package com.example.hobbyzooapp.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.TodoTask;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<TodoTask> todoList;

    public TodoAdapter(List<TodoTask> todoList) {
        this.todoList = todoList;
    }

    // Méthode onCreateViewHolder pour créer la vue de chaque élément de la liste
    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_task, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        TodoTask item = todoList.get(position);
        holder.bind(item);
    }

    // Méthode getItemCount pour renvoyer le nombre d'éléments dans la liste
    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView taskNameTextView;
        private CheckBox taskStatusCheckbox;

        public TodoViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.taskNameTextView);
            taskStatusCheckbox = itemView.findViewById(R.id.taskStatusCheckbox);
        }

        public void bind(TodoTask item) {
            taskNameTextView.setText(item.getTaskname());
            taskStatusCheckbox.setChecked(item.isCompleted());

            taskStatusCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setCompleted(isChecked);
                }
            });
        }
    }
}
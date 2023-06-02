package com.example.hobbyzooapp.Activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.TodoTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;

    private final List<TodoTask> todoList;

    public TodoAdapter(List<TodoTask> todoList) {
        this.todoList = todoList;
    }

    @Override
    public int getItemViewType(int position) {
        if (todoList.isEmpty()) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = inflater.inflate(R.layout.item_empty_list, parent, false);
            return new EmptyViewHolder(emptyView);
        } else {
            View view = inflater.inflate(R.layout.todo_task, parent, false);
            return new TodoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TodoViewHolder) {
            TodoTask item = todoList.get(position);
            ((TodoViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        if (todoList.isEmpty()) {
            return 1; // Liste vide, afficher l'élément de mise en page vide
        } else {
            return todoList.size(); // Nombre d'éléments dans la liste
        }
    }

    public class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView taskNameTextView;
        private CheckBox taskStatusCheckbox;
        private Button deleteTaskButton;

        public TodoViewHolder(View itemView) {
            super(itemView);
            taskNameTextView = itemView.findViewById(R.id.taskNameTextView);
            taskStatusCheckbox = itemView.findViewById(R.id.taskStatusCheckbox);
            deleteTaskButton = itemView.findViewById(R.id.deleteTaskButton);

            deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Supprimer la tâche de la base de données
                    DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks");
                    Query query = tasksRef.orderByChild("taskName").equalTo(taskNameTextView.getText().toString());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String taskId = snapshot.getKey();
                                tasksRef.child(taskId).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Gestion de l'erreur, si nécessaire
                        }
                    });
                }
            });
        }

        public void bind(TodoTask item) {
            taskNameTextView.setText(item.getTaskname());
            taskStatusCheckbox.setChecked(item.isCompleted());
            taskStatusCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setCompleted(isChecked);
                    if (isChecked) {
                        updateDBTasks("TRUE", taskNameTextView.getText().toString());
                    } else {
                        updateDBTasks("FALSE", taskNameTextView.getText().toString());
                    }
                }
            });
        }

        private void updateDBTasks(String status, String taskName) {
            DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks");
            Query query = tasksRef.orderByChild("taskName").equalTo(taskName);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String taskId = snapshot.getKey();
                        DatabaseReference taskRef = tasksRef.child(taskId);
                        taskRef.child("taskStatus").setValue(status, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    System.out.println("Tasks modified!");
                                } else {
                                    System.err.println("Error: " + databaseError.getMessage());
                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error if needed
                }
            });
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}



package com.example.hobbyzooapp.Activities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

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

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private final List<TodoTask> todoList;

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
                            if (isChecked) {
                                updateDBTasks("TRUE",(String) taskNameTextView.getText());
                            } else {
                                updateDBTasks("FALSE",(String) taskNameTextView.getText());

                            }
                        }
                    });
        }


        private void updateDBTasks(String statue, String taskName){
            DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("Tasks");
            System.out.println(taskName);

            Query query = tasksRef.orderByChild("taskName").equalTo(taskName);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String taskId = snapshot.getKey();
                        DatabaseReference taskRef = tasksRef.child(taskId);
                        taskRef.child("taskStatus").setValue(statue, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    System.out.println("Tasks modified ! ");
                                } else {
                                    System.err.println("Error : " + databaseError.getMessage());
                                }
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Gestion de l'erreur, si nécessaire
                }
            });




        }
    }
}
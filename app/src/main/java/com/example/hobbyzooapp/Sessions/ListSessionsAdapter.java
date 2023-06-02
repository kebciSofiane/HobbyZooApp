package com.example.hobbyzooapp.Sessions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.Activities.ActivityPage;
import com.example.hobbyzooapp.Activities.TodoAdapter;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.HomeActivity;
import com.example.hobbyzooapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class ListSessionsAdapter extends RecyclerView.Adapter<ListSessionsAdapter.ViewHolder> {
    private List<Session> items;
    private int displayedItemCount = 3;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_EMPTY = 1;
    private boolean isExpanded = false;
    private  LayoutInflater inflater;
    ViewGroup v;

    public void setDisplayedItemCount(int displayedItemCount) {
        this.displayedItemCount = displayedItemCount;
    }

    public ListSessionsAdapter(List<Session> items,LayoutInflater inflater) {
        this.items = items;
        this.inflater=inflater;
    }

    public void setItems(List<Session> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= parent;
        // Créez et retournez une instance de ViewHolder qui contient la vue de chaque élément
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_session_list_activity, parent, false);
       return new ViewHolder(view);


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items.isEmpty()) {
            holder.textView.setText("No session planned for this activity"); // Message affiché lorsque la liste est vide
            holder.removeSession.setVisibility(View.GONE);
        } else {
            Session item = items.get(position);
            holder.textView.setText(item.getDay()+"-"+item.getMonth()+"-"+item.getYear()+" for "+item.getTime());

            holder.textView.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    CalendarUtils.selectedDate = LocalDate.of(item.getYear(),item.getMonth(),item.getDay());
                    Intent intent = new Intent(v.getContext(), MyDailySessions.class);
                }
            });


            holder.textView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    View dialogView = inflater.inflate(R.layout.custom_dialog_, null);

                    TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                    TextView dialogText = dialogView.findViewById(R.id.dialogText);

                    Button dialogButtonYes = dialogView.findViewById(R.id.dialogButtonLeft);
                    Button dialogButtonNo = dialogView.findViewById(R.id.dialogButtonRight);

                    dialogTitle.setText(items.get(position).getActivityName() + " - " + items.get(position).getTime());
                    dialogText.setText("Do you want to start ?");
                    dialogButtonYes.setText("Yes");
                    dialogButtonYes.setTextColor(Color.GREEN);
                    dialogButtonNo.setText("No");
                    dialogButtonNo.setTextColor(Color.RED);

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(holder.textView.getContext());
                    dialogBuilder.setView(dialogView);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                    dialogButtonYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(holder.textView.getContext(), RunSession.class);
                            intent.putExtra("activity_id", items.get(position).getActivityId());
                            intent.putExtra("session_id", items.get(position).getSessionId());
                            holder.itemView.getContext().startActivity(intent);
                            dialog.dismiss();

                        }
                    });
                    dialogButtonNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });





                }
            });



            holder.removeSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    View dialogView = inflater.inflate(R.layout.custom_dialog_, null);

                    TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
                    TextView dialogText = dialogView.findViewById(R.id.dialogText);

                    Button dialogButtonYes = dialogView.findViewById(R.id.dialogButtonLeft);
                    Button dialogButtonNo = dialogView.findViewById(R.id.dialogButtonRight);

                    dialogTitle.setText(items.get(position).getActivityName() + " - " + items.get(position).getTime());
                    dialogText.setText("Do you really want to delete the session ?");
                    dialogButtonYes.setText("Yes");
                    dialogButtonYes.setTextColor(Color.GREEN);
                    dialogButtonNo.setText("No");
                    dialogButtonNo.setTextColor(Color.RED);

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(holder.textView.getContext());
                    dialogBuilder.setView(dialogView);
                    AlertDialog dialog = dialogBuilder.create();
                    dialog.show();
                    dialogButtonYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DatabaseReference sessionRef = FirebaseDatabase.getInstance().getReference("Session");
                            Query query = sessionRef.orderByChild("session_id").equalTo(items.get(position).getSessionId());
                            sessionRef.child(items.get(position).getSessionId()).removeValue();
                            dialog.dismiss();
                            Intent intent = new Intent(holder.itemView.getContext(), ActivityPage.class);
                            intent.putExtra("activity_id",items.get(position).getActivityId());
                            holder.itemView.getContext().startActivity(intent);
                        }
                    });
                    dialogButtonNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });





                }
            });


        }
    }

    public boolean isExpanded() { return isExpanded; }

    public void setExpanded(boolean expanded) { isExpanded = expanded; }

    @Override
    public int getItemCount() {
        if (items.isEmpty()) {
            return 1; // Retourne 1 pour afficher le message si la liste est vide
        } else if (isExpanded) {
            return items.size();
        } else {
            return Math.min(items.size(), displayedItemCount);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public Button removeSession;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTitleTextView);
            removeSession = itemView.findViewById(R.id.deleteSessionButton);

        }
    }
}
package com.example.hobbyzooapp.Sessions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.R;

import java.util.List;

public class ListSessionsAdapter extends RecyclerView.Adapter<ListSessionsAdapter.ViewHolder> {
    private List<String> items;
    private int displayedItemCount = 3;
    private boolean isExpanded = false;


    public void setDisplayedItemCount(int displayedItemCount) {
        this.displayedItemCount = displayedItemCount;
    }

    public ListSessionsAdapter(List<String> items) {
        this.items = items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Créez et retournez une instance de ViewHolder qui contient la vue de chaque élément
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (items.isEmpty()) {
            holder.textView.setText("Aucune session disponible"); // Message affiché lorsque la liste est vide
        } else {
            String item = items.get(position);
            holder.textView.setText(item);
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }


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



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTitleTextView);
        }
    }
}

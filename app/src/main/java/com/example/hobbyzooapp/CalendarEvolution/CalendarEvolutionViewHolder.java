package com.example.hobbyzooapp.CalendarEvolution;

import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbyzooapp.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarEvolutionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    private final ArrayList<LocalDate> days;
    public final View parentView;
    public final TextView dayOfMonth;
    Spinner spinner;
    private final CalendarEvolutionAdapter.OnItemListener onItemListener;
    public CalendarEvolutionViewHolder(@NonNull View itemView, CalendarEvolutionAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days)
    {
        super(itemView);
        parentView = itemView.findViewById(R.id.parentView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        spinner = itemView.findViewById(R.id.evolutionActivityChooseActivity);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
        this.days = days;
    }

    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), days.get(getAdapterPosition()));
    }
}
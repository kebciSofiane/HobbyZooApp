package com.example.hobbyzooapp.CalendarEvolution;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.example.hobbyzooapp.Calendar.CalendarUtils;
import com.example.hobbyzooapp.R;
import com.example.hobbyzooapp.Sessions.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarEvolutionAdapter extends RecyclerView.Adapter<CalendarEvolutionViewHolder> {

        private final ArrayList<LocalDate> days;
        private  int evenDayColor;
        private String activity;


    private final CalendarEvolutionAdapter.OnItemListener onItemListener;

    public CalendarEvolutionAdapter(ArrayList<LocalDate> days, CalendarEvolutionAdapter.OnItemListener onItemListener, int evenDayColor, String activity)
    {
        this.evenDayColor =evenDayColor;
        this.days = days;
        this.onItemListener = onItemListener;
        this.activity=activity;

    }

    @NonNull
    @Override
    public CalendarEvolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size() > 15) //month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalendarEvolutionViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEvolutionViewHolder holder, int position) {
        final LocalDate date = days.get(position);

        if(date == null)
            holder.dayOfMonth.setText("");

        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            }
            if(date.equals(CalendarUtils.selectedDate)){
                GradientDrawable borderDrawable = new GradientDrawable();
                borderDrawable.setShape(GradientDrawable.RECTANGLE);
                borderDrawable.setCornerRadius(10);
                borderDrawable.setStroke(4, Color.LTGRAY);
                holder.parentView.setBackground(borderDrawable);}

            getSessions(holder,date);
        }
    }


    @Override
        public int getItemCount()
        {
            return days.size();
        }

        public interface  OnItemListener
        {
            void onItemClick(int position, LocalDate date);
        }

    private void getSessions(CalendarEvolutionViewHolder holder,LocalDate date) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Session");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String uid = user.getUid();
        ArrayList<Session> mySessions = new ArrayList<>();
        reference.orderByChild("user_id").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String session_id = snapshot.child("session_id").getValue(String.class);
                    String session_duration = snapshot.child("session_duration").getValue(String.class);
                    String activity_id = snapshot.child("activity_id").getValue(String.class);
                    String session_day = snapshot.child("session_day").getValue(String.class);
                    String session_month = snapshot.child("session_month").getValue(String.class);
                    String session_year = snapshot.child("session_year").getValue(String.class);
                    String session_done = snapshot.child("session_done").getValue(String.class);
                    String session_image = snapshot.child("session_picture").getValue(String.class);



                    if (session_done.equals("TRUE")) {

                    DatabaseReference referenceActivity = database.getReference("Activity");

                    referenceActivity.child(activity_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                String activityName = dataSnapshot.child("activity_name").getValue(String.class);
                                LocalDate sessionDate = LocalDate.of(Integer.parseInt(session_year), Integer.parseInt(session_month), Integer.parseInt(session_day));

                                if (date.getMonth() == sessionDate.getMonth() &&
                                        date.getDayOfMonth() == sessionDate.getDayOfMonth() &&
                                        date.getYear() == sessionDate.getYear()) {
                                    assert activityName != null;
                                    if (activityName.equals(activity)) {

                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRef = storage.getReference();

                                        if (!session_image.isEmpty()) {
                                            System.out.println("session: " + session_image);
                                            Glide.with(holder.itemView.getContext())
                                                    .load(session_image)
                                                    .into(new CustomTarget<Drawable>() {
                                                        @Override
                                                        public void onResourceReady(@NonNull Drawable resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                                                            BitmapDrawable bitmapDrawable = (BitmapDrawable) resource;
                                                            Bitmap bitmap = bitmapDrawable.getBitmap();
                                                            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(holder.itemView.getResources(), bitmap);
                                                            roundedDrawable.setCornerRadius(20);

                                                            holder.itemView.setBackground(roundedDrawable);
                                                        }

                                                        @Override
                                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                                            // Méthode facultative pour gérer le chargement annulé ou effacé
                                                        }
                                                    });
                                        }
                                    }
                                }
                            } else {
                                // L'activité n'existe pas dans la base de données
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Une erreur s'est produite lors de la récupération des données
                        }
                    });
                }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Erreur lors de la récupération des données", databaseError.toException());
            }
        });
    }

}

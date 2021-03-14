package com.example.mycitycourts.ObserverDesignPattern;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MaxIdClass implements SubjectObserverable, Serializable {
    private List<MyObserverImpl> observersList = new ArrayList<>();
    private int MaxIdNow;
    private ValueEventListener maxIdListener;
    private DatabaseReference pointsDB;
    public MaxIdClass(){
        Log.d("Max Id Class","Created");
        pointsDB=FirebaseDatabase.getInstance().getReference("points");
        pointsDB.addValueEventListener(maxIdListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        Log.d("Observer Class kid ",d.getKey());
                    }
                    MaxIdNow = (int) dataSnapshot.getChildrenCount();
                    if(MaxIdNow ==0){
                        Log.d("Observer Class","Max id now is 0 - problem");
                    }
                }
                notifyObservers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    @Override
    public void register(MyObserverImpl obj) {
        observersList.add(obj);
    }

    @Override
    public void unregister(MyObserverImpl obj) {
        observersList.remove(obj);
    }

    @Override
    public void notifyObservers() {
        for (MyObserverImpl observer: observersList)observer.update(MaxIdNow);
    }

    @Override
    public Object getUpdate(MyObserverImpl obj) {
        return null;
    }

    @Override
    public void DeleteListener() {
        if(maxIdListener!=null&&pointsDB!=null)pointsDB.removeEventListener(maxIdListener);
    }
}

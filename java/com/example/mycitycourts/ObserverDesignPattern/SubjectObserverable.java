package com.example.mycitycourts.ObserverDesignPattern;

public interface SubjectObserverable{
    //methods to register and unregister observers
     void register(MyObserverImpl obj);
     void unregister(MyObserverImpl obj);

    //method to notify observers of change
     void notifyObservers();

    //method to get updates from subject
     Object getUpdate(MyObserverImpl obj);
     void DeleteListener();
}

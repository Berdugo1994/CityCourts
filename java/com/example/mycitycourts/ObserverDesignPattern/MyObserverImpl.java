package com.example.mycitycourts.ObserverDesignPattern;

public interface MyObserverImpl {

    //method to update the observer, used by subject
    void update(int x);

    //attach with subject to observe
    void setSubject(SubjectObserverable sub);
}
package com.example.kronosprojeto.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kronosprojeto.model.Notification;

import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final MutableLiveData<List<Notification>> _notifications = new MutableLiveData<>();

    public void setNotifications(List<Notification> notifications) {
        _notifications.setValue(notifications);
    }

    public LiveData<List<Notification>> getNotifications() {
        return _notifications;
    }
}

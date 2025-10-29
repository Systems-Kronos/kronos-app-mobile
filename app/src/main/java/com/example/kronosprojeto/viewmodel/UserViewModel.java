// UserViewModel.java
package com.example.kronosprojeto.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.kronosprojeto.dto.UserResponseDto;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<UserResponseDto> userLiveData = new MutableLiveData<>();

    public void setUser(UserResponseDto user) {
        userLiveData.setValue(user);
    }

    public LiveData<UserResponseDto> getUser() {
        return userLiveData;
    }
}

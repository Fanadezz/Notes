package com.androidshowtime.notes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class NotesViewModel : ViewModel() {

    private val _isListEdited = MutableLiveData<Boolean>()
    val isListEdited: LiveData<Boolean>
        get() = _isListEdited


    fun listEdited() {

        _isListEdited.value = true
    }

    fun listEditingDone() {

        _isListEdited.value = false
    }

    fun onClickSaveButton() {
        Timber.i("Button Clicked")
        listEdited()

        _isButtonPressed.value = true
    }


    private val _isButtonPressed = MutableLiveData<Boolean>()
    val isButtonPressed: LiveData<Boolean>
        get() = _isButtonPressed


    fun resetSaveButton() {
        _isButtonPressed.value = false
    }
}
package com.example.todolist.ui.feature.addedit

sealed class AddEditEvent {
    data class OnTitleChange(val title: String): AddEditEvent()
    data class OnDescriptionChange(val description: String): AddEditEvent()
    object OnSaveTodo: AddEditEvent()
    object OnNavigateBack: AddEditEvent()
}
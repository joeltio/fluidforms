package io.joelt.fluidforms.ui.screens.forms

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.database.FormsRepository
import kotlinx.coroutines.launch

class FormsViewModel(
    private val repository: FormsRepository
) : ViewModel() {
    var forms: List<Form>? by mutableStateOf(null)
        private set

    fun loadForms() {
        viewModelScope.launch {
            forms = repository.getForms()
        }
    }
}
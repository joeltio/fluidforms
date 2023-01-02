package io.joelt.fluidforms.ui.screens.form_preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.database.FormsRepository
import kotlinx.coroutines.*

class FormPreviewViewModel(
    private val repository: FormsRepository
) : ViewModel() {
    var form by mutableStateOf<Form?>(null)

    fun loadForm(id: Long) {
        viewModelScope.launch {
            form = repository.getForm(id)
        }
    }

    fun deleteForm(nav: NavHostController) {
        viewModelScope.launch {
            form?.let {
                repository.deleteForm(it.id)
            }
            nav.popBackStack()
        }
    }
}

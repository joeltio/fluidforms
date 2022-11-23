package io.joelt.texttemplate.ui.screens.template_preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.database.TemplatesRepository
import kotlinx.coroutines.*

class TemplatePreviewViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var template by mutableStateOf<Template?>(null)

    fun loadTemplate(id: Long) {
        viewModelScope.launch {
            template = repository.getTemplate(id)
        }
    }

    fun deleteTemplate(nav: NavHostController) {
        viewModelScope.launch {
            template?.let {
                repository.deleteTemplate(it.id)
            }
            nav.popBackStack()
        }
    }
}

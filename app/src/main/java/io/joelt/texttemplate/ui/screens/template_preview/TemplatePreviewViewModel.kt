package io.joelt.texttemplate.ui.screens.template_preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
}
package io.joelt.texttemplate.ui.screens.drafts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.database.TemplatesRepository
import kotlinx.coroutines.launch

class DraftsViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var drafts: List<Draft>? by mutableStateOf(null)
        private set

    init {
        loadDrafts()
    }

    private fun loadDrafts() {
        viewModelScope.launch {
            drafts = repository.getDrafts(false)
        }
    }
}

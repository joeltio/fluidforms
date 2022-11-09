package io.joelt.texttemplate.ui.screens.archived

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.database.TemplatesRepository
import kotlinx.coroutines.launch

class ArchivedViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var archived: List<Draft>? by mutableStateOf(null)
        private set

    init {
        loadArchived()
    }

    private fun loadArchived() {
        viewModelScope.launch {
            archived = repository.getDrafts(true)
        }
    }
}

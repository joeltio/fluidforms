package io.joelt.texttemplate.ui.screens.archived_view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.database.TemplatesRepository
import kotlinx.coroutines.launch

class ArchivedViewViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var archived: Draft? by mutableStateOf(null)
        private set

    fun loadArchived(archivedId: Long) {
        viewModelScope.launch {
            archived = repository.getDraft(archivedId)
        }
    }
}

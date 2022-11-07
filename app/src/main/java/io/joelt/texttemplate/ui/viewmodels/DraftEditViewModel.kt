package io.joelt.texttemplate.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.models.Draft
import kotlinx.coroutines.launch

class DraftEditViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var draft: Draft? by mutableStateOf(null)
        private set

    fun loadDraft(draftId: Long) {
        viewModelScope.launch {
            draft = repository.getDraft(draftId)
        }
    }

    fun createDraft(templateId: Long) {
        viewModelScope.launch {
            val template = repository.getTemplate(templateId)
            val newDraft = Draft(template)
            val draftId = repository.createDraft(newDraft)
            draft = newDraft.copy(id = draftId)
        }
    }
}

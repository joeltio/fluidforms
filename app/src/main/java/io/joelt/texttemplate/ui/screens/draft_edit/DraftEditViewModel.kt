package io.joelt.texttemplate.ui.screens.draft_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.models.Draft
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DraftEditViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var draft: Draft? by mutableStateOf(null)

    fun loadDraft(draftId: Long) {
        viewModelScope.launch {
            draft = repository.getDraft(draftId)
        }
    }

    fun createDraft(templateId: Long) {
        viewModelScope.launch {
            val template = repository.getTemplate(templateId)
            val newDraft = Draft.fromTemplate(template)
            val draftId = repository.createDraft(newDraft)
            draft = newDraft.copy(id = draftId)
        }
    }

    fun saveDraft(nav: NavHostController) {
        viewModelScope.launch {
            draft?.let {
                repository.updateDraft(it.copy(lastEditedOn = LocalDateTime.now()))
                nav.navigateBackToDrafts()
            }
        }
    }

    fun deleteDraft(nav: NavHostController) {
        viewModelScope.launch {
            draft?.let {
                repository.deleteDraft(it.id)
                nav.navigateBackToDrafts()
            }
        }
    }
}

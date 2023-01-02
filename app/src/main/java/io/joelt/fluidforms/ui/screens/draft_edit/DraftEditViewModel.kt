package io.joelt.fluidforms.ui.screens.draft_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.joelt.fluidforms.database.FormsRepository
import io.joelt.fluidforms.models.Draft
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DraftEditViewModel(
    private val repository: FormsRepository
) : ViewModel() {
    var draft: Draft? by mutableStateOf(null)

    fun loadDraft(draftId: Long) {
        viewModelScope.launch {
            draft = repository.getDraft(draftId)
        }
    }

    fun createDraft(formId: Long) {
        viewModelScope.launch {
            val form = repository.getForm(formId)
            val newDraft = Draft.fromForm(form)
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

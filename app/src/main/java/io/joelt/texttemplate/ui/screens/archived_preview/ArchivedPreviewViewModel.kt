package io.joelt.texttemplate.ui.screens.archived_preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.navigation.Route
import io.joelt.texttemplate.navigation.navigateClearStack
import io.joelt.texttemplate.ui.screens.draft_edit.draftEdit
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class ArchivedPreviewViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var archived: Draft? by mutableStateOf(null)
        private set

    fun loadArchived(archivedId: Long) {
        viewModelScope.launch {
            archived = repository.getDraft(archivedId)
        }
    }

    fun unarchive(nav: NavHostController) {
        viewModelScope.launch {
            archived?.let {
                // Unarchive and update the last edited time so it does not get re-archived
                repository.updateDraft(it.copy(lastEditedOn = LocalDateTime.now(), archived = false))
                nav.navigateClearStack(Route.draftEdit(it.id))
            }
        }
    }

    fun deleteArchived(nav: NavHostController) {
        viewModelScope.launch {
            archived?.let {
                repository.deleteDraft(it.id)
                nav.popBackStack()
            }
        }
    }
}

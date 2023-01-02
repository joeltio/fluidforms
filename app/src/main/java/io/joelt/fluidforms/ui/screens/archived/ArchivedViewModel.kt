package io.joelt.fluidforms.ui.screens.archived

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.joelt.fluidforms.models.Draft
import io.joelt.fluidforms.database.FormsRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

class ArchivedViewModel(
    private val repository: FormsRepository
) : ViewModel() {
    var archived: List<Draft>? by mutableStateOf(null)
        private set

    fun loadArchived() {
        viewModelScope.launch {
            val newArchived = mutableListOf<Draft>()
            val today = LocalDate.now()
            repository.getDrafts().forEach {
                val period = Period.between(it.lastEditedOn.toLocalDate(), today)
                if (period.toTotalMonths() > 0) {
                    // Archive
                    repository.archiveDraft(it.id)
                    newArchived.add(it)
                } else if (it.archived) {
                    newArchived.add(it)
                }
            }

            archived = newArchived
        }
    }
}

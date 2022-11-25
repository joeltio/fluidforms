package io.joelt.texttemplate.ui.screens.drafts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.database.TemplatesRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

class DraftsViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var drafts: List<Draft>? by mutableStateOf(null)
        private set

    fun loadDrafts() {
        viewModelScope.launch {
            val newDrafts = mutableListOf<Draft>()
            val today = LocalDate.now()
            repository.getDrafts(false).forEach {
                val period = Period.between(it.lastEditedOn.toLocalDate(), today)
                if (period.toTotalMonths() > 0) {
                    // Archive
                    repository.archiveDraft(it.id)
                } else {
                    newDrafts.add(it)
                }
            }

            drafts = newDrafts
        }
    }
}

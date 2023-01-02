package io.joelt.fluidforms.ui.screens.form_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.joelt.fluidforms.models.Form
import io.joelt.fluidforms.database.FormsRepository
import io.joelt.fluidforms.models.slots.serializeFormBody
import io.joelt.fluidforms.navigation.Route
import io.joelt.fluidforms.ui.screens.form_preview.formPreview
import kotlinx.coroutines.*

class FormEditViewModel(
    private val repository: FormsRepository
) : ViewModel() {
    var screenState by mutableStateOf<FormEditState?>(null)
    lateinit var initialForm: Form

    fun loadForm(id: Long, defaultName: String) {
        if (id == 0L) {
            FormEditState(Form(name = defaultName, body = emptyList())).let {
                screenState = it
                initialForm = it.form
            }
        } else {
            viewModelScope.launch {
                FormEditState(repository.getForm(id)).let {
                    screenState = it
                    initialForm = it.form
                }
            }
        }
    }

    fun saveForm(nav: NavHostController) {
        viewModelScope.launch {
            screenState?.let {
                val form = it.form
                if (form.id == 0L) {
                    val formId = repository.createForm(form)
                    screenState = it.copy(form = form.copy(id = formId))
                    nav.navigate(Route.formPreview(formId))
                } else {
                    repository.updateForm(form)
                    nav.navigate(Route.formPreview(form.id))
                }
            }
        }
    }

    fun formChanged(): Boolean {
        screenState?.let {
            return initialForm.name != it.form.name || serializeFormBody(initialForm.body) != serializeFormBody(
                it.form.body
            )
        }
        return false
    }
}

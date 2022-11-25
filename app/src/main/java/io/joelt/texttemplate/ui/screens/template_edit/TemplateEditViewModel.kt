package io.joelt.texttemplate.ui.screens.template_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.navigation.Route
import io.joelt.texttemplate.ui.screens.templates.templates
import kotlinx.coroutines.*

class TemplateEditViewModel(
    private val repository: TemplatesRepository
) : ViewModel() {
    var screenState by mutableStateOf<TemplateEditState?>(null)
    lateinit var initialTemplate: Template

    fun loadTemplate(id: Long, defaultName: String) {
        if (id == 0L) {
            TemplateEditState(Template(name = defaultName, text = "")).let {
                screenState = it
                initialTemplate = it.template
            }
        } else {
            viewModelScope.launch {
                TemplateEditState(repository.getTemplate(id)).let {
                    screenState = it
                    initialTemplate = it.template
                }
            }
        }
    }

    fun saveTemplate(nav: NavHostController) {
        viewModelScope.launch {
            screenState?.let {
                val template = it.template
                if (template.id == 0L) {
                    val templateId = repository.createTemplate(template)
                    screenState = it.copy(template = template.copy(id = templateId))
                } else {
                    repository.updateTemplate(template)
                }
            }

            nav.navigate(Route.templates)
        }
    }

    fun templateChanged(): Boolean {
        screenState?.let {
            return initialTemplate.name != it.template.name || initialTemplate.text != it.template.text
        }
        return false
    }
}

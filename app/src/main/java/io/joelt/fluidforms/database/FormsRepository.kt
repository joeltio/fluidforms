package io.joelt.fluidforms.database

import io.joelt.fluidforms.models.Draft
import io.joelt.fluidforms.models.Form

interface FormsRepository {
    // Forms
    suspend fun createForm(form: Form): Long

    suspend fun getForm(id: Long): Form
    suspend fun getForms(): List<Form>

    suspend fun updateForm(form: Form)

    suspend fun deleteForm(id: Long)

    // Drafts
    suspend fun createDraft(draft: Draft): Long

    suspend fun getDraft(id: Long): Draft
    suspend fun getDrafts(): List<Draft>
    suspend fun getDrafts(archived: Boolean): List<Draft>

    suspend fun updateDraft(draft: Draft)
    suspend fun archiveDraft(id: Long)
    suspend fun unarchiveDraft(id: Long)

    suspend fun deleteDraft(id: Long)
}
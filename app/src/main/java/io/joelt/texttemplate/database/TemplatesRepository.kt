package io.joelt.texttemplate.database

import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.models.Template

interface TemplatesRepository {
    // Templates
    suspend fun createTemplate(template: Template): Long

    suspend fun getTemplate(id: Long): Template
    suspend fun getTemplates(): List<Template>

    suspend fun updateTemplate(template: Template)

    suspend fun deleteTemplate(id: Long)

    // Drafts
    suspend fun createDraft(draft: Draft): Long

    suspend fun getDraft(id: Long): Draft
    suspend fun getDrafts(): List<Draft>
    suspend fun getDrafts(archived: Boolean): List<Draft>

    suspend fun updateDraft(draft: Draft)
    suspend fun archiveDraft(id: Long)

    suspend fun deleteDraft(id: Long)
    suspend fun deleteArchived(id: Long)
}
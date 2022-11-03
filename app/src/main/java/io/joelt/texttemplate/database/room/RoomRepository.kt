package io.joelt.texttemplate.database.room

import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.models.Draft
import io.joelt.texttemplate.models.Template
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class RoomRepository(
    private val database: AppDatabase
) : TemplatesRepository {
    private suspend fun <T> withDb(
        block: suspend AppDatabase.() -> T
    ): T {
        return withContext(Dispatchers.IO) {
            block(database)
        }
    }

    // Templates
    override suspend fun createTemplate(template: Template): Long = withDb {
        templateDao().insert(template.toRoom(0))
    }

    override suspend fun getTemplate(id: Long): Template = withDb {
        templateDao().get(id).toModel()
    }

    override suspend fun getTemplates(): List<Template> = withDb {
        templateDao().getAll().map { it.toModel() }
    }

    override suspend fun updateTemplate(template: Template) = withDb {
        templateDao().update(template.toRoom())
    }

    override suspend fun deleteTemplate(id: Long) = withDb {
        templateDao().delete(id)
    }

    // Drafts
    override suspend fun createDraft(draft: Draft): Long = withDb {
        draftDao().insert(draft.toRoom(0))
    }

    override suspend fun getDraft(id: Long): Draft = withDb {
        draftDao().getDraft(id).toModel()
    }

    override suspend fun getDrafts(): List<Draft> = withDb {
        draftDao().getDrafts().map { it.toModel() }
    }

    override suspend fun getDrafts(archived: Boolean): List<Draft> = withDb {
        draftDao().getDrafts(archived).map { it.toModel() }
    }

    override suspend fun updateDraft(draft: Draft) = withDb {
        draftDao().update(draft.toRoom())
    }

    override suspend fun archiveDraft(id: Long) = withDb {
        draftDao().updateToArchived(id)
    }

    override suspend fun deleteDraft(id: Long) = withDb {
        draftDao().delete(id)
    }
}
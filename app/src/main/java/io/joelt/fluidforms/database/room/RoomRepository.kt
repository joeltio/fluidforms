package io.joelt.fluidforms.database.room

import io.joelt.fluidforms.database.FormsRepository
import io.joelt.fluidforms.models.Draft
import io.joelt.fluidforms.models.Form
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomRepository(
    private val database: AppDatabase
) : FormsRepository {
    private suspend fun <T> withDb(
        block: suspend AppDatabase.() -> T
    ): T {
        return withContext(Dispatchers.IO) {
            block(database)
        }
    }

    // Forms
    override suspend fun createForm(form: Form): Long = withDb {
        formDao().insert(form.toRoom(0))
    }

    override suspend fun getForm(id: Long): Form = withDb {
        formDao().get(id).toModel()
    }

    override suspend fun getForms(): List<Form> = withDb {
        formDao().getAll().map { it.toModel() }
    }

    override suspend fun updateForm(form: Form) = withDb {
        formDao().update(form.toRoom())
    }

    override suspend fun deleteForm(id: Long) = withDb {
        formDao().delete(id)
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

    override suspend fun unarchiveDraft(id: Long) = withDb {
        draftDao().updateToUnarchived(id)
    }

    override suspend fun deleteDraft(id: Long) = withDb {
        draftDao().delete(id)
    }
}
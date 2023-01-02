package io.joelt.fluidforms

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.joelt.fluidforms.database.FormsRepository
import io.joelt.fluidforms.database.room.AppDatabase
import io.joelt.fluidforms.database.room.RoomRepository
import io.joelt.fluidforms.models.Draft
import io.joelt.fluidforms.models.Either
import io.joelt.fluidforms.models.slots.EscapedString
import io.joelt.fluidforms.models.slots.Slot
import io.joelt.fluidforms.models.slots.deserializeFormBody
import io.joelt.fluidforms.models.slots.serializeFormBody
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class DraftReadWriteTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: FormsRepository
    private fun String.deserialize() = deserializeFormBody(EscapedString(this))
    private fun List<Either<String, Slot>>.serialize() = serializeFormBody(this)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        repo = RoomRepository(db)
    }

    @After
    fun closeDb() {
        db.close()
    }

    fun createDraft(
        name: String = "default draft",
        text: String = "default draft text",
        lastEditedIndex: Int = 0,
        lastEditedOn: LocalDateTime = LocalDateTime.now()
    ): Draft = Draft(
        name = name,
        body = text.deserialize(),
        lastEditedIndex = lastEditedIndex,
        lastEditedOn = lastEditedOn
    )

    @Test
    fun saveAndRetrieveDate() = runTest {
        val date = LocalDateTime.now()
        val draft = createDraft(name = "my draft", lastEditedOn = date)

        val id = repo.createDraft(draft)
        val dbDate = repo.getDraft(id).lastEditedOn

        assertEquals(date.year, dbDate.year)
        assertEquals(date.month, dbDate.month)
        assertEquals(date.dayOfMonth, dbDate.dayOfMonth)
        assertEquals(date.hour, dbDate.hour)
        assertEquals(date.minute, dbDate.minute)
        assertEquals(date.second, dbDate.second)
        assertEquals(date.nano, dbDate.nano)
    }

    @Test
    fun createAndReadDraft() = runTest {
        val draft = createDraft(name = "my draft", text = "hello world!")

        repo.createDraft(draft)
        repo.createDraft(draft)
        repo.createDraft(draft)

        val drafts = repo.getDrafts()
        assertEquals(drafts.size, 3)
        assertEquals(drafts[0].name, "my draft")
        assertEquals(drafts[0].body.serialize(), "hello world\\!")
    }

    @Test
    fun deleteDraft() = runTest {
        val draft = createDraft(name = "my draft", text = "hello world!")

        repo.createDraft(draft)
        repo.createDraft(draft)
        val lastDraftId = repo.createDraft(draft)

        var drafts = repo.getDrafts()
        repo.deleteDraft(drafts[0].id)
        repo.deleteDraft(drafts[1].id)

        drafts = repo.getDrafts()
        assertEquals(drafts.size, 1)
        assertEquals(drafts[0].id, lastDraftId)
    }

    @Test
    fun updateDraft() = runTest {
        val draft = createDraft(name = "my draft", text = "hello world!")

        repo.createDraft(draft)
        val draftId = repo.createDraft(draft)
        repo.createDraft(draft)

        val newDraft = draft.copy(id = draftId, name = "new name")
        repo.updateDraft(newDraft)

        val updatedDraft = repo.getDraft(draftId)
        assertEquals(updatedDraft.name, "new name")
    }

    @Test
    fun updateDraftArchived() = runTest {
        val draft = createDraft()

        val draftId = repo.createDraft(draft)
        assertEquals(repo.getDrafts(false).size, 1)
        assertEquals(repo.getDrafts(true).size, 0)

        repo.archiveDraft(draftId)
        assertEquals(repo.getDrafts(false).size, 0)
        assertEquals(repo.getDrafts(true).size, 1)

        repo.unarchiveDraft(draftId)
        assertEquals(repo.getDrafts(false).size, 1)
        assertEquals(repo.getDrafts(true).size, 0)
    }
}

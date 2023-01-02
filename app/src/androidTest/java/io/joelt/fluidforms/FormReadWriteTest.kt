package io.joelt.fluidforms

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.joelt.fluidforms.database.FormsRepository
import io.joelt.fluidforms.database.room.AppDatabase
import io.joelt.fluidforms.database.room.RoomRepository
import io.joelt.fluidforms.models.Either
import io.joelt.fluidforms.models.Form
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
class FormReadWriteTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: FormsRepository
    private fun String.deserialize() = deserializeFormBody(EscapedString(this))
    private fun List<Either<String, Slot>>.serialize() = serializeFormBody(this)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        repo = RoomRepository(db)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun saveAndRetrieveDate() = runTest {
        val date = LocalDateTime.now()
        val form = Form(createdOn = date, name = "my form", body = "hello world!".deserialize())

        val id = repo.createForm(form)
        val dbDate = repo.getForm(id).createdOn

        assertEquals(date.year, dbDate.year)
        assertEquals(date.month, dbDate.month)
        assertEquals(date.dayOfMonth, dbDate.dayOfMonth)
        assertEquals(date.hour, dbDate.hour)
        assertEquals(date.minute, dbDate.minute)
        assertEquals(date.second, dbDate.second)
        assertEquals(date.nano, dbDate.nano)
    }

    @Test
    fun createAndReadForms() = runTest {
        val form = Form(name = "my form", body = "hello world!".deserialize())

        repo.createForm(form)
        repo.createForm(form)
        repo.createForm(form)

        val forms = repo.getForms()
        assertEquals(forms.size, 3)
        assertEquals(forms[0].name, "my form")
        assertEquals(forms[0].body.serialize(), "hello world\\!")
    }

    @Test
    fun deleteForms() = runTest {
        val form = Form(name = "my form", body = "hello world!".deserialize())

        repo.createForm(form)
        repo.createForm(form)
        val lastFormId = repo.createForm(form)

        var forms = repo.getForms()
        repo.deleteForm(forms[0].id)
        repo.deleteForm(forms[1].id)

        forms = repo.getForms()
        assertEquals(forms.size, 1)
        assertEquals(forms[0].id, lastFormId)
    }

    @Test
    fun updateForm() = runTest {
        val form = Form(name = "my form", body = "hello world!".deserialize())

        repo.createForm(form)
        val formId = repo.createForm(form)
        repo.createForm(form)

        val newForm = form.copy(id = formId, name = "new name")
        repo.updateForm(newForm)

        val updatedForm = repo.getForm(formId)
        assertEquals(updatedForm.name, "new name")
    }
}

package io.joelt.texttemplate

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.database.room.AppDatabase
import io.joelt.texttemplate.database.room.RoomRepository
import io.joelt.texttemplate.models.Either
import io.joelt.texttemplate.models.Template
import io.joelt.texttemplate.models.slots.EscapedString
import io.joelt.texttemplate.models.slots.Slot
import io.joelt.texttemplate.models.slots.deserializeTemplateBody
import io.joelt.texttemplate.models.slots.serializeTemplateBody
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
class TemplateReadWriteTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: TemplatesRepository
    private fun String.deserialize() = deserializeTemplateBody(EscapedString(this))
    private fun List<Either<String, Slot>>.serialize() = serializeTemplateBody(this)

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
        val template = Template(createdOn = date, name = "my template", body = "hello world!".deserialize())

        val id = repo.createTemplate(template)
        val dbDate = repo.getTemplate(id).createdOn

        assertEquals(date.year, dbDate.year)
        assertEquals(date.month, dbDate.month)
        assertEquals(date.dayOfMonth, dbDate.dayOfMonth)
        assertEquals(date.hour, dbDate.hour)
        assertEquals(date.minute, dbDate.minute)
        assertEquals(date.second, dbDate.second)
        assertEquals(date.nano, dbDate.nano)
    }

    @Test
    fun createAndReadTemplates() = runTest {
        val template = Template(name = "my template", body = "hello world!".deserialize())

        repo.createTemplate(template)
        repo.createTemplate(template)
        repo.createTemplate(template)

        val templates = repo.getTemplates()
        assertEquals(templates.size, 3)
        assertEquals(templates[0].name, "my template")
        assertEquals(templates[0].body.serialize(), "hello world\\!")
    }

    @Test
    fun deleteTemplates() = runTest {
        val template = Template(name = "my template", body = "hello world!".deserialize())

        repo.createTemplate(template)
        repo.createTemplate(template)
        val lastTemplateId = repo.createTemplate(template)

        var templates = repo.getTemplates()
        repo.deleteTemplate(templates[0].id)
        repo.deleteTemplate(templates[1].id)

        templates = repo.getTemplates()
        assertEquals(templates.size, 1)
        assertEquals(templates[0].id, lastTemplateId)
    }

    @Test
    fun updateTemplate() = runTest {
        val template = Template(name = "my template", body = "hello world!".deserialize())

        repo.createTemplate(template)
        val templateId = repo.createTemplate(template)
        repo.createTemplate(template)

        val newTemplate = template.copy(id = templateId, name = "new name")
        repo.updateTemplate(newTemplate)

        val updatedTemplate = repo.getTemplate(templateId)
        assertEquals(updatedTemplate.name, "new name")
    }
}

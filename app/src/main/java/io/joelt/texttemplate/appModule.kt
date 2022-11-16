package io.joelt.texttemplate

import androidx.room.Room
import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.database.room.AppDatabase
import io.joelt.texttemplate.database.room.RoomRepository
import io.joelt.texttemplate.ui.screens.archived.ArchivedViewModel
import io.joelt.texttemplate.ui.screens.archived_view.ArchivedViewViewModel
import io.joelt.texttemplate.ui.screens.draft_edit.DraftEditViewModel
import io.joelt.texttemplate.ui.screens.drafts.DraftsViewModel
import io.joelt.texttemplate.ui.screens.template_edit.TemplateEditViewModel
import io.joelt.texttemplate.ui.screens.templates.TemplatesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "templates_db"
        ).build()
    }
    single<TemplatesRepository> { RoomRepository(get()) }
    viewModel { TemplatesViewModel(get()) }
    viewModel { TemplateEditViewModel(get()) }
    viewModel { DraftsViewModel(get()) }
    viewModel { ArchivedViewModel(get()) }
    viewModel { DraftEditViewModel(get()) }
    viewModel { ArchivedViewViewModel(get()) }
}

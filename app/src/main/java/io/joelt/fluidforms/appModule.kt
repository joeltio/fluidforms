package io.joelt.fluidforms

import androidx.room.Room
import io.joelt.fluidforms.database.FormsRepository
import io.joelt.fluidforms.database.UserPreferencesRepository
import io.joelt.fluidforms.database.dataStore
import io.joelt.fluidforms.database.room.AppDatabase
import io.joelt.fluidforms.database.room.RoomRepository
import io.joelt.fluidforms.ui.screens.archived.ArchivedViewModel
import io.joelt.fluidforms.ui.screens.archived_preview.ArchivedPreviewViewModel
import io.joelt.fluidforms.ui.screens.draft_edit.DraftEditViewModel
import io.joelt.fluidforms.ui.screens.drafts.DraftsViewModel
import io.joelt.fluidforms.ui.screens.form_edit.FormEditViewModel
import io.joelt.fluidforms.ui.screens.form_preview.FormPreviewViewModel
import io.joelt.fluidforms.ui.screens.forms.FormsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "forms_db"
        ).build()
    }
    single<FormsRepository> { RoomRepository(get()) }
    single { UserPreferencesRepository(androidApplication().dataStore) }
    viewModel { FormsViewModel(get()) }
    viewModel { FormEditViewModel(get()) }
    viewModel { FormPreviewViewModel(get()) }
    viewModel { DraftsViewModel(get()) }
    viewModel { ArchivedViewModel(get()) }
    viewModel { DraftEditViewModel(get()) }
    viewModel { ArchivedPreviewViewModel(get()) }
}

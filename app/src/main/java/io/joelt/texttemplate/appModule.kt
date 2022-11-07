package io.joelt.texttemplate

import androidx.room.Room
import io.joelt.texttemplate.database.TemplatesRepository
import io.joelt.texttemplate.database.room.AppDatabase
import io.joelt.texttemplate.database.room.RoomRepository
import io.joelt.texttemplate.ui.viewmodels.DraftEditViewModel
import io.joelt.texttemplate.ui.viewmodels.TemplatesViewModel
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
    viewModel { DraftEditViewModel(get()) }
}

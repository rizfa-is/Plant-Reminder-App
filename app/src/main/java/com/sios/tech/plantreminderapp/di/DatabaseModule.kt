package com.sios.tech.plantreminderapp.di

import android.content.Context
import androidx.room.Room
import com.sios.tech.plantreminderapp.data.local.PlantDatabase
import com.sios.tech.plantreminderapp.data.local.PlantDao
import com.sios.tech.plantreminderapp.data.repository.PlantRepositoryImpl
import com.sios.tech.plantreminderapp.domain.repository.PlantRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePlantDatabase(
        @ApplicationContext context: Context
    ): PlantDatabase {
        return Room.databaseBuilder(
            context,
            PlantDatabase::class.java,
            "plants.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePlantDao(database: PlantDatabase): PlantDao {
        return database.plantDao
    }

    @Provides
    @Singleton
    fun providePlantRepository(dao: PlantDao): PlantRepository {
        return PlantRepositoryImpl(dao)
    }
}

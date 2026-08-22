package com.example.weatherforecasts.model

import android.content.Context
import androidx.room.*


@Database(
    entities = arrayOf(Alarm::class, Alert::class, Location::class, Fav::class), version = 19,/*autoMigrations = [

]*/exportSchema = true
)
@TypeConverters(Converters::class)

abstract class AppDataBase : RoomDatabase() {

    abstract fun getProdDao(): AlarmDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(ctx: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    AppDataBase::class.java,
                    "Alarm_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }


    }


}










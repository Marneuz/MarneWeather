package com.marneux.marneweather.data.local.textgeneration

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GeneratedTextForLocationEntity::class], version = 1)
abstract class GeneratedTextCacheDatabase : RoomDatabase() {

    abstract fun getDao(): GeneratedTextCacheDatabaseDao

    companion object {
        const val DATABASE_NAME = "Generated_Text_Cache_Database"
    }
}

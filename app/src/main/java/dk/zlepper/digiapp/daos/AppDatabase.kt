package dk.zlepper.digiapp.daos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dk.zlepper.digiapp.models.UserCredentials

fun GetAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "db.db").build()
}

@Database(entities = arrayOf(UserCredentials::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userCredentialsDao(): UserCredentialsDao


}
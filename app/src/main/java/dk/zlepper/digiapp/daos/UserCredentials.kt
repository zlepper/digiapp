package dk.zlepper.digiapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface UserCredentials {
    @Insert(onConflict = REPLACE)
    fun insert(userCredentials: UserCredentials)
}
package dk.zlepper.digiapp.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import dk.zlepper.digiapp.models.UserCredentials

@Dao
interface UserCredentialsDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertCredentials(userCredentials: UserCredentials)

    @Query("SELECT * FROM UserCredentials")
    suspend fun getStoredCredentials(): UserCredentials?

    @Query("DELETE FROM UserCredentials")
    suspend fun removeStoredCredentials()
}
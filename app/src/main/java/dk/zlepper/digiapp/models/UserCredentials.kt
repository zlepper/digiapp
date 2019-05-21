package dk.zlepper.digiapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserCredentials(@PrimaryKey val username: String, val password: String)
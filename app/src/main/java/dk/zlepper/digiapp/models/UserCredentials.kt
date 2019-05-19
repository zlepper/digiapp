package dk.zlepper.digiapp.models

import androidx.room.Entity

@Entity
data class UserCredentials(val username: String, val password: String)
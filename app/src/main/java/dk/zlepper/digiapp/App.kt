package dk.zlepper.digiapp

import android.app.Application
import dk.zlepper.digiapp.daos.GetAppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()


        GlobalScope.launch(Dispatchers.Main) {
            println("Loading database")
            val userDao = GetAppDatabase(applicationContext).userCredentialsDao()

            println("Loading credentials")
            val creds = userDao.getStoredCredentials()

            if (creds != null) {
                println("Found stored credentials: $creds")
            } else {
                println("No credentials stored")
            }
        }
    }
}
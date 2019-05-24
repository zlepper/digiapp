package dk.zlepper.digiapp.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import dk.zlepper.digiapp.R
import dk.zlepper.digiapp.daos.AppDatabase
import dk.zlepper.digiapp.models.UserCredentials
import dk.zlepper.digiapp.services.AuthenticatedUserService
import dk.zlepper.digiapp.services.AuthenticationService
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LoginFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onStart() {
        super.onStart()

        loginButton.setOnClickListener(::onLogin)
    }

    fun onLogin(view: View) {

        GlobalScope.launch(Dispatchers.Main) {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            try {
                val response = AuthenticationService.login(username, password)
                println(response)

                AuthenticatedUserService.accessKey = response.accessKey

                val db = Room.databaseBuilder<AppDatabase>(requireContext(), AppDatabase::class.java, "db.db").build()
                val dao = db.userCredentialsDao()
                dao.removeStoredCredentials()
                dao.insertCredentials(UserCredentials(username, password))

                // Close the keyboard, since it doesn't autoclose when the input disappears...
                // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
                activity?.currentFocus?.let { v ->
                    val imm = getSystemService<InputMethodManager>(v.context, InputMethodManager::class.java)
                    imm?.hideSoftInputFromWindow(v.windowToken, 0)
                }

                val action = LoginFragmentDirections.actionLoginFragmentToAssetListFragment()
                findNavController().navigate(action)

            } catch (e: AuthenticationService.LoginException) {
                println("Login failed...")
            }
        }

    }
}

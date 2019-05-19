package dk.zlepper.digiapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            } catch (e: AuthenticationService.LoginException) {
                println("Login failed...")
            }
        }

    }
}

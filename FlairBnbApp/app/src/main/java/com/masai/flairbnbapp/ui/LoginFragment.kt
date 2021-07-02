package com.masai.flairbnbapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.masai.flairbnbapp.R
import com.masai.flairbnbapp.localdatabases.LocalKeys
import com.masai.flairbnbapp.localdatabases.PreferenceHelper
import com.masai.flairbnbapp.models.UserModel
import com.shobhitpuri.custombuttons.GoogleSignInButton
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var gso: GoogleSignInOptions
    lateinit var googleSignInClient: GoogleSignInClient
    val SIGN_IN_CODE = 10
    private var mAuth: FirebaseAuth? = null
    lateinit var navController: NavController
    lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        this.v = view
        initializeSignin(view)

    }

    private fun initializeSignin(view: View) {
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mAuth = FirebaseAuth.getInstance()

        googleSignInClient = GoogleSignIn.getClient(view.context, gso)

        val signInButton = view.findViewById<GoogleSignInButton>(R.id.signInButton);
        signInButton.setOnClickListener {
            val intent: Intent = googleSignInClient.signInIntent
            startActivityForResult(intent, SIGN_IN_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_CODE) {
            var task: Task<GoogleSignInAccount>? = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            if (task!!.isSuccessful) {
                val account = task.getResult(ApiException::class.java)
                PreferenceHelper.writeBooleanToPreference(LocalKeys.KEY_IS_USER_LOGGED_IN, true)
                updatePreference(account!!)
                Toast.makeText(v.context, "Welcome ${account.displayName}", Toast.LENGTH_SHORT)
                    .show()
                saveUser(account)
                navController.navigate(R.id.action_loginFragment_to_addInfoFragment)
            } else {
                Toast.makeText(
                    v.context,
                    "Login Error " + task.exception?.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
        }
    }

    private fun updatePreference(account: GoogleSignInAccount) {
        PreferenceHelper.writeBooleanToPreference(LocalKeys.KEY_IS_USER_LOGGED_IN, true)
        PreferenceHelper.writeStringToPreference(LocalKeys.KEY_USER_GOOGLE_ID, account.id)
        PreferenceHelper.writeStringToPreference(LocalKeys.KEY_USER_LOGIN_TYPE, "google")

    }

    private fun saveUser(account: GoogleSignInAccount) {
        val database = FirebaseDatabase.getInstance()
        val dbUsers = database.getReference("users").child(account.id!!)
        dbUsers.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //even if user exit in database the FCM token will be different for each device
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseDatabase.getInstance().getReference("users")
                                .child(account.id!!)
                            val token = Objects.requireNonNull(task.result)
                            user.child("token").setValue(token)
                        } else {
                            Log.d("TAG", "onComplete: " + task.exception!!.message)
                        }
                    }
                    Toast.makeText(v.context, "Welcomeback", Toast.LENGTH_SHORT).show()
                    return
                }
                if (!snapshot.exists()) {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val token: String = Objects.requireNonNull<String>(it.result)
                            if (account.photoUrl != null) {
                                val user =
                                    UserModel(
                                        id = account.id.toString(),
                                        email = account.email,
                                        firstName = account.displayName,
                                        lastName = "",
                                        gender = "",
                                        profilePic = account.photoUrl.toString(),
                                        contactNumber = "",
                                        location = "",
                                        address = "",
                                        token = token,
                                        loginType = "google"
                                    )
                                dbUsers.setValue(user)
                                    .addOnCompleteListener { it_inside ->
                                        if (it_inside.isSuccessful) {
                                            Toast.makeText(
                                                v?.context,
                                                "token saved",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    }
                            } else {
                                val user =
                                    UserModel(
                                        id = account.id.toString(),
                                        email = account.email,
                                        firstName = account.displayName,
                                        lastName = "",
                                        gender = "",
                                        contactNumber = "",
                                        location = "",
                                        address = "",
                                        token = token,
                                        profilePic = "",
                                        loginType = "google",
                                    )
                                dbUsers.setValue(user)
                                    .addOnCompleteListener { it_inside ->
                                        if (it_inside.isSuccessful) {
                                            Toast.makeText(
                                                v.context,
                                                "token saved",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}
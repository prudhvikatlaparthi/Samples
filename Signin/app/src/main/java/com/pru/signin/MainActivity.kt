package com.pru.signin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pru.signin.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnGoogleSigIn.setOnClickListener {
            lifecycleScope.launch {
                googleAuthUiClient.signOut()
                binding.pbView.isVisible = true
                if (googleAuthUiClient.getSignedInUser() == null) {
                    val signInIntentSender = googleAuthUiClient.signIn()
                    googleLauncher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                } else {
                    binding.pbView.isVisible = false
                    val signInResult = googleAuthUiClient.getSignedInUser()
                    Log.i("Prudhvi Log", ": $signInResult")
                    Toast.makeText(
                        this@MainActivity, "Hi ${signInResult?.username}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnAppleSigIn.setOnClickListener {

            appleSignIn()
        }
    }

    private fun appleSignIn() {
        binding.pbView.isVisible = true
        val appleIdpConfig = AuthUI.IdpConfig.AppleBuilder()
                .setScopes(listOf("email", "name"))
                .build()

            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(listOf(appleIdpConfig))
                .build()

            appleLauncher.launch(intent)


        val provider = OAuthProvider.newBuilder("apple.com")
        provider.setScopes(listOf("email", "name"))

        val auth = Firebase.auth
        val pending = auth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { authResult ->
                binding.pbView.isVisible = false
                extractResult(authResult)
            }.addOnFailureListener { e ->
                binding.pbView.isVisible = false
                Log.i("Prudhvi Log", "onCreate:Fail $e")
            }
        } else {
            auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener { authResult ->
                    binding.pbView.isVisible = false
                    extractResult(authResult)
                }.addOnFailureListener { e ->
                    binding.pbView.isVisible = false
                    Log.w(
                        "Prudhvi Log",
                        "activitySignIn:onFailure = {FirebaseAuthException@26250} \"com.google.firebase.auth.FirebaseAuthException: The given sign-in provider is disabled for this Firebase project. Enable it in the Firebase console, under the sign-in method tab of the Auth section. [ Code flow is not enabled for Apple. ]\"",
                        e
                    )
                    Toast.makeText(
                        this,
                        "activitySignIn:onFailure = {FirebaseAuthException@26250} \"com.google.firebase.auth.FirebaseAuthException: The given sign-in provider is disabled for this Firebase project. Enable it in the Firebase console, under the sign-in method tab of the Auth section. [ Code flow is not enabled for Apple. ]\"",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

    private fun extractResult(authResult: AuthResult?) {
        // Get the user profile with authResult.getUser() and
        // authResult.getAdditionalUserInfo(), and the ID
        // token from Apple with authResult.getCredential().
        Log.i("Prudhvi Log", "onCreate:onSuccess $authResult")
    }

    private val appleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    /*val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    withContext(Dispatchers.Main){
                        Log.i("Prudhvi Log", ": $signInResult")
                        Toast.makeText(this@MainActivity, "Hi ${signInResult.data?.username}", Toast.LENGTH_SHORT).show()
                    }*/
                }
            }
        }

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            binding.pbView.isVisible = false
            if (result.resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    withContext(Dispatchers.Main) {
                        Log.i("Prudhvi Log", ": $signInResult")
                        Toast.makeText(
                            this@MainActivity,
                            "Hi ${signInResult.data?.username}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    /*private fun signInUsingCredentialManager() {
        val credentialManager = CredentialManager.create(this)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val nonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("573183919743-gdkup08hmjat3h0uhb4hhe4hqqj08ok9.apps.googleusercontent.com")
            .setNonce(nonce)
            .build()

        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        lifecycleScope.launch {
            try {
                val result =
                    credentialManager.getCredential(context = this@MainActivity, request = request)

                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                Log.i("Prudhvi Log", "onCreate: ${googleIdTokenCredential.toString()}")
                Log.i("Prudhvi Log", "onCreate: $googleIdToken")
                Toast.makeText(this@MainActivity, "You are signed in!", Toast.LENGTH_SHORT).show()
            } catch (e: GetCredentialException) {
                e.printStackTrace()
            } catch (e: GoogleIdTokenParsingException) {
                e.printStackTrace()
            }
        }
    }*/
}
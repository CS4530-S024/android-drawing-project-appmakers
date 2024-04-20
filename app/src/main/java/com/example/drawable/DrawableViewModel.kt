package com.example.drawable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await

external fun brightness(bmp: Bitmap?, brightness: Float)
external fun invertColors(bmp: Bitmap?)
enum class LoginState {
    NotLogged, SigningUp, LoggedIn
}

data class Drawing(val bitmap: Bitmap, val dPath: DrawingPath)
class DrawableViewModel(private val repository: DrawingRepository) : ViewModel() {
    companion object {
        init {
            System.loadLibrary("drawable")
        }
    }

    //new implementation
    val drawings: Flow<List<Drawing>> = repository.drawings
    val count: Flow<Int> = repository.count
    val usernameFlow = MutableStateFlow<String?>(null)

    class Factory(private val repository: DrawingRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DrawableViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DrawableViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private val bitmapLiveData = MutableLiveData<Bitmap>()
    var currBitmap = bitmapLiveData as LiveData<out Bitmap>
    private val saveColor = MutableLiveData<Int>(Color.BLACK)
    var currColor = saveColor as LiveData<out Int>
    private val _state = MutableStateFlow<LoginState>(LoginState.NotLogged)
    val state: StateFlow<LoginState> = _state

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
        updateLoginState(firebaseAuth.currentUser)
    }

    init {
        auth.addAuthStateListener(authStateListener)

    }


    fun login() {
        _state.value = LoginState.LoggedIn // Update state when logged in
    }

    fun startSignUp() {
        _state.value = LoginState.SigningUp // Update state when starting sign up
    }

    fun signOut() {
        _state.value = LoginState.NotLogged // Update state when signed out
    }

    /**
     * Adds drawing to list
     * @param drawing: The drawing we are inserting into the List
     */
    fun add(drawing: Drawing) {
        viewModelScope.launch {
            repository.saveDrawing(drawing)
        }
    }

    /**
     * Removes drawing from list
     * @param dpath: The drawing path of the file to delete
     */
    fun removeDrawing(dpath: DrawingPath) {
        viewModelScope.launch {
            repository.deleteDrawing(dpath)
        }
    }

    /**
     *
     */
    suspend fun checkForDrawing(name: String): Boolean {
        return repository.nameCheck(name)
    }

    /**
     * Updates the color when its changed
     * @param color: the new color
     */
    fun updateColor(color: Int) {
        saveColor.value = color
        saveColor.value = saveColor.value
    }

    /**
     * Updates the current bitmap when changes happen
     * @param bitmap: the changed bitmap
     */
    fun updateBitmap(bitmap: Bitmap) {
        bitmapLiveData.value = bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     * Sets the current bitmap
     * @param dpath: The drawing path of file to set as current bitmap
     */
    fun setCurrBitmap(dpath: DrawingPath) {
        val drawing = repository.loadDrawing(dpath)
        bitmapLiveData.value = drawing.bitmap
        bitmapLiveData.value = bitmapLiveData.value
    }

    /**
     *
     */
    fun brightenImage() {
        val currentBitmap = bitmapLiveData.value
        if (currentBitmap != null) {
            brightness(currentBitmap, .25F)
            updateBitmap(currentBitmap)
        }
    }

    /**
     *
     */
    fun invertColors() {
        val currentBitmap = bitmapLiveData.value
        if (currentBitmap != null) {
            invertColors(currentBitmap)
            updateBitmap(currentBitmap)

        }
    }

//    /**
//     *
//     */
//    fun setTheUsername(name: String?){
//        usernameFlow.value = name
//        usernameFlow.value = usernameFlow.value
//    }

    fun clear() {
        viewModelScope.launch {
            repository.clearDatabase()
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun updateLoginState(user: FirebaseUser?) {
        _state.value = when (user) {
            null -> LoginState.NotLogged
            else -> LoginState.LoggedIn
        }
    }



    fun log_in(email: String, password: String, letEmKnow: (String) -> Unit, context: Context, onSignIn: () -> Unit) {
        viewModelScope.launch {
            try {
                // Sign in and wait for completion
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = Firebase.auth.currentUser
                val userId = _currentUser.value?.uid ?: throw IllegalStateException("User ID is null")
                val userRef = Firebase.firestore.collection("Usernames").document(userId)

                // Get the username
                val documentSnapshot = userRef.get().await()
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(DrawableUser::class.java) ?: throw IllegalStateException("User data is null")
                    usernameFlow.value = user.username

                    // Proceed only if the username is not null
                    val username = usernameFlow.value ?: throw IllegalStateException("Username is not available.")
                    val storageRef = Firebase.storage.reference
                    val imagesRef = storageRef.child("$username/drawings/")
                    getDrawingsFromFirebase(imagesRef, letEmKnow, onSignIn)
                } else {
                    letEmKnow("Document does not exist.")
                }

            } catch (e: Exception) {
                letEmKnow("Failed to login or fetch user data: ${e.message}")
            }
        }
    }

    private fun getDrawingsFromFirebase(imagesRef: StorageReference, letEmKnow: (String) -> Unit, onSignIn: () -> Unit) {
        viewModelScope.launch {

            val options = BitmapFactory.Options().apply {
                inMutable = true
            }
            imagesRef.listAll().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val listResult = task.result
                    listResult?.items?.forEach { fileRef ->
                        async {

                            var downloadedBitmap: Bitmap? = null
                            fileRef.getBytes(10 * 1024 * 1024).addOnSuccessListener { bytes ->
                                downloadedBitmap =
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)

                                fileRef.metadata.addOnSuccessListener { metadata ->
                                    val lastModified = metadata.updatedTimeMillis
                                    val drawing = Drawing(
                                        downloadedBitmap!!,
                                        DrawingPath(lastModified, fileRef.name)
                                    )
                                    add(drawing)
                                }.addOnFailureListener { e ->
                                    letEmKnow("Failed to get metadata: ${e.message}")
                                }
                            }.addOnFailureListener { e ->
                                letEmKnow("Failed to download image: ${e.message}")
                            }
                        }
                    }
//                    onSignIn()
                }
            }
//                .addOnFailureListener { onSignIn() }
        }
    }


    fun register(
        username: String,
        email: String,
        password: String,
        letEmKnow: (String) -> Unit,
        onSignUpClicked: () -> Unit
    ) {
        usernameFlow.value = username
        viewModelScope.launch {
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { _ ->
                    _currentUser.value = Firebase.auth.currentUser
                    val userDoc = Firebase.firestore.collection("Usernames").document(Firebase.auth.currentUser!!.uid)
                    val drawableUser = DrawableUser(username)
                    userDoc.set(drawableUser)

                    onSignUpClicked()
                }
                .addOnFailureListener { _ ->
                    letEmKnow("Failed while signing up")
                }
        }

    }

    fun update_user(
        username: String,
        email: String,
        unChanged: Boolean,
        emChanged: Boolean,
        letEmKnow: (String) -> Unit
    ) {
        if (emChanged) {
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _currentUser.value!!.verifyBeforeUpdateEmail(email)
                    .addOnSuccessListener { _ ->
                        letEmKnow("Email address successfully changed :)") // doesnt work lol
                    }
            } else {
                letEmKnow("Enter a valid email address >:(")
            }
        }

        if (unChanged) {
            val userUpdates = mapOf(
                "username" to username
            )
            Firebase.firestore.collection("Usernames")
                .document(_currentUser.value!!.uid)
                .update(userUpdates)
                .addOnSuccessListener {
                    letEmKnow("Username successfully changed!")
                    usernameFlow.value = username
                }
                .addOnFailureListener { e ->
                    letEmKnow("Failed to change username :( ${e.message}")
                }
        }
    }

    fun sign_out(letEmKnow: (String) -> Unit, onSignOut: () -> Unit) {
        viewModelScope.launch {
            try {
                // First, get the drawings
                val drawings = repository.drawings.first()

                // Initiate all upload tasks and wait for them to complete
                val tasks = drawings.map { drawing ->
                    async {
                        val data = ByteArrayOutputStream().use { baos ->
                            drawing.bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos)
                            baos.toByteArray()  // This creates the byte array you need to upload
                        }
                        val fileName = drawing.dPath.name
                        val fileRef = Firebase.storage.reference.child("${usernameFlow.value}/pictures/$fileName")

                        try {
                            fileRef.putBytes(data).await()  // Upload the byte array and wait for it to complete
                            letEmKnow("Upload successful for $fileName")
                        } catch (e: Exception) {
                            letEmKnow("Upload failed for $fileName: ${e.message}")
                            throw e  // Rethrow to handle in the overarching catch
                        }
                    }
                }

                tasks.awaitAll()  // Wait for all the upload tasks to complete

                // All uploads have completed here, now clear and sign out
                clear()  // Assuming this method clears some local data
                Firebase.auth.signOut()  // Sign out the user
                usernameFlow.value = null  // Clear the username state
                _currentUser.value = Firebase.auth.currentUser
                onSignOut()  // Notify that sign-out has completed

            } catch (e: Exception) {
                // Handle any errors from uploads or other operations
                letEmKnow("An error occurred: ${e.message}")
            }
        }
    }

}
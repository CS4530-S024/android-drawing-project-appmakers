package com.example.drawable

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Patterns
import android.widget.Toast
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
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                _currentUser.value = Firebase.auth.currentUser
                val userId = currentUser.value!!.uid
                val userRef = Firebase.firestore.collection("Usernames").document(userId)
                userRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val user =
                                documentSnapshot.toObject(DrawableUser::class.java)
                            usernameFlow.value = user!!.username
                        }
                    }
                    .addOnFailureListener { e ->
                        letEmKnow("Error getting document: ${e.message}")
                    }

                val storageRef =
                    Firebase.storage.reference
                val imagesRef = storageRef.child("${usernameFlow}/drawings/")
                get_drawings_from_firebase(imagesRef, letEmKnow, context)
                onSignIn()
            }
            .addOnFailureListener { e ->
                letEmKnow("Failed to login: ${e.message}")
            }
    }

    private fun get_drawings_from_firebase(
        imagesRef: StorageReference,
        letEmKnow: (String) -> Unit,
        context: Context
    ) {
        imagesRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { fileRef ->

                    fileRef.metadata.addOnSuccessListener { metadata ->
                        val lastModified = metadata.updatedTimeMillis
                        val localFile =
                            File(
                                context.filesDir,
                                fileRef.name
                            ) // dont know if this is the right way

                        fileRef.getFile(localFile)
                            .addOnSuccessListener {
                                processBitmap(
                                    localFile,
                                    lastModified,
                                    fileRef.name
                                )
                            }
                            .addOnFailureListener {
                                letEmKnow("Failed to download file.")
                            }
                    }
                        .addOnFailureListener {
                            letEmKnow("Failed to retrieve file metadata.")
                        }
                }
            }
    }

    private fun processBitmap(localFile: File, lastModified: Long, fileName: String) {
        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        bitmap.recycle()  // Clean up the original bitmap immediately
        val drawing = Drawing(mutableBitmap, DrawingPath(lastModified, fileName))
        add(drawing)
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


    fun sign_out(letEmKnow: (String) -> Unit, onSignOut: () -> Unit,){
        // Move drawing from room to firebase storage
        viewModelScope.launch {
            val drawings = drawings.first()
            val storageRef =
                Firebase.storage.reference  // Get a reference to Firebase Storage
            // Upload each drawing to Firebase Storage
            drawings.forEachIndexed { _, drawing ->
                val baos =
                    ByteArrayOutputStream() // Convert the bitmap to a byte array as before
                val bitmap = drawing.bitmap
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos)
                val data = baos.toByteArray()
                // Delete picture from room
                val fileName = drawing.dPath.name
                val fileRef =
                    storageRef.child("${usernameFlow}/pictures/$fileName")
                val uploadTask =
                    fileRef.putBytes(data)  // Upload the byte array to Firebase Storage
                // Handle the upload task's response
                uploadTask
                    .addOnFailureListener { e ->
                        letEmKnow("Upload failed because: ${e.message}")
                    }
                    .addOnSuccessListener {
                        letEmKnow("Upload Successful")
                    }
            }
        }
        clear()
        Firebase.auth.signOut()
        usernameFlow.value = null
        onSignOut()
    }

}
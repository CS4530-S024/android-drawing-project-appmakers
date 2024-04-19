package com.example.drawable

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    /**
     *
     */
    fun setTheUsername(name: String?){
        usernameFlow.value = name
        usernameFlow.value = usernameFlow.value
    }

    fun clear(){
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

    fun log_in(email:String, password:String){

    }

    fun sign_out(email:String, password:String){

    }

}
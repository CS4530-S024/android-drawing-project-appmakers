package com.example.drawable


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.regex.Pattern


class DrawingLoginNRegister : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStore: FirebaseFirestore
    private val myViewModel: DrawableViewModel by activityViewModels {
        val application = requireActivity().application as DrawableApplication
        DrawableViewModel.Factory(application.drawingRepository)
    }
    private var currUser: FirebaseUser? = null
    private var currUsername: String? = null


    /**
     * Creates the view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = ComposeView(requireContext())

        view.apply {
            setContent {
                MainScreen(myViewModel)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            myViewModel.usernameFlow.collect { updatedUsername ->
                currUsername = updatedUsername ?: "Guest"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            myViewModel.currentUser.collect{ user->
                currUser = user

            }
        }


        mAuth = FirebaseAuth.getInstance()
        mStore = FirebaseFirestore.getInstance()
        return view
    }

    /**
     * Main Composable that decides which screen to show based on a boolean flag.
     */
    @Composable
    fun MainScreen(viewModel: DrawableViewModel) {
        val state by viewModel.state.collectAsState()
        val currentUser by viewModel.currentUser.collectAsState()

        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current

        val modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            )


        when (state) {
        // If user is logging in
            LoginState.NotLogged -> Login(modifier) {
                myViewModel.startSignUp()
            }
         //If user is signing up
            LoginState.SigningUp -> Register(modifier) {
                myViewModel.login()
            }
         //If user is signed in
            LoginState.LoggedIn -> UserPage(modifier) {
                myViewModel.signOut()
            }
        }

    }

    /**
     *
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Login(modifier: Modifier, onRegisterClicked: () -> Unit) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        val isLoggingIn = remember { mutableStateOf(false) }
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(innerPadding)

            ) {
                Logo()

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "Log in",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                EmailTextField(email) { email = it }
                Spacer(modifier = Modifier.height(20.dp))
                PasswordTextField(password) { password = it }
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Username or password cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isLoggingIn.value = true
                            // Proceed with login
                            mAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    // need to load drawings from firebase storage into room
                                    currUser = Firebase.auth.currentUser
                                    val userId =
                                        currUser!!.uid  // Ensure this is non-null or handle the null case appropriately
                                    val userRef = mStore.collection("Usernames").document(userId)
//                                    var username: String? = null

                                    userRef.get()
                                        .addOnSuccessListener { documentSnapshot ->
                                            if (documentSnapshot.exists()) {
                                                val user =
                                                    documentSnapshot.toObject(DrawableUser::class.java)
                                                myViewModel.setTheUsername(user!!.username)
                                                // Use the username as needed
//                                                println("Retrieved username: $username")
                                                Toast.makeText(
                                                    context,
                                                    "Retrieved username: $currUsername",
                                                    Toast.LENGTH_SHORT
                                                ).show()
//                                                myViewModel.setTheUsername(username!!)
                                            } else {
                                                println("No such document!")
                                            }
                                        }
                                        .addOnFailureListener { exception ->
                                            println("Error getting document: ${exception.message}")
                                        }

                                    val storageRef =
                                        Firebase.storage.reference
                                    val imagesRef = storageRef.child("${currUsername}/drawings/")

                                    imagesRef.listAll()
                                        .addOnSuccessListener { listResult ->
                                            listResult.items.forEach { fileRef ->

                                                fileRef.metadata.addOnSuccessListener { metadata ->
                                                    val lastModified = metadata.updatedTimeMillis
                                                    val localFile =
                                                        File(context.filesDir, fileRef.name)

                                                    fileRef.getFile(localFile)
                                                        .addOnSuccessListener {
                                                            processBitmap(
                                                                localFile,
                                                                lastModified,
                                                                fileRef.name
                                                            )
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(
                                                                context,
                                                                "Failed to download file.",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to retrieve file metadata.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Failed to list files.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            isLoggingIn.value = false
                                        }

                                    Toast.makeText(
                                        context,
                                        "You are now logged in :)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                .addOnFailureListener {
                                    Toast.makeText(
                                        context,
                                        "Your credentials r wrong :P :(",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            isLoggingIn.value = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))
                ) {
                    Text("Login")
                }
                Spacer(modifier = Modifier.height(75.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Don't have an account? ")
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onRegisterClicked() },
                        modifier = Modifier.background(Color.Transparent),
                        colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))
                    ) {
                        Text("Sign up")
                    }
                }
                if (isLoggingIn.value) {
                    Loading()
                }
            }
        }
    }

    fun processBitmap(localFile: File, lastModified: Long, fileName: String) {
        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        bitmap.recycle()  // Clean up the original bitmap immediately

        val drawing = Drawing(mutableBitmap, DrawingPath(lastModified, fileName))
        myViewModel.add(drawing)
    }

    /**
     *
     */
    @Composable
    fun Loading() {
        Dialog(onDismissRequest = {}) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp) // You can adjust the size
                )
            }
        }
    }


    /**
     *
     */
    @Composable
    fun Logo() {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(120.dp) // You can adjust the size as needed
            )
            Spacer(Modifier.width(2.dp)) // Adds some space between the icon and text

            Text(
                "Drawable", style = TextStyle(
                    color = Color.Black,
                    fontSize = 30.sp,

                    )
            )
        }
    }

    /**
     *
     */
    @Composable
    fun EmailTextField(email: String, onEmailChange: (String) -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier
                    .width(350.dp)
                    .border(
                        border = BorderStroke(1.dp, Color(0xFFC1C7D6)),
                        shape = RoundedCornerShape(50.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.email), // Load the drawable resource
                        contentDescription = "login",
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xFFC1C7D6)
                    )
                }
            )
        }
    }

    /**
     *
     */
    @Composable
    fun PasswordTextField(password: String, onPasswordChange: (String) -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier
                    .width(350.dp)
                    .border(
                        border = BorderStroke(1.dp, Color(0xFFC1C7D6)),
                        shape = RoundedCornerShape(50.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "login",
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xFFC1C7D6)
                    )
                }
            )
        }
    }

    /**
     *
     */
    @Composable
    fun UsernameTextField(nickName: String, onNameChange: (String) -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = nickName,
                onValueChange = onNameChange,
                label = { Text("Username") },
                modifier = Modifier
                    .width(350.dp)
                    .border(
                        border = BorderStroke(1.dp, Color(0xFFC1C7D6)),
                        shape = RoundedCornerShape(50.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "login",
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xFFC1C7D6)
                    )
                }
            )
        }
    }

    /**
     *
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Register(modifier: Modifier, onSignUpClicked: () -> Unit) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var username by rememberSaveable { mutableStateOf("") }
        val isRegistering = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(innerPadding)
            ) {

                Spacer(modifier = Modifier.height(100.dp))

                Text(
                    text = "Sign Up",
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                )

                Spacer(modifier = Modifier.height(100.dp))
                UsernameTextField(username) { username = it }
                Spacer(modifier = Modifier.height(20.dp))
                EmailTextField(email) { email = it }
                Spacer(modifier = Modifier.height(25.dp))
                PasswordTextField(password) { password = it }
                Spacer(modifier = Modifier.height(25.dp))

                Button(
                    onClick = {
                        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Username, Password, or Email cannot be empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isRegistering.value = true
                            coroutineScope.launch {
                                mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener { _ ->

                                        currUser = Firebase.auth.currentUser

                                        mStore.collection("Usernames")
                                            .document(currUser!!.uid)
                                            .set(DrawableUser(username))

                                        isRegistering.value =
                                            false // Stop showing the progress indicator
                                        onSignUpClicked()
                                        myViewModel.setTheUsername(username)
                                    }
                                    .addOnFailureListener { _ ->
                                        // Handle failure, exception contains the Exception object
                                        isRegistering.value =
                                            false // Stop showing the progress indicator
                                    }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))
                ) {
                    Text("Sign up")
                }
                if (isRegistering.value) {
                    Loading()
                }
            }
        }
    }

    /**
     *
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UserPage(modifier: Modifier, onSignOutClicked: () -> Unit) {
        var email by rememberSaveable { mutableStateOf(currUser!!.email) }
        var username by rememberSaveable { mutableStateOf(currUsername) }
        val isSigningOut = remember { mutableStateOf(false) }
        val unChanged = remember { mutableStateOf(false) }
        val emChanged = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { findNavController().popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .padding(innerPadding)
            ) {
                Logo()

                Spacer(modifier = Modifier.height(100.dp))
                UsernameTextField(username!!) {
                    username = it
                    unChanged.value = true
                }
                Spacer(modifier = Modifier.height(20.dp))
                EmailTextField(email!!) {
                    email = it
                    emChanged.value = true
                }

                Spacer(modifier = Modifier.height(100.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Allows the user to change their username and email
                    Button(
                        onClick = {
                            if (emChanged.value) {
                                if (Patterns.EMAIL_ADDRESS.matcher(email!!).matches()) {
                                    currUser!!.verifyBeforeUpdateEmail(email!!)
                                        .addOnSuccessListener { _ ->
                                            Toast.makeText(
                                                context,
                                                "Email address successfully changed :)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    emChanged.value = false
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Enter a valid email address >:(",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            if (unChanged.value) {
                                val userUpdates = mapOf(
                                    "username" to username
                                )
                                mStore.collection("Usernames")
                                    .document(currUser!!.uid)
                                    .update(userUpdates)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Username successfully changed!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Failed to change username :(",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                unChanged.value = false
                            }

                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))
                    ) {
                        Text("Update User")
                    }
                    Spacer(modifier = Modifier.width(30.dp))

                    Button(
                        onClick = {
                            isSigningOut.value = true
                            // Move drawing from room to firebase storage
                            coroutineScope.launch {
                                val drawings = myViewModel.drawings.first()
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
                                        storageRef.child("${username}/pictures/$fileName")
                                    val uploadTask =
                                        fileRef.putBytes(data)  // Upload the byte array to Firebase Storage
                                    // Handle the upload task's response
                                    uploadTask
                                        .addOnFailureListener {
                                        }
                                        .addOnSuccessListener {
                                        }
                                }
                            }
                            myViewModel.clear()
                            currUser = null
                            mAuth.signOut()
                            myViewModel.setTheUsername(null)
                            onSignOutClicked()
                            isSigningOut.value = false
                            findNavController().popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))
                    ) {
                        Text("Sign Out")
                    }

                }
                if (isSigningOut.value) {
                    Loading()
                }
            }
        }
    }

    @Preview(showBackground = true, widthDp = 412, heightDp = 892)
    @Composable
    fun LoginPreview() {
        Login(Modifier) {}
    }

    @Preview(showBackground = true, widthDp = 412, heightDp = 892)
    @Composable
    fun RegisterPreview() {
        Register(Modifier) {}
    }

    @Preview(showBackground = true, widthDp = 412, heightDp = 892)
    @Composable
    fun UserPagePreview() {
        UserPage(Modifier) {}
    }
}
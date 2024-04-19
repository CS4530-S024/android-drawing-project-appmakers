package com.example.drawable


import android.os.Bundle
import android.view.Gravity
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser


class DrawingLoginNRegister : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStore: FirebaseFirestore
    private val myViewModel: DrawableViewModel by activityViewModels {
        val application = requireActivity().application as DrawableApplication
        DrawableViewModel.Factory(application.drawingRepository)
    }
    private var currUser: FirebaseUser? = null



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
                MainScreen()
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
    fun MainScreen() {
        val notLogged = remember { mutableStateOf(true) }
        val signingUp = remember { mutableStateOf(false) }
        val loggedIn = remember { mutableStateOf(false) }

        // If user is not logged in
        if (notLogged.value) {
            Login { notLogged.value = false
            signingUp.value = true}
        }
        // If user is signing up
        else if(signingUp.value){
            Register { signingUp.value = false
                loggedIn.value = true
            }
        }
        // If user is logged in
        else if(currUser != null && loggedIn.value){
            UserPage()
        }
    }

    /**
     *
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Login(onRegisterClicked: () -> Unit) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
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
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
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

                            val toast = Toast.makeText(
                                context,
                                "Username or password cannot be empty",
                                Toast.LENGTH_SHORT
                            )
                            toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 100)
                            toast.show()
                        } else {
                            // Proceed with login
//                            Firebase.auth.signInWithEmailAndPassword(email, password)
//                                .addOnCompleteListener(this@MainActivity) { task ->
//                                    if (task.isSuccessful) {
//                                        user = Firebase.auth.currentUser
//                                    } else {
//                                        email = "login failed, try again"
//                                    }


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
            }
        }
    }

    /**
     *
     */
    @Composable
    fun Logo(){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(120.dp) // You can adjust the size as needed
            )
            Spacer(Modifier.width(2.dp)) // Adds some space between the icon and text

            Text("Drawable", style = TextStyle(
                color = Color.Black,
                fontSize = 30.sp,

            ))
        }
    }

    /**
     *
     */
    @Composable
    fun EmailTextField(email: String, onEmailChange: (String) -> Unit) {

        Row(verticalAlignment = Alignment.CenterVertically){

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
                    focusedContainerColor = Color.Transparent),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.email), // Load the drawable resource
                        contentDescription = "login",
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xFFC1C7D6))
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
                    focusedContainerColor = Color.Transparent),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                    Icons.Default.Lock,
                    contentDescription = "login",
                    modifier = Modifier.size(30.dp),
                    tint = Color(0xFFC1C7D6))},

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
                    focusedContainerColor = Color.Transparent),
                leadingIcon =  {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "login",
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xFFC1C7D6))},

            )
        }
    }

    /**
     *
     */
    @Composable
    fun Register(onSignUpClicked: () -> Unit){
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var username by rememberSaveable { mutableStateOf("") }
        val isRegistering = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        Column(horizontalAlignment = Alignment.CenterHorizontally){

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

            Button(onClick = {
                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    Toast.makeText(context, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()

                } else {
                    isRegistering.value = true
                    coroutineScope.launch {
                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { _ ->

                                currUser = Firebase.auth.currentUser

                                mStore.collection("Usernames")
                                    .document(currUser!!.uid)
                                    .set(DrawableUser(username))

                                isRegistering.value = false // Stop showing the progress indicator
                                onSignUpClicked()
                                findNavController().popBackStack()
                            }
                            .addOnFailureListener { _ ->
                                // Handle failure, exception contains the Exception object
                                isRegistering.value = false // Stop showing the progress indicator
                            }
                    }
                }
            },
                colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))){
                Text("Sign up")
            }
            if (isRegistering.value) {
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
        }
    }

    /**
     *
     */
    @Composable
    fun UserPage(){

        // Move drawing from room to firebase storage

        /*coroutineScope.launch {
            val drawings = myViewModel.drawings.first()

                    // All should be moved to sign out
                    // Get a reference to Firebase Storage
                    val storageRef = Firebase.storage.reference

                    // Upload each drawing to Firebase Storage
                    drawings.forEachIndexed { _, drawing ->
                        // Convert the bitmap to a byte array as before
                        val baos = ByteArrayOutputStream()
                        val bitmap = drawing.bitmap
                        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos)
                        val data = baos.toByteArray()
                        // Delete picture from room
                        // Create a unique file name or path for each image
                        val fileName = drawing.dPath.name
                        val fileRef = storageRef.child("${username}/pictures/$fileName")
                        // Upload the byte array to Firebase Storage
                        val uploadTask = fileRef.putBytes(data)
                        // Handle the upload task's response
                        uploadTask
                            .addOnFailureListener {
//                                            Log.e("PICUPLOAD", "Failed to upload image $fileName: ${e.message}")
                            }
                            .addOnSuccessListener {
//                                            Log.d("PICUPLOAD", "Successfully uploaded image $fileName")
                            }
                    }


                    isRegistering.value = false // Stop showing the progress indicator
                    findNavController().popBackStack()

                }*/
    }


    @Preview(showBackground = true,  widthDp = 412, heightDp = 892)
    @Composable
    fun LoginPreview() {
            Login {}
    }

    @Preview(showBackground = true,  widthDp = 412, heightDp = 892)
    @Composable
    fun RegisterPreview() {
        Register{}
    }

//    @Preview(showBackground = true,  widthDp = 412, heightDp = 892)
//    @Composable
//    fun UserPagePreview() {
//        UserPage {}
//    }

}
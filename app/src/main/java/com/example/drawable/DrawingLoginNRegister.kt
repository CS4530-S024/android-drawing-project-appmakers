package com.example.drawable


import android.graphics.Typeface
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.drawable.databinding.FragmentDrawingsListBinding
import kotlinx.coroutines.launch

class DrawingLoginNRegister : Fragment() {

    /**
     * Creates the view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        view.apply {
            setContent {
                Login()
            }
        }

        return view
    }




    @Composable
    fun Login(){
        var username by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        val context = LocalContext.current

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
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
            Spacer(modifier = Modifier.height(15.dp))
            UsernameTextField(username) { username = it }
            Spacer(modifier = Modifier.height(15.dp))
            PasswordTextField(password) { password = it }
            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    // Proceed with login
                }
            },
                colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))){
                Text("Login")
            }

            Spacer(modifier = Modifier.height(75.dp))

            Row(verticalAlignment = Alignment.CenterVertically){
                Text("Don't have an account? ")
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = { },
                    modifier = Modifier.background(Color.Transparent),
                    colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))){
                    Text("Sign up")
                }
            }
        }
    }

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



//    @Composable
//    fun BubblyRainbowText(text: String, fontSize: TextUnit) {
//        val gradient = Brush.linearGradient(
//            colors = listOf(
//                Color.Red, Color.Magenta, Color.Blue, Color.Cyan, Color.Green, Color.Yellow, Color.Red
//            ),
//            start = Offset.Zero,
//            end = Offset.Infinite
//        )
//
//        Text(
//            text = text,
//            fontSize = fontSize,
//            modifier = Modifier.drawWithContent {
//                drawContent()
//                drawIntoCanvas { canvas ->
//                    val textPaint = Paint().asFrameworkPaint().apply {
//                        this.isAntiAlias = true
//                        this.textSize = fontSize.value * density
//                        typeface = Typeface.DEFAULT_BOLD // Change this to a custom typeface if you have a 'bubbly' font
//                        shader = gradient.asAndroidShader()
//                    }
//                    canvas.nativeCanvas.drawText(
//                        text,
//                        0f,
//                        0f + fontSize.toPx(),
//                        textPaint
//                    )
//                }
//            }
//        )
//    }




    @Composable
    fun UsernameTextField(username: String, onUsernameChange: (String) -> Unit) {

        Row(verticalAlignment = Alignment.CenterVertically){

            TextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.border(border = BorderStroke(1.dp, Color(0xFFC1C7D6)), shape = RoundedCornerShape(50.dp)),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent),
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "login",
                        modifier = Modifier.size(30.dp),
                        tint = Color(0xFFC1C7D6))
                }

            )
        }
    }

    @Composable
    fun PasswordTextField(password: String, onPasswordChange: (String) -> Unit) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            TextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.border(border = BorderStroke(1.dp, Color(0xFFC1C7D6)), shape = RoundedCornerShape(50.dp)),
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

    @Composable
    fun Register(){
        var username by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        val context = LocalContext.current

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Logo()

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "Sign Up",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
            )
            Spacer(modifier = Modifier.height(15.dp))

            UsernameTextField(username) { username = it }
            Spacer(modifier = Modifier.height(15.dp))
            PasswordTextField(password) { password = it }
            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Username or password cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    // Proceed with login
                }
            },
                colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))){
                Text("Login")
            }

            Spacer(modifier = Modifier.height(75.dp))

            Row(verticalAlignment = Alignment.CenterVertically){
                Text("Don't have an account? ")
                Spacer(modifier = Modifier.width(10.dp))
                Button(onClick = { },
                    modifier = Modifier.background(Color.Transparent),
                    colors = ButtonDefaults.buttonColors(Color(0xFF6C80E8))){
                    Text("Sign up")
                }
            }
        }
    }

    @Preview(showBackground = true,  widthDp = 412, heightDp = 892)
    @Composable
    fun LoginPreview() {
            Login()
    }

    @Preview(showBackground = true,  widthDp = 412, heightDp = 892)
    @Composable
    fun RegisterPreview() {
        Register()
    }

}
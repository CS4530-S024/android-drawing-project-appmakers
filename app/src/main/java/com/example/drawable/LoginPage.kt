package com.example.drawable


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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment

class LoginPage : Fragment() {

    @Composable
    fun Login(){

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Logo()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Log in",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
            )
            Spacer(modifier = Modifier.height(15.dp))
            UsernameTextField()
            Spacer(modifier = Modifier.height(15.dp))
            PasswordTextField()
            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = { },
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
        Image(
            painter = painterResource(id = R.drawable.paint_palette_svgrepo_com),
            contentDescription = "",
            contentScale = ContentScale.Fit
        )
    }

    @Composable
    fun UsernameTextField(){
        var username by rememberSaveable { mutableStateOf("") }

        Row(verticalAlignment = Alignment.CenterVertically){
//            Icon(
//                Icons.Default.Person,
//                contentDescription = "login",
//                modifier = Modifier.size(35.dp),
//                tint = Color(0xFFC1C7D6))
//            Spacer(modifier = Modifier.width(7.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
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
    fun PasswordTextField() {
        var password by rememberSaveable { mutableStateOf("") }

        Row(verticalAlignment = Alignment.CenterVertically) {

//            Spacer(modifier = Modifier.width(7.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
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
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
            Logo()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Sign Up",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
            )
            Spacer(modifier = Modifier.height(15.dp))

            UsernameTextField()
            Spacer(modifier = Modifier.height(15.dp))
            PasswordTextField()
            Spacer(modifier = Modifier.height(15.dp))

            Button(onClick = { },
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
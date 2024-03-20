package com.example.drawable

import android.os.Bundle
import androidx.compose.material3.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import com.example.drawable.ui.theme.DrawingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        var currentDrawing: Drawing
        val vm: DrawableViewModel by viewModels {
            Factory((application as DrawableApplication).drawingRepository)
        }

        setContent {
            DrawingTheme {
                Scaffold(floatingActionButton = {
                    ExtendedFloatingActionButton(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        onClick = {
                            currentDrawing = vm.fetchDrawing()
                        }) {
                        Text(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            text = "Fetch Drawing")
                    }}) {
                    Surface(
                        modifier = Modifier.padding(it).fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column {
                            Drawing(
                                drawing = currentDrawing,
                                modifier = Modifier.padding(8.dp).fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            )
                        }
                    }

                    val list by vm.drawings.collectAsState(listOf())

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (drawing in list!!) {
                            item {
                                Drawing(drawing.name, drawing.bitmap, drawing.date)
                            }
                        }
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

@Composable
fun Drawing(drawing: Drawing, modifier: Modifier = Modifier) {
    Card(modifier = modifier
        .background(MaterialTheme.colorScheme.surface)
        .padding(8.dp)
        .fillMaxWidth()
    ) {
        Button(
            modifier = modifier.padding(16.dp),
            contentPadding = PaddingValues(top = 0.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
            onClick = {
                openDrawing(drawing.name)
            }) {
            Text(
                text = "Drawing",
                style = MaterialTheme.typography.labelSmall,
                modifier = modifier.padding(0.dp)
            )
        }
        Text(
            text = drawing.name,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = modifier.padding(16.dp)
        )
    }
}

fun openDrawing(drawingName: String) {

}

/*
@PreviewLightDark
@Preview(showBackground = true)
@Composable
fun DrawingPreview() {
    DrawingTheme {

    }
}*/

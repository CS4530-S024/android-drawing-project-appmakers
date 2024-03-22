package com.example.drawable

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomTests {

    private lateinit var drawingDatabase: DrawingDatabase
    private lateinit var drawingDao: DrawingDAO

    val bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        drawingDatabase = Room.inMemoryDatabaseBuilder(context, DrawingDatabase::class.java).build()
        drawingDao = drawingDatabase.drawingDao()
    }

    @Test
    @Throws(Exception::class)
    fun test_insert_drawing() = runBlocking {
        val timestamp = 1000000000L
        val name = "Riley Test"
        val dp = DrawingPath(timestamp, name)

        drawingDao.insertImage(dp)
        val drawings = drawingDatabase.drawingDao().getAllPaths()
        assertEquals(1, drawings.count())
    }

    @Test
    @Throws(Exception::class)
    fun test_remove_drawing() = runBlocking {
        val timestamp = 1000000000L
        val name = "Riley Test2"

        drawingDao.insertImage(DrawingPath(timestamp, name))
        drawingDao.deleteDrawing(DrawingPath(timestamp, name))
        val drawings = drawingDatabase.drawingDao().getAllPaths()
        assertEquals(0, drawings.count())
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        drawingDatabase.close()
    }
}
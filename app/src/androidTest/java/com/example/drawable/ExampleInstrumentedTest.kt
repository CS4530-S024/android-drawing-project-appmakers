package com.example.drawable

import android.content.Context
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var drawingDatabase: DrawingDatabase
    private lateinit var drawingDao: DrawingDAO

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        drawingDatabase = Room.inMemoryDatabaseBuilder(context, DrawingDatabase::class.java).build()
        drawingDao = drawingDatabase.drawingDao()
    }

    @Test
    fun test_add_item() = runBlocking {
        val dPath = DrawingPath(System.currentTimeMillis(), "Riley Test")
        drawingDao.insertImage(dPath)

        val result: Int = drawingDao.getDrawingCount().first()
        assertEquals(1, result)
    }

    @Test
    fun test_add_few_items() = runBlocking {
        val dPath = DrawingPath(System.currentTimeMillis(), "Riley Test2")
        drawingDao.insertImage(dPath)
        val dPath2 = DrawingPath(System.currentTimeMillis(), "Riley Test3")
        drawingDao.insertImage(dPath2)
        val dPath3 = DrawingPath(System.currentTimeMillis(), "Riley Test4")
        drawingDao.insertImage(dPath3)

        val result: Int = drawingDao.getDrawingCount().first()
        assertEquals(3, result)
    }

    @Test
    fun test_add_lots_of_items() = runBlocking {
        for (i in 1..100) {
            val dPath = DrawingPath(System.currentTimeMillis(), "test " + i)
            drawingDao.insertImage(dPath)
        }

        val result: Int = drawingDao.getDrawingCount().first()
        assertEquals(100, result)
    }

    @Test
    fun test_add_then_remove() = runBlocking {
        val dPath = DrawingPath(System.currentTimeMillis(), "Riley Test")
        var result: Int = drawingDao.getDrawingCount().first()
        assertEquals(0, result)
        drawingDao.insertImage(dPath)
        result = drawingDao.getDrawingCount().first()
        assertEquals(1, result)
        drawingDao.deleteDrawing(dPath)
        result = drawingDao.getDrawingCount().first()
        assertEquals(0, result)
    }

    @Test
    fun test_add_then_remove_lots() = runBlocking {
        for (i in 1..100) {
            val dPath = DrawingPath(System.currentTimeMillis(), "test " + i)
            drawingDao.insertImage(dPath)
            drawingDao.deleteDrawing(dPath)
            // Wait for the deletion operation to complete before proceeding
            val result = drawingDao.getDrawingCount().first()
            assertEquals(0, result)
        }
    }

    @After
    fun tearDown() {
        drawingDatabase.close()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.drawable", appContext.packageName)
    }
}
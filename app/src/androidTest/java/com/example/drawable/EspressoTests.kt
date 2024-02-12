package com.example.drawable

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class EspressoTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

//  tests for the drawing canvas page //
    @Test
    fun testBackButton() {
        onView(withId(R.id.back_button)).check(matches(isDisplayed()))
        onView(withId(R.id.back_button)).perform(click())
        // verify that it navigates back to the recycler view / list of paintings
    }

    @Test
    fun testColorPicker() {
        onView(withId(R.id.pallete)).check(matches(isDisplayed()))
        onView(withId(R.id.pallete)).perform(click())
        // verify that the color picker popup shows
        // verify that the user's selected color changes from default --> picked one
    }

    @Test
    fun testPaintBrushButton() {
        onView(withId(R.id.paintBrush)).check(matches(isDisplayed()))
        onView(withId(R.id.paintBrush)).perform(click())
        // verify that the paint brush button opens the pop up with the size adjuster
    }

    @Test
    fun testPaintBrushFunctionality() {
        onView(withId(R.id.paintBrush)).perform(click())
        // verify that the user has drawn something where they touched the screen
    }

    @Test
    fun testPaintBucketButton() {
        onView(withId(R.id.paintBucket)).check(matches(isDisplayed()))
        onView(withId(R.id.paintBucket)).perform(click())
        onView(withId(R.id.canvas)).perform(click())
        // verify that the paint bucket has colored the background the intended color
        // how to check the existing color?
    }

    @Test
    fun testSprayCanButton() {
        onView(withId(R.id.spraycan)).check(matches(isDisplayed()))
        onView(withId(R.id.spraycan)).perform(click())
        // verify that the user has drawn with the spray can where they touched the screen
    }

    @Test
    fun testEraserButton() {
        onView(withId(R.id.eraser)).check(matches(isDisplayed()))
        onView(withId(R.id.eraser)).perform(click())
        // verify that the user has erased where they touched the screen
    }

//  tests for the drawings list page //
    @Test
    fun testClickDrawing() {
        // on the view with the id item of each recycler item ... [to do later]
    }
}
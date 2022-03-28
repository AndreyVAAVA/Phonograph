package com.kabouzeid.gramophone.ui.activities

import android.R
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kabouzeid.gramophone.ui.activities.MainActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    var activityActivityTestRule = ActivityTestRule(
        MainActivity::class.java
    )

    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getTargetContext()
        assertEquals("com.kabouzeid.gramophone.ui.activities", context.packageName)
    }

    @Test
    fun clickButtonHome() {
        Espresso.onView(withId(R.id.navigation_home))
            .perform(click()).check(matches(ViewMatchers.isDisplayed()))
    }
}
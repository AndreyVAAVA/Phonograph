package com.kabouzeid.gramophone.ui.activities


import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    // yes I know, ActivityTestRule deprecated and it's better to use ActivityScenarioRule
    // but this new google "solution" doesn't work with vm and my physical phone.
    // java.lang.AssertionError: Activity never becomes requested state "[DESTROYED, CREATED, STARTED, RESUMED]" (last lifecycle transition = "PRE_ON_CREATE")
    // solutions that was on StackOverflow, doesn't helped
    var activityActivityTestRule = ActivityTestRule(
        MainActivity::class.java
    )

    @Test fun useAppContext() {
        /*val scenario: ActivityScenario<*> = activityActivityTestRule.scenario
        scenario.moveToState(Lifecycle.State.RESUMED);    // Moves the activity state to State.RESUMED.
        scenario.moveToState(Lifecycle.State.STARTED);    // Moves the activity state to State.STARTED.
        scenario.moveToState(Lifecycle.State.CREATED);    // Moves the activity state to State.CREATED.
        scenario.moveToState(Lifecycle.State.DESTROYED);*/
        Espresso.onView(withText("Hello world!")).check(matches(isDisplayed()))
        /*val context = InstrumentationRegistry.getInstrumentation().context
        assertEquals("com.kabouzeid.gramophone.debug.test", context.packageName)*/
    }

    /*@Test
    fun clickButtonHome() {

        //NavigationViewActions.navigateTo(R.id.nav_folders)
        //Espresso.onView(ViewMatchers.withText("Folders")).perform(click())
    }*/
}
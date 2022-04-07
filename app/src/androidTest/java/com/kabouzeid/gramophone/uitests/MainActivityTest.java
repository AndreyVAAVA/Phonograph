package com.kabouzeid.gramophone.uitests;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import com.kabouzeid.gramophone.ui.activities.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
class MainActivityTest {
    @Rule
    ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Before
    public void useAppContext() {

    }


    @Test
    public void clickButtonHome() {
        Espresso.onView(withId(com.kabouzeid.gramophone.R.id.search_button))
            .perform(click());
        Espresso.pressBack();
    }
}
/*
package com.kabouzeid.gramophone.uitests

        import androidx.test.espresso.Espresso
        import androidx.test.rule.ActivityTestRule
        import androidx.test.runner.AndroidJUnit4
        import com.kabouzeid.gramophone.ui.activities.MainActivity
        import org.junit.Rule
        import org.junit.Test
        import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @Rule
    var activityActivityTestRule = ActivityTestRule(
            MainActivity::class.java
    )

*/
/*
    @Test
    fun useAppContext() {
        val context = InstrumentationRegistry.getTargetContext()
        assertEquals("com.kabouzeid.gramophone.ui.activities", context.packageName)
    }
*//*


    @Test
    fun clickButtonHome() {
        */
/*Espresso.onView(withId(com.kabouzeid.gramophone.R.id.search_button))
            .perform(click())*//*

        Espresso.pressBack()
    }
}*/

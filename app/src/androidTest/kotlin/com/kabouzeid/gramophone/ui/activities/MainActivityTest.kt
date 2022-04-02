package com.kabouzeid.gramophone.ui.activities


import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.core.content.ContextCompat.getSystemService
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.kabouzeid.gramophone.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


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

    @Test
    fun useAppContext() {
        /*val scenario: ActivityScenario<*> = activityActivityTestRule.scenario
        scenario.moveToState(Lifecycle.State.RESUMED);    // Moves the activity state to State.RESUMED.
        scenario.moveToState(Lifecycle.State.STARTED);    // Moves the activity state to State.STARTED.
        scenario.moveToState(Lifecycle.State.CREATED);    // Moves the activity state to State.CREATED.
        scenario.moveToState(Lifecycle.State.DESTROYED);*/
        //Espresso.onView(withText("Hello world!")).check(matches(isDisplayed()))
        /*val context = InstrumentationRegistry.getInstrumentation().context
        assertEquals("com.kabouzeid.gramophone.debug.test", context.packageName)*/
    }

    @Test
    fun clickButtonHome() {
        NavigationViewActions.navigateTo(R.id.nav_folders)
        val manager =
            activityActivityTestRule.activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri =
            Uri.parse("https://cdn.pixabay.com/download/audio/2022/03/23/audio_07b2a04be3.mp3?filename=order-99518.mp3")
        val request =
            DownloadManager.Request(uri).setTitle("music.mp3").setDescription("Downloading...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI xor DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MUSIC,
                "downloadfileName"
            ).setMimeType("*/*")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
        val file = File("/root/storage/emulated/0/music")
        manager.enqueue(request)
        /*Espresso.onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        Espresso.onView(withId(R.id.drawer_layout)).check(matches(isOpen()))
//        Espresso.onView(withText("Phonograph")).check(matches(isDisplayed()))
        Espresso.onView(withId(R.id.navigation_view)).perform(NavigationViewActions.navigateTo(R.id.nav_library))
        Espresso.onView(withText("ALBUMS")).check(matches(isDisplayed()))*/

        Espresso.onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        Espresso.onView(withId(R.id.drawer_layout)).check(matches(isOpen()))
        Espresso.onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_folders))
        Espresso.onView(withText("MUSIC")).check(matches(isDisplayed()))
        /*Espresso.onView(withText("Folders")).check(matches(isDisplayed()))
        Espresso.onView(withText("ROOT")).check(matches(isDisplayed()))*/

    }
}
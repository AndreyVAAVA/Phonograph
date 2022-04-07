package com.kabouzeid.gramophone.ui.activities


import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.kabouzeid.gramophone.R
import com.kabouzeid.gramophone.adapter.PlaylistAdapter
import com.kabouzeid.gramophone.adapter.SongFileAdapter
import com.kabouzeid.gramophone.ui.activities.EspressoTestsMatchers.withDrawable
import com.kabouzeid.gramophone.ui.fragments.mainactivity.folders.FoldersFragment
import com.kabouzeid.gramophone.ui.fragments.mainactivity.library.LibraryFragment
import com.kabouzeid.gramophone.ui.fragments.mainactivity.library.pager.PlaylistsFragment
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf
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

    /**
     * Preload songs and if everything was loaded, it returns true boolean, else it returns false boolean
     */
    private fun preloadSongs(): Boolean {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .toString() + "/" + "song1.mp3"
        )
        var isDownloadedMP3File = false
        if (!file.exists()) {
            val manager =
                activityActivityTestRule.activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val uri: Uri =
                Uri.parse("https://cdn.pixabay.com/download/audio/2022/03/23/audio_07b2a04be3.mp3?filename=order-99518.mp3")
            val request =
                DownloadManager.Request(uri).setTitle("song1.mp3").setDescription("Downloading...")
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI xor DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "song1.mp3")
                    .setMimeType("audio/MP3")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
            manager.enqueue(request)

            while (!isDownloadedMP3File) {
                isDownloadedMP3File = MusicDownloadFileBroadcastChecker.isDownloadComplete
            }
        } else isDownloadedMP3File = true
        return isDownloadedMP3File
    }

    /**
     * It selects first downloaded song, named "song1.mp3", that can be used in different tests
     */
    private fun selectFirstDownloadedSong() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()))
        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(R.id.nav_folders))
        Wait(object : Wait.Condition {
            override fun check(): Boolean {
                return activityActivityTestRule.activity.currentFragment is FoldersFragment
            }
        }).waitForIt()
        onView(withText("MUSIC")).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItem<SongFileAdapter.ViewHolder>(
                hasDescendant(withText("song1.mp3")), click()
            )
        )
    }

    @Test
    fun clickButtonHome() {
        if (preloadSongs()) {
            selectFirstDownloadedSong()
            onView(withId(R.id.mini_player_image)).perform(click())
            onView(withId(R.id.player_play_pause_fab)).perform(click())
            onView(withId(R.id.action_toggle_favorite)).perform(click())
            Espresso.pressBack()
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()))
            onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_library))
            Wait(object : Wait.Condition {
                override fun check(): Boolean {
                    return activityActivityTestRule.activity.currentFragment is LibraryFragment
                }
            }).waitForIt()
            while (true) {
                if ((activityActivityTestRule.activity.currentFragment as LibraryFragment).currentFragment is PlaylistsFragment) {
                    break
                } else {
                    onView(withId(R.id.pager)).perform(ViewPagerActions.scrollRight())
                }
            }


        }
    }


    /**
     * This methods checks workability of favorite button in player with Favorite playlist
     */
    @Test
    fun checkFavouriteWorkability() {
        if (preloadSongs()) {
            selectFirstDownloadedSong()
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
            onView(withId(R.id.drawer_layout)).check(matches(isOpen()))
            onView(withId(R.id.navigation_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_library))
            Wait(object : Wait.Condition {
                override fun check(): Boolean {
                    return activityActivityTestRule.activity.currentFragment is LibraryFragment
                }
            }).waitForIt()
            onView(AllOf.allOf(isDisplayed(), withId(R.id.recycler_view)))
                .perform(
                    RecyclerViewActions.actionOnItem<PlaylistAdapter.ViewHolder>(
                        hasDescendant(
                            withText(
                                activityActivityTestRule.activity.applicationContext.getString(
                                    R.string.favorites
                                )
                            )
                        ), click()
                    )
                )
            try {
                onView(withText(activityActivityTestRule.activity.applicationContext.getString(R.string.playlist_empty_text))).check(
                    matches(isDisplayed())
                )
                onView(withId(R.id.mini_player_image)).perform(click())
                onView(withId(R.id.player_play_pause_fab)).perform(click())
                onView(withId(R.id.action_toggle_favorite)).perform(click())
                onView(withText("song1.mp3")).check(matches(isDisplayed()))
            } catch (ex: Throwable) {
                onView(withId(R.id.mini_player_image)).perform(click())
                onView(withId(R.id.player_play_pause_fab)).perform(click())
                onView(withId(R.id.action_toggle_favorite)).perform(click())
                Espresso.pressBack()
                onView(withText(activityActivityTestRule.activity.getString(R.string.playlist_empty_text))).check(
                    matches(isDisplayed())
                )
            }
        }
    }
}

/*fun swipeDown(): ViewAction? {
    return GeneralSwipeAction(
        Swipe.FAST, GeneralLocation.CENTER,
        GeneralLocation.BOTTOM_CENTER, Press.FINGER
    )
}*/

class Wait(private val mCondition: Condition) {
    interface Condition {
        fun check(): Boolean
    }

    fun waitForIt() {
        var state = mCondition.check()
        val startTime = System.currentTimeMillis()
        while (!state) {
            try {
                Thread.sleep(CHECK_INTERVAL.toLong())
            } catch (e: InterruptedException) {
                throw RuntimeException(e)
            }
            if (System.currentTimeMillis() - startTime > TIMEOUT) {
                throw AssertionError("Wait timeout.")
            }
            state = mCondition.check()
        }
    }

    companion object {
        private const val CHECK_INTERVAL = 100
        private const val TIMEOUT = 10000
    }
}

object EspressoTestsMatchers {
    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun noDrawable(): Matcher<View> {
        return DrawableMatcher(-1)
    }
}
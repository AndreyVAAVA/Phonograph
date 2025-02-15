package com.kabouzeid.gramophone.ui.activities


import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.kabouzeid.gramophone.R
import com.kabouzeid.gramophone.adapter.PlaylistAdapter
import com.kabouzeid.gramophone.adapter.SongFileAdapter
import com.kabouzeid.gramophone.adapter.song.PlaylistSongAdapter
import com.kabouzeid.gramophone.ui.fragments.mainactivity.folders.FoldersFragment
import com.kabouzeid.gramophone.ui.fragments.mainactivity.library.LibraryFragment
import com.kabouzeid.gramophone.ui.fragments.mainactivity.library.pager.PlaylistsFragment
import org.hamcrest.Matcher
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File


// I know how to realize next and prev button test, but because lack of time(I needed more about 30 mins), I don't have enough time to do this.
// If you want to see code, write me to email: ballgameandrew@yandex.ru (I may clone this repo and push it by different name into my github)
// And you may ask, why I stopped using download method, I can say, because it is somehow was crushing my emulator.
// Please look previous commits
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

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    private val songName1 = "song1.mp3"
    private val songLink1 =
        "https://cdn.pixabay.com/download/audio/2022/03/23/audio_07b2a04be3.mp3?filename=order-99518.mp3"
    private val songName2 = "song2.mp3"
    private val songLink2 =
        "https://cdn.pixabay.com/download/audio/2022/03/21/audio_50da5d4db6.mp3?filename=showreel-99195.mp3"
    private val playlistName = "Test Playlist"
    private val stringRenamed = "Renamed"
    private val renamedPlaylistName = playlistName + stringRenamed

    private fun pressFavouriteButtonAndReturnBack() {
        onView(withId(R.id.mini_player_image)).perform(click())
        onView(withId(R.id.player_play_pause_fab)).perform(click())
        onView(withId(R.id.action_toggle_favorite)).perform(click())
        Espresso.pressBack()
    }

    /**
     * It selects first downloaded song, named "song1.mp3", that can be used in different tests
     */
    private fun selectFirstDownloadedSong() {
        goToDrawerActivity(R.id.nav_folders)
        while (activityActivityTestRule.activity == null)
            onView(isRoot()).perform(waitFor(1000))
        while (activityActivityTestRule.activity.currentFragment !is FoldersFragment)
            onView(isRoot()).perform(waitFor(1000))
        onView(withText("MUSIC")).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItem<SongFileAdapter.ViewHolder>(
                hasDescendant(withText("song1.mp3")), click()
            )
        )
    }

    private fun goToDrawerActivity(id: Int) {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()))
        onView(withId(R.id.navigation_view))
            .perform(NavigationViewActions.navigateTo(id))
    }

    @Test
    fun checkPrevNext() {
        val id = preloadSongs(songName1, songLink1)
        var status = true
        while (status)
            status = checkStatus(
                this.activityActivityTestRule.activity.applicationContext,
                getStatus(this.activityActivityTestRule.activity.applicationContext, id)
            )
        selectFirstDownloadedSong()
        onView(withId(R.id.mini_player_image)).perform(click())
        onView(withId(R.id.player_play_pause_fab)).perform(click())
        onView(withId(R.id.player_next_button)).perform(click())
        onView(withId(R.id.player_prev_button)).perform(click())

    }

    /**
     * This test checks is play/pause button fully workable. (in meaning does music start playing and stops)
     */
    @Test
    fun checkPlayPause() {
        val id = preloadSongs(songName1, songLink1)
        var status = true
        while (status)
            status = checkStatus(
                this.activityActivityTestRule.activity.applicationContext,
                getStatus(this.activityActivityTestRule.activity.applicationContext, id)
            )
        selectFirstDownloadedSong()
        onView(withId(R.id.mini_player_image)).perform(click())
        onView(withId(R.id.player_play_pause_fab)).perform(click())
        val playerSongCurrentProgress =
            activityActivityTestRule.activity.findViewById<TextView>(R.id.player_song_current_progress)
        var currentTime = playerSongCurrentProgress.text.toString().split(":")
        onView(isRoot()).perform(waitFor(10000))
        var timeAfter10Seconds = playerSongCurrentProgress.text.toString().split(":")
        if (timeAfter10Seconds != currentTime) throw PlayPauseException(
            "Time difference is not zero somehow"
        )
        onView(withId(R.id.player_play_pause_fab)).perform(click())
        currentTime = playerSongCurrentProgress.text.toString().split(":")
        onView(isRoot()).perform(waitFor(10000))
        timeAfter10Seconds = playerSongCurrentProgress.text.toString().split(":")
        // You may ask, why I take 10 second delay and then take time number from text
        // I can answer, because I decided, you can change it, but understand you risks, because song, that I use for test, is only 1 minute 42 seconds long.
        if (timeAfter10Seconds[0].toInt() - currentTime[0].toInt() < 0) throw PlayPauseException(
            "Time difference is below zero somehow"
        )
        if ((timeAfter10Seconds[1].toInt() - currentTime[1].toInt() == 0)
            && (timeAfter10Seconds[1].toInt() - currentTime[1].toInt() == 0)
        ) throw PlayPauseException(
            "Time difference is zero somehow"
        )
        if (timeAfter10Seconds[1].toInt() - currentTime[1].toInt() < 0) throw PlayPauseException(
            "Time difference is zero or below zero somehow"
        )
        onView(withId(R.id.player_play_pause_fab)).perform(click())
    }

    /**
     * IMPORTANT!!! PLAYLISTS WITH STRING VALUES FROM VARIABLES playlistName AND renamedPlaylistName MUST NOT EXISTS
     * In short: It checks ability of: creating/deleting playlist, adding/deleting songs to/from playlist and renaming of playlist
     * This test does search, then add song that was found to playlist (by creating it), then it goes back and open this playlist
     * After playlist was opened, it deletes previously added song,
     * then renames playlist(and of course checks if playlist was renamed) and then, deletes playlist.
     */
    @Test
    fun checkPlaylistCreatingSongAddingAndBothDeletingAndWithPlaylistRenaming() {
        // yes I know about Single Responsibility Principe, but that is the most safest solution.(In compare to other solutions that I see)
        val id = preloadSongs(songName1, songLink1)
        var status = true
        while (status)
            status = checkStatus(
                this.activityActivityTestRule.activity.applicationContext,
                getStatus(this.activityActivityTestRule.activity.applicationContext, id)
            )
        onView(withId(R.id.action_search))
            .perform(click())
        onView(withId(R.id.search_src_text)).perform(typeText("song1"))
        onView(withId(R.id.menu)).perform(click())
        onView(withText(R.string.action_add_to_playlist)).perform(click())
        onView(withText(R.string.action_new_playlist)).perform(click())
        onView(withHint(R.string.playlist_name_empty)).perform(
            ViewActions.typeTextIntoFocusedView(
                playlistName
            )
        )
        onView(withText(R.string.create_action)).perform(click())
        Espresso.closeSoftKeyboard()
        Espresso.pressBack()
        Espresso.pressBack()
        while (true) {
            if ((activityActivityTestRule.activity.currentFragment as LibraryFragment).currentFragment is PlaylistsFragment) {
                break
            } else {
                onView(withId(R.id.pager)).perform(ViewPagerActions.scrollRight())
            }
        }
        onView(AllOf.allOf(isDisplayed(), withId(R.id.recycler_view)))
            .perform(
                RecyclerViewActions.actionOnItem<PlaylistAdapter.ViewHolder>(
                    hasDescendant(
                        withText(
                            playlistName
                        )
                    ), click()
                )
            )
        onView(withId(R.id.recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<PlaylistSongAdapter.ViewHolder>(
                1,
                MyViewAction.clickChildViewWithId(R.id.menu)
            )
        )
        onView(withText(R.string.action_remove_from_playlist)).perform(click())
        onView(withText(R.string.remove_action)).perform(click())
        onView(withText("Test Playlist")).check(matches(isDisplayed()))
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.action_rename)).perform(click())
        onView(withHint(R.string.playlist_name_empty)).perform(
            ViewActions.typeTextIntoFocusedView(
                stringRenamed
            )
        )
        onView(withText(renamedPlaylistName)).check(
            matches(isDisplayed())
        )
        onView(withText(R.string.rename_action)).perform(click())
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.action_delete)).perform(click())
        onView(withText(R.string.delete_action)).perform(click())
    }

    /**
     * IMPORTANT!!! FAVORITE MUST NOT EXISTS OR BE EMPTY
     * This methods checks workability of favorite button in player with Favorite playlist.
     */
    @Test
    fun checkFavoriteWorkability() {
        deleteAllDownloadedSongFromMusicDir()
        val id = preloadSongs(songName1, songLink1)
        var status = true
        while (status)
            status = checkStatus(
                this.activityActivityTestRule.activity.applicationContext,
                getStatus(this.activityActivityTestRule.activity.applicationContext, id)
            )
        selectFirstDownloadedSong()
        goToDrawerActivity(R.id.nav_library)
        while (activityActivityTestRule.activity == null)
            onView(isRoot()).perform(waitFor(1000))
        while (activityActivityTestRule.activity.currentFragment !is LibraryFragment)
            onView(isRoot()).perform(waitFor(1000))
        while (true) {
            if ((activityActivityTestRule.activity.currentFragment as LibraryFragment).currentFragment is PlaylistsFragment) {
                break
            } else {
                onView(withId(R.id.pager)).perform(ViewPagerActions.scrollRight())
            }
        }
        // This function being call for two times, because of possibility not having Favorites folder.
        pressFavouriteButtonAndReturnBack()
        pressFavouriteButtonAndReturnBack()
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
        val textToCompare =
            activityActivityTestRule.activity.getString(R.string.playlist_empty_text)
        onView(withText(textToCompare)).check(
            matches(isDisplayed())
        )
        pressFavouriteButtonAndReturnBack()
        try {
            onView(withText(textToCompare)).check(matches(isDisplayed()))
        } catch (ex: Throwable) {
            onView(withId(R.id.mini_player_image)).perform(click())
            onView(withId(R.id.action_toggle_favorite)).perform(click())
            Espresso.pressBack()
        } finally {
            onView(withText(textToCompare)).check(matches(isDisplayed()))
        }
    }

    private fun getStatus(context: Context, downloadId: Long): Int {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId) // filter your download bu download Id
        val c = downloadManager.query(query)
        if (c.moveToFirst()) {
            val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            c.close()
            Log.i("DOWNLOAD_STATUS", status.toString())
            return status
        }
        Log.i("AUTOMATION_DOWNLOAD", "DEFAULT")
        return -1
    }

    private fun checkStatus(context: Context, status: Int): Boolean {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterByStatus(status)
        val c = downloadManager.query(query)
        if (c.moveToFirst()) {
            c.close()
            Log.i("DOWNLOAD_STATUS", status.toString())
            return true
        }
        Log.i("AUTOMATION_DOWNLOAD", "DEFAULT")
        return false
    }

    /**
     * Preload songs and if everything was loaded, it returns true boolean, else it returns false boolean
     */
    private fun preloadSongs(songName: String, songLink: String): Long {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .toString() + "/" + songName
        )
        var isDownloadedMP3File = false
        val manager =
            activityActivityTestRule.activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri =
            Uri.parse(songLink)
        DownloadManager.Query()
        val id = activityActivityTestRule.activity.intent.getLongExtra(
            DownloadManager.EXTRA_DOWNLOAD_ID,
            -1
        )
        val request =
            DownloadManager.Request(uri).setTitle(songName).setDescription("Downloading...")
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI xor DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, songName)
                .setMimeType("audio/MP3")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
        return manager.enqueue(request)
    }


    private fun deleteAllDownloadedSongFromMusicDir() {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                .toString()
        )
        val files: Array<File>? = dir.listFiles()
        if (files != null) {
            for (file in files) {
                file.delete()
            }
        }
    }

    private fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}

object MyViewAction {
    fun clickChildViewWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController?, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
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

/*class Wait(private val mCondition: Condition) {
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
}*/

/*
object EspressoTestsMatchers {
    fun withDrawable(resourceId: Int): Matcher<View> {
        return DrawableMatcher(resourceId)
    }

    fun noDrawable(): Matcher<View> {
        return DrawableMatcher(-1)
    }
}*/

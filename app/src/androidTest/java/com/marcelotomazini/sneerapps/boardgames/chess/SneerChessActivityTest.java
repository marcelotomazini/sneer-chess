package com.marcelotomazini.sneerapps.boardgames.chess;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SneerChessActivityTest {

    @Rule
    public ActivityTestRule<SneerChessActivity> mActivityRule = new ActivityTestRule<>(SneerChessActivity.class);

    @org.junit.Test
    public void testOnCreate() throws Exception {
//        Espresso.onView(ViewMatchers.withId(R.layout.activity_main)).perform(ViewActions.click());
    }

    @org.junit.Test
    public void testOnDestroy() throws Exception {

    }

    @org.junit.Test
    public void testOnMove() throws Exception {

    }

    @org.junit.Test
    public void testOnSelect() throws Exception {

    }

    @org.junit.Test
    public void testOnDeselect() throws Exception {

    }

    @org.junit.Test
    public void testMove() throws Exception {

    }
}
package com.example.v7;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest2 {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest2() {
        ViewInteraction materialCheckBox = onView(
                allOf(withId(R.id.cB),
                        childAtPosition(
                                allOf(withId(R.id.linear_layout),
                                        childAtPosition(
                                                withId(R.id.v7_RecyclerAdapter),
                                                2)),
                                1),
                        isDisplayed()));
        materialCheckBox.perform(click());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.cast_vote_btn), withText("Cast Your Vote"),
                        childAtPosition(
                                allOf(withId(R.id.ConstraintLayout),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                6),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.cast_vote2), withText("Will cast this as your Vote"),
                        childAtPosition(
                                allOf(withId(R.id.ConstraintLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.cast_vote2), withText("Will cast this as your Vote"),
                        childAtPosition(
                                allOf(withId(R.id.ConstraintLayout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialButton3.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}

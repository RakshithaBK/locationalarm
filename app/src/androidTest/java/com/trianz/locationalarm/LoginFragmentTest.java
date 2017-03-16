package com.trianz.locationalarm;

import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.trianz.locationalarm.Authentication.AuthenticationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Rakshitha.Krishnayya on 21-02-2017.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginFragmentTest {

    @Rule
    public ActivityTestRule<AuthenticationActivity> mActivityRule =
            new ActivityTestRule<>(AuthenticationActivity.class);

    @Test
    public void ensureTextChangesWork() throws InterruptedException {
        // Type text and then press the button.
        Intents.init();
        onView(withId(R.id.loginMobileTxt))
                .perform(typeText("8722720060"), closeSoftKeyboard());
        onView(withId(R.id.loginPasswordTxt))
                .perform(typeText("Test@123"), closeSoftKeyboard());
        onView(withId(R.id.loginCheckBox)).perform(click());

        onView(withId(R.id.signInBtn)).perform(click());
        intended(hasComponent(HomeActivity.class.getName()));
        Intents.release();
    }

}

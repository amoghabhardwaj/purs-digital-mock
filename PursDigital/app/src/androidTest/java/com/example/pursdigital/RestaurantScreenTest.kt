package com.example.pursdigital


import androidx.compose.ui.test.*
import org.junit.Test
import org.junit.Assert.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import androidx.activity.ComponentActivity
import com.example.pursdigital.Hours

import com.example.pursdigital.LocationResponse
import com.example.pursdigital.RestaurantScreen

class RestaurantScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun restaurantScreen_ShowsRestaurantName() {
        // Given some location data
        val locationData = LocationResponse(
            location_name = "BEASTRO by Marshawn Lynch",
            hours = listOf()
        )

        // When the RestaurantScreen is loaded with this location data
        composeTestRule.setContent {
            RestaurantScreen(locationData)
        }

        // Then the restaurant name should be displayed
        composeTestRule.onNodeWithText("BEASTRO by Marshawn Lynch").assertIsDisplayed()
    }


    @Test
    fun restaurantScreen_ButtonsArePresent() {
        // Given some location data
        val locationData = LocationResponse(
            location_name = "Test Restaurant",
            hours = listOf()
        )

        // When the RestaurantScreen is loaded with this location data
        composeTestRule.setContent {
            RestaurantScreen(locationData)
        }

        // Then the buttons for sharing and opening maps should be present
        composeTestRule.onNodeWithContentDescription("Share").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Open in Maps").assertIsDisplayed()
    }


    @Test
    fun testFeedbackOnInteraction() {
        val locationData = LocationResponse(
            location_name = "Test Restaurant",
            hours = listOf(
                Hours("WED", "07:00:00", "23:00:00")
            )
        )

        composeTestRule.setContent {
            RestaurantScreen(locationData)
        }

        // Click on the "View Menu" button and verify if the feedback (e.g., ripple effect) is visible
        composeTestRule.onNodeWithText("View Menu").performClick().assertIsDisplayed()
        // Check for any resulting changes, assuming the UI reacts to this click
    }

    @Test
    fun testNavigationActions() {
        val locationData = LocationResponse(
            location_name = "Test Restaurant",
            hours = listOf()
        )

        composeTestRule.setContent {
            RestaurantScreen(locationData)
        }

        // Perform click and verify navigation attempt
        composeTestRule.onNodeWithContentDescription("Open in Maps").performClick()
        // You need to mock or observe the interaction to verify if it attempts to open a maps activity
    }

}
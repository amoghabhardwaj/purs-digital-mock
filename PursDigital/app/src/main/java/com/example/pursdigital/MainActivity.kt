package com.example.pursdigital

import MainViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import com.example.pursdigital.ui.theme.YourAppTheme
import com.example.pursdigital.utils.TimeUtils

// MainActivity is the entry point of the app
class MainActivity : ComponentActivity() {
    // Lazy initialization of MainViewModel
    private val mainViewModel: MainViewModel by viewModels()

    // Called when the activity is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content of the activity to a composable function
        setContent {
            YourAppTheme {
                // Observe location data from ViewModel
                val locationData by mainViewModel.locationData
                // Display the RestaurantScreen if data is available, otherwise show loading text
                if (locationData != null) {
                    RestaurantScreen(locationData!!)
                } else {
                    // Show a loading or error state
                    Text(text = "Loading...")
                }
            }
        }
    }
}

// Function to open Google Maps with a specified location
fun openMaps(context: Context, locationName: String) {
    // Create a Uri for the location query
    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(locationName)}")
    // Create an Intent to open the location in Google Maps
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }
    // Check if an activity can handle the Intent and start it
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    }
}

// Function to share restaurant details via WhatsApp
fun shareOnWhatsApp(context: Context, locationData: LocationResponse) {
    // Create a message with the restaurant's name and open hours
    val hours = locationData.hours.joinToString("\n") { "${it.day_of_week}: ${TimeUtils.convertTo12HourFormat(it.start_local_time)} - ${TimeUtils.convertTo12HourFormat(it.end_local_time)}" }
    val message = "Check out this restaurant:\n\n${locationData.location_name}\n\nOpen Hours:\n$hours"

    // Create an Intent to share the message via WhatsApp
    val packageManager = context.packageManager
    try {
        val i = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, message)
        }
        // Check if WhatsApp is installed and start the activity
        if (i.resolveActivity(packageManager) != null) {
            context.startActivity(i)
        } else {
            // Show a toast if WhatsApp is not installed
            Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        // Handle any exceptions and show a toast message
        e.printStackTrace()
        Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
    }
}

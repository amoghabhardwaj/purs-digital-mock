package com.example.pursdigital

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.pursdigital.utils.TimeUtils
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import com.example.pursdigital.ui.theme.YourAppTheme
import coil.transform.BlurTransformation
import java.util.*

@Composable
fun BlurredImage(imageRes: Int) {
    val context = LocalContext.current
    Image(
        painter = rememberImagePainter(
            data = imageRes,
            builder = {
                transformations(BlurTransformation(context, 5f, 4f))
            }
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun RestaurantScreen(locationData: LocationResponse) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.restaurant_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp) // Adjust the height for a subtle effect
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent),
                        startY = 0f,
                        endY = 50f
                    )
                )
        )

        // Bottom Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        startY = 0f,
                        endY = 200f
                    )
                )
        )

        // Main content of the screen
        Column(
            modifier = Modifier
                .padding(2.dp)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Restaurant name text
            Text(
                text = "BEASTRO by Marshawn Lynch",
                color = Color.White,
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            // Expandable card showing restaurant hours
            ExpandableSurface(locationData)

            Spacer(modifier = Modifier.weight(1f))

            // Share via WhatsApp button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        shareOnWhatsApp(context, locationData)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.whatsapp_logo), // Make sure this drawable exists
                        contentDescription = "Share",
                        tint = Color.Unspecified
                    )
                }
            }

            // Open in Maps button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        openMaps(context, locationData.location_name)
                    },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_add_location_24), // Make sure this drawable exists
                        contentDescription = "Open in Maps",
                        tint = Color.Unspecified
                    )
                }
            }

            // Arrow icons to indicate expandable section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                   ,
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_up), // Make sure this drawable exists
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_up_bold), // Make sure this drawable exists
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // View Menu button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { /* Handle menu click */ },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .padding(horizontal = 5.dp)
                ) {
                    Text(
                        text = "View Menu",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

@Composable
fun ExpandableSurface(locationData: LocationResponse) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(if (expanded) 180f else 0f)
    val scrollState = rememberScrollState()

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.9f),
        modifier = Modifier
            .padding(vertical = 50.dp)
            .clickable { expanded = !expanded }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            val (currentDay, currentTime) = TimeUtils.getCurrentDayAndTime()
            val openingStatus = TimeUtils.determineOpeningStatus(currentDay, currentTime, locationData.hours)
            val status = TimeUtils.getStatus(currentDay, currentTime, locationData.hours)

            // Display opening status and see full hours text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.widthIn(max = 200.dp)) {
                    Text(
                        text = openingStatus,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "SEE FULL HOURS",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.weight(0.2f))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            when (status) {
                                "open" -> Color.Green
                                "closing_soon" -> Color.Yellow
                                "closed" -> Color.Red
                                else -> Color.Gray
                            },
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = null,
                    modifier = Modifier
                        .rotate(rotationAngle)
                        .size(32.dp)
                        .padding(start = 8.dp)
                )
            }

            // Expandable section showing detailed hours
            AnimatedVisibility(visible = expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Set the fixed height
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 36.dp)
                            .verticalScroll(scrollState) // Add vertical scroll
                    ) {

                        val combinedHours = TimeUtils.combineOpeningHours(locationData.hours)  // Assume this returns a Map<String, String>
                        combinedHours.forEach { (day, hoursString) ->
                            val listOfHours = TimeUtils.splitHours(hoursString)  // Split the hours string into a list
                            val isToday = day.substring(0, 3).uppercase(Locale.ROOT) == currentDay
                            if (listOfHours.isNotEmpty()) {
                                // Use a Row with a Spacer to push the hours to the right
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = day,
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Spacer(Modifier.weight(1f))  // This Spacer will push the hours to the right
                                    Text(
                                        text = listOfHours.first(),
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                // Align additional hours to the right
                                listOfHours.drop(1).forEach { hours ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Text(
                                            text = hours,
                                            color = Color.Black,
                                            fontSize = 16.sp,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}

// Preview function to display the RestaurantScreen composable in the Android Studio preview
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YourAppTheme {
        val sampleLocation = LocationResponse(
            location_name = "BEASTRO by Marshawn Lynch",
            hours = listOf(
                Hours("WED", "07:00:00", "13:00:00"),
                Hours("WED", "00:00:00", "02:00:00"),
                Hours("WED", "15:00:00", "22:00:00"),
                Hours("TUE", "07:00:00", "13:00:00"),
                Hours("TUE", "15:00:00", "24:00:00"),
                Hours("THU", "00:00:00", "24:00:00")
            )
        )
        RestaurantScreen(sampleLocation)
    }
}

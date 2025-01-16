package hr.ferit.ivankolobara.calorietracker.ui

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hr.ferit.ivankolobara.calorietracker.R
import hr.ferit.ivankolobara.calorietracker.Routes


@Composable
fun DashboardScreen(navigation: NavHostController) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        TopNavMenu(navigation)
        CircularCalorieGraph(300, 2130, Color.Blue, Color.DarkGray)
        ClickableListWithPopup()
        IconButton(R.drawable.ic_plus, "Add meal", navigation)
    }
}


@Composable
fun CircularCalorieGraph(
    consumedCalories: Int,
    totalCalories: Int,
    consumedColor: Color = Color.Green,
    remainingColor: Color = Color.Red
) {
    val consumedPercentage = consumedCalories.toFloat() / totalCalories

    Canvas(modifier = Modifier.size(150.dp)) {
        drawArc(
            color = remainingColor,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 20f)
        )

        drawArc(
            color = consumedColor,
            startAngle = -90f,
            sweepAngle = 360f * consumedPercentage,
            useCenter = false,
            style = Stroke(width = 20f)
        )
    }
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = consumedColor)){
                append("$consumedCalories")
            }
            withStyle(style = SpanStyle(color = Color.Black)){
                append("/")
            }
            withStyle(style = SpanStyle(color = remainingColor)){
                append("$totalCalories")
            }
        },
        style = TextStyle(
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun ClickableListWithPopup() {
    val items = listOf("Breakfast", "Lunch", "Dinner", "Snacks")
    val meals = mutableStateMapOf(
        "Breakfast" to mutableListOf("Pancakes", "Eggs", "Coffee"),
        "Lunch" to mutableListOf("Salad", "Soup", "Bread"),
        "Dinner" to mutableListOf("Steak", "Potatoes", "Wine"),
        "Snacks" to mutableListOf("Chips", "Fruit", "Yogurt")
    )


    var showPopup by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .padding(top = 18.dp, bottom = 0.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .clickable {
                        selectedItem = item
                        showPopup = true
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = item,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = {
                Text(text = "$selectedItem")
            },
            text = {
                val mealList = meals[selectedItem] ?: emptyList()
                Column {
                    mealList.forEachIndexed { index, meal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = meal,
                                fontSize = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Row {
                                TextButton(onClick = {
                                    meals[selectedItem]?.set(index, "Edited Meal")
                                }) {
                                    Text("Edit")
                                }
                                TextButton(onClick = {
                                    meals[selectedItem]?.removeAt(index)
                                }) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showPopup = false }) {
                    Text(text = "Close")
                }
            }
        )
    }
}

@Composable
fun IconButton(
    @DrawableRes iconResource: Int,
    text: String,
    navigation: NavHostController
) {
    Button(
        onClick = { navigation.navigate(Routes.AddMeal) },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
        modifier = Modifier.padding(top = 18.dp)
    ) {
        Row {
            Icon(
                painter = painterResource(id = iconResource),
                contentDescription = text
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavMenu(
    navigation: NavHostController
) {
    Column(modifier = Modifier.padding(bottom = 56.dp),
    horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            title = {
                Text(text = "Calorie Tracker",
                    modifier = Modifier.fillMaxWidth()
                        .padding(end = 40.dp),
                    textAlign = TextAlign.Center
                    )
            },
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable {
                            navigation.navigate(Routes.Profile)
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        )
    }
}



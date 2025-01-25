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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
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
import hr.ferit.ivankolobara.calorietracker.ui.data.UserViewModel
import java.time.LocalDate

@Composable
fun DashboardScreen(navigation: NavHostController, userViewModel: UserViewModel) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        TopNavMenu(navigation)
        CircularCalorieGraph(300, 2130, Color.Blue, Color.DarkGray)
        ExpandableListWithCalories(navigation = navigation, userViewModel = userViewModel)
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
    val remainingCalories = totalCalories - consumedCalories

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
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

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$remainingCalories",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = remainingColor
                )
            )
            Text(
                text = "Remaining",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            )
        }
    }

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = consumedColor)) {
                append("$consumedCalories")
            }
            withStyle(style = SpanStyle(color = Color.Black)) {
                append("/")
            }
            withStyle(style = SpanStyle(color = remainingColor)) {
                append("$totalCalories")
            }
        },
        style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(top = 16.dp)
    )
}


@SuppressLint("UnrememberedMutableState", "RememberReturnType", "NewApi")
@Composable
fun ExpandableListWithCalories(userViewModel: UserViewModel, navigation: NavHostController) {
    val items = listOf("Breakfast", "Lunch", "Dinner", "Snacks")
    val user = userViewModel.userData.value

// Get today's date components
    val today = LocalDate.now()
    val todayDay = today.dayOfMonth
    val todayMonth = today.monthValue
    val todayYear = today.year

    val meals = remember { mutableStateMapOf<String, MutableList<Pair<String, Int>>>() } // Meal name and calories
    val expandedItems = remember { mutableStateMapOf<String, Boolean>().apply { items.forEach { this[it] = false } } }

// Populate meals dynamically based on user data
    LaunchedEffect(user) {
        meals.clear()
        user?.meals?.forEach { (mealType, mealsList) ->
            // Filter meals based on today's date
            mealsList.filter { meal ->
                val mealDateString = meal.date // Assuming the date is stored as a string "dd.MM.yyyy"
                val (mealDay, mealMonth, mealYear) = mealDateString.split(".").map { it.toIntOrNull() ?: 0 }

                // Compare the stored meal date with today's date
                mealDay == todayDay && mealMonth == todayMonth && mealYear == todayYear
            }.forEach { meal ->
                // Add meal data to the corresponding meal type
                meals.computeIfAbsent(mealType) { mutableListOf() }
                    .add(Pair(meal.mealId.id ?: "Unknown Meal", meal.servingSize))
            }
        }
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 64.dp, top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { category ->
                val isExpanded = expandedItems[category] ?: false
                val totalCalories = meals[category]?.sumOf { it.second } ?: 0

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable {
                                expandedItems[category] = !isExpanded
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "$category - $totalCalories kcal",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null
                            )
                        }
                    }
                }

                if (isExpanded) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            meals[category]?.forEach { (mealName, calories) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$mealName - $calories kcal",
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        CustomIconButton(R.drawable.ic_plus, "Add Food", navigation)
    }
}




@Composable
fun CustomIconButton(
    @DrawableRes iconResource: Int,
    text: String,
    navigation: NavHostController
) {
    Button(
        onClick = { navigation.navigate(Routes.AddMeal) },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
        modifier = Modifier.padding(top = 0.dp)
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



package hr.ferit.ivankolobara.calorietracker.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.compose.currentBackStackEntryAsState
import hr.ferit.ivankolobara.calorietracker.R
import hr.ferit.ivankolobara.calorietracker.Routes
import hr.ferit.ivankolobara.calorietracker.ui.data.MealDetails
import hr.ferit.ivankolobara.calorietracker.ui.data.UserMeals
import hr.ferit.ivankolobara.calorietracker.ui.data.UserMealsViewModel
import hr.ferit.ivankolobara.calorietracker.ui.data.UserViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(navigation: NavHostController, userMealsViewModel: UserMealsViewModel, userViewModel: UserViewModel) {
    val totalCalories = userViewModel.userData.value?.dailyCalorieGoal ?: 0

    val userMeals by userMealsViewModel.getMealsForDate(LocalDate.now()).collectAsState(emptyList())
    val mealDetailsMap by userMealsViewModel.mealDetailsMap.collectAsState()

    Scaffold(
        topBar = { TopNavMenu(navigation) },
        bottomBar = { BottomNavigationBar(navigation) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xff140330)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(54.dp))
            CircularCalorieGraph(getConsumedCalories(userMeals, mealDetailsMap),
                totalCalories,
                Color(0xff520655),
                Color(0xffFBF4FB))
            if (mealDetailsMap.isEmpty()) {
                CircularProgressIndicator()
            } else {
                ExpandableListWithCalories(
                    navigation = navigation,
                    userMealsViewModel = userMealsViewModel,
                    userMeals = userMeals,
                    mealDetailsMap = mealDetailsMap
                )
            }
        }
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
        modifier = Modifier.size(150.dp),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
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
                    color = remainingColor
                )
            )
        }
    }

    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = consumedColor)) {
                append("$consumedCalories")
            }
            withStyle(style = SpanStyle(color = remainingColor)) {
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


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState", "RememberReturnType")
@Composable
fun ExpandableListWithCalories(userMealsViewModel: UserMealsViewModel, navigation: NavHostController,
                               userMeals: List<UserMeals>, mealDetailsMap: Map<String, MealDetails>) {
    val items = listOf("Breakfast", "Lunch", "Dinner", "Snacks")

    val meals = remember { mutableStateMapOf<String, MutableList<Triple<String, Int, String>>>() }
    val expandedItems = remember { mutableStateMapOf<String, Boolean>().apply { items.forEach { this[it] = false } } }

    LaunchedEffect(userMeals) {
        meals.clear()

        userMeals.forEach { meal ->
            val mealName = mealDetailsMap[meal.mealId?.id]?.name ?: "Unknown Meal"
            val mealDetails = mealDetailsMap[meal.mealId?.id]
            val totalCalories = if (mealDetails != null && mealDetails.grams > 0) {
                ((meal.servingSize / mealDetails.grams.toFloat()) * mealDetails.calories).toInt()
            } else {
                0
            }

            meals.computeIfAbsent(meal.mealType) { mutableListOf() }
                .add(Triple(mealName, totalCalories, meal.id))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = 16.dp, top = 42.dp),
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
                            .background(Color(0xff520655), RoundedCornerShape(8.dp))
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
                                .background(Color(0xff520655), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            meals[category]?.forEach { (mealName, calories, id) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 0.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$mealName - $calories kcal",
                                        fontSize = 14.sp,
                                        modifier = Modifier.weight(1f)
                                    )

                                    IconButton(
                                        onClick = {
                                            userMealsViewModel.deleteMealFromDatabase(id)
                                        },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete Meal",
                                            modifier = Modifier.size(16.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavMenu(
    navigation: NavHostController
) {
    Column(
    horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar( colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color(0xff520655),
        ),
            title = {
                Text(text = "Calorie Tracker",
                    modifier = Modifier.fillMaxWidth()
                        .padding(end = 40.dp),
                    color = Color(0xffFBF4FB),
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


@RequiresApi(Build.VERSION_CODES.O)
fun getConsumedCalories(userMeals: List<UserMeals>, mealDetailsMap: Map<String, MealDetails>): Int {
    return userMeals.sumOf { meal ->
        val mealDetails = mealDetailsMap[meal.mealId?.id]
        if (mealDetails != null && mealDetails.grams > 0) {
            ((meal.servingSize / mealDetails.grams.toFloat()) * mealDetails.calories).toInt()
        } else {
            0
        }
    }
}

@Composable
fun BottomNavigationBar(navigation: NavHostController) {
    val currentRoute = navigation.currentBackStackEntryAsState().value?.destination?.route

    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color(0xff520655)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentRoute != Routes.Dashboard){
                    navigation.navigate(Routes.Dashboard)
                } }
            ) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Dashboard", tint = Color(0xffFBF4FB))
            }
            IconButton(onClick = { navigation.navigate(Routes.AddMeal) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Meal", tint = Color(0xffFBF4FB))
            }
            IconButton(onClick = { if (currentRoute != Routes.History) { navigation.navigate(Routes.History) } } ) {
                Icon(imageVector = Icons.Filled.History, contentDescription = "History", tint = Color(0xffFBF4FB))
            }
        }
    }
}



package hr.ferit.ivankolobara.calorietracker.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.ferit.ivankolobara.calorietracker.ui.data.User
import hr.ferit.ivankolobara.calorietracker.ui.data.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navigation: NavHostController, userViewModel: UserViewModel) {
    val userState = userViewModel.userData.value

    var isEditing by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf(userState?.age?.toString() ?: "") }
    var gender by remember { mutableStateOf(userState?.gender ?: "") }
    var height by remember { mutableStateOf(userState?.height?.toString() ?: "") }
    var weight by remember { mutableStateOf(userState?.weight?.toString() ?: "") }
    var activityLevel by remember { mutableStateOf(userState?.activityLevel ?: "") }
    var goal by remember { mutableStateOf(userState?.goal ?: "") }

    val genders = listOf("Male", "Female")
    val activityLevels = listOf("Basal Metabolic Rate",
        "Little or no exercise",
        "Exercise 1-3 times/week",
        "Exercise 4-5 times/week",
        "Daily exercise or intense exercise 3-4 times/week",
        "Intense exercise 6-7 times/week",
        "Very intense exercise daily, or physical job")
    val goals = listOf("Maintain weight",
        "Mild weight loss (0.25kg/week)",
        "Weight loss (0.5kg/week)",
        "Extreme weight loss (1kg/week)",
        "Mild weight gain (0.25kg/week)",
        "Weight gain (0.5kg/week)",
        "Fast weight gain (1kg/week)")

    Scaffold(
        containerColor = Color(0xff140330),
        contentColor = Color(0xffFBF4FB),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xff520655)),
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navigation.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            isEditing = false
                            userViewModel.updateUserData(
                                User(
                                    id = userState?.id ?: "",
                                    age = age.toIntOrNull() ?: userState?.age ?: 0,
                                    gender = gender,
                                    height = height.toIntOrNull() ?: userState?.height ?: 0,
                                    weight = weight.toIntOrNull() ?: userState?.weight ?: 0,
                                    activityLevel = activityLevel,
                                    dailyCalorieGoal = calculateDailyCalorieGoal(
                                        age.toInt(),
                                        height.toInt(),
                                        weight.toInt(),
                                        activityLevel,
                                        goal,
                                        gender
                                    ),
                                    goal = goal
                                )
                            )
                            Log.d("GLEDAJ OVO MAJMUNE", "${calculateDailyCalorieGoal(age.toInt(),
                                height.toInt(),
                                weight.toInt(),
                                activityLevel,
                                goal,
                                gender)} OVOLIKO JE GOAL $goal")
                        })
                            {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(Color(0xff140330)),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileField("Age", age, isEditing, onValueChange = { age = it })
            ProfileDropdownField(
                label = "Gender",
                selectedValue = gender,
                isEditing = isEditing,
                options = genders,
                onValueChange = { gender = it }
            )
            ProfileField("Height (cm)", height, isEditing, onValueChange = { height = it })
            ProfileField("Weight (kg)", weight, isEditing, onValueChange = { weight = it })
            ProfileDropdownField(
                label = "Activity Level",
                selectedValue = activityLevel,
                isEditing = isEditing,
                options = activityLevels,
                onValueChange = { activityLevel = it }
            )
            ProfileDropdownField(
                label = "Goal",
                selectedValue = goal,
                isEditing = isEditing,
                options = goals,
                onValueChange = { goal = it }
            )
        }
    }
}


@Composable
fun ProfileDropdownField(
    label: String,
    selectedValue: String,
    isEditing: Boolean,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        if (isEditing) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp))
                    .clickable { isDropdownExpanded = true }
                    .padding(16.dp)
            ) {
                Text(text = selectedValue, style = MaterialTheme.typography.bodyLarge)
            }
            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(option)
                            isDropdownExpanded = false
                        },
                        text = { Text(text = option) }
                    )
                }
            }
        } else {
            Text(
                text = selectedValue,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditing: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        if (isEditing) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

fun calculateDailyCalorieGoal(age: Int,
                              height: Int,
                              weight: Int,
                              activityLevel: String,
                              goal: String,
                              gender: String) : Int {

    var bmr : Double = 0.0
    var activityCoeff : Double = 1.0

    when (activityLevel){
        "Basal Metabolic Rate" -> activityCoeff = 1.0
        "Little or no exercise" -> activityCoeff = 1.2
        "Exercise 1-3 times/week" -> activityCoeff = 1.375
        "Exercise 4-5 times/week" -> activityCoeff = 1.465
        "Daily exercise or intense exercise 3-4 times/week" -> activityCoeff = 1.55
        "Intense exercise 6-7 times/week" -> activityCoeff = 1.725
        "Very intense exercise daily, or physical job" -> activityCoeff = 1.9
    }

    when (gender){
        "Male" -> bmr = 10*weight + 6.25*height - 5*age + 5
        "Female" -> bmr = 10*weight + 6.25*height - 5*age - 161
    }

    when (goal){
        "Maintain weight" -> return (bmr * activityCoeff).toInt()
        "Mild weight loss (0.25kg/week)" -> return (bmr * activityCoeff - 250).toInt()
        "Weight loss (0.5kg/week)" -> return (bmr * activityCoeff - 500).toInt()
        "Extreme weight loss (1kg/week)" -> return (bmr * activityCoeff - 1000).toInt()
        "Mild weight gain (0.25kg/week)" -> return (bmr * activityCoeff + 250).toInt()
        "Weight gain (0.5kg/week)" -> return (bmr * activityCoeff + bmr * 500).toInt()
        "Fast weight gain (1kg/week)" -> return (bmr * activityCoeff + 1000).toInt()
    }
    return 0
}
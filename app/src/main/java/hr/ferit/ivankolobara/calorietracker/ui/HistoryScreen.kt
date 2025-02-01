package hr.ferit.ivankolobara.calorietracker.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import hr.ferit.ivankolobara.calorietracker.ui.data.UserMealsViewModel
import hr.ferit.ivankolobara.calorietracker.ui.data.UserViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    navigation: NavHostController,
    userMealsViewModel: UserMealsViewModel,
    userViewModel: UserViewModel
) {
    val goal = userViewModel.userData.value?.dailyCalorieGoal ?: 0

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val userMeals by userMealsViewModel.getMealsForDate(selectedDate).collectAsState(emptyList())
    val mealDetailsMap by userMealsViewModel.mealDetailsMap.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { timeInMillis ->
                timeInMillis?.let {
                    selectedDate = Instant.ofEpochMilli(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                }
            },
            onDismiss = { showDatePicker = false }
        )
    }

    Scaffold(
        containerColor = Color(0xff140330),
        contentColor = Color(0xffFBF4FB),
        topBar = { TopNavMenu(navigation) },
        bottomBar = { BottomNavigationBar(navigation) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xff140330)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(64.dp))

            Button(onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff520655),
                    contentColor = Color(0xffFBF4FB))
            ) {
                Text(text = "Select date: ${selectedDate.format(dateFormatter)}")
            }

            Spacer(modifier = Modifier.height(32.dp))

            GoalProgressContainer(
                goal, getConsumedCalories(userMeals, mealDetailsMap)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExpandableListWithCalories(
                navigation = navigation,
                userMealsViewModel = userMealsViewModel,
                userMeals = userMeals,
                mealDetailsMap = mealDetailsMap
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(Color(0xff520655)),
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun GoalProgressContainer(goal: Int, consumed: Int) {
    val remaining = goal - consumed

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color(0xff140330), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$goal",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "-",
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "$consumed",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "=",
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "$remaining",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Goal", fontSize = 14.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Consumed", fontSize = 14.sp)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Remaining", fontSize = 14.sp)
            }
        }
    }
}



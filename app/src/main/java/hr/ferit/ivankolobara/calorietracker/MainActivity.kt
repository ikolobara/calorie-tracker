package hr.ferit.ivankolobara.calorietracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import hr.ferit.ivankolobara.calorietracker.ui.data.MealViewModel
import hr.ferit.ivankolobara.calorietracker.ui.data.UserMealsViewModel
import hr.ferit.ivankolobara.calorietracker.ui.data.UserViewModel
import hr.ferit.ivankolobara.calorietracker.ui.theme.CalorieTrackerTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val userMealsViewModel by viewModels<UserMealsViewModel>()
        val userViewModel by viewModels<UserViewModel>()
        val mealViewModel by viewModels<MealViewModel>()

        setContent {
            CalorieTrackerTheme {
                NavigationController(userMealsViewModel,
                    userViewModel,
                    mealViewModel)
            }
        }
    }
}

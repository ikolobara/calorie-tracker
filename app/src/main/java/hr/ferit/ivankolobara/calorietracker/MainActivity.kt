package hr.ferit.ivankolobara.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import hr.ferit.ivankolobara.calorietracker.ui.data.UserViewModel
import hr.ferit.ivankolobara.calorietracker.ui.theme.CalorieTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userViewModel by viewModels<UserViewModel>()
        setContent {
            CalorieTrackerTheme {
                NavigationController(userViewModel)
            }
        }
    }
}

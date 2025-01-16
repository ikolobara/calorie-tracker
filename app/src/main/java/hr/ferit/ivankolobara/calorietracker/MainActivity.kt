package hr.ferit.ivankolobara.calorietracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import hr.ferit.ivankolobara.calorietracker.ui.theme.CalorieTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalorieTrackerTheme {
                NavigationController()
            }
        }
    }
}

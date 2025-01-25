package hr.ferit.ivankolobara.calorietracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hr.ferit.ivankolobara.calorietracker.ui.AddFoodScreen
import hr.ferit.ivankolobara.calorietracker.ui.DashboardScreen
import hr.ferit.ivankolobara.calorietracker.ui.ProfileScreen
import hr.ferit.ivankolobara.calorietracker.ui.data.UserViewModel

object Routes {
    const val Dashboard = "dashboard"
    const val Profile = "profile"
    const val AddMeal = "addmeal"
}

@Composable
fun NavigationController(userViewModel: UserViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination =
    Routes.Dashboard) {
        composable(Routes.Dashboard) {
            DashboardScreen(navigation = navController, userViewModel = userViewModel)
        }
        composable(Routes.Profile) {
            ProfileScreen(navigation = navController)
        }
        composable(Routes.AddMeal) {
            AddFoodScreen(navigation = navController)
        }
    }
}
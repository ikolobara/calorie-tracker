package hr.ferit.ivankolobara.calorietracker.ui.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class MealViewModel: ViewModel() {
    private val db = Firebase.firestore
    val mealsData = mutableStateListOf<Meal>()

    init {
        fetchDatabaseData()
    }

    private fun fetchDatabaseData(){
        db.collection("meals")
            .get()
            .addOnSuccessListener { result ->
                for (data in result.documents) {
                    val meal = data.toObject(Meal::class.java)
                    if (meal != null) {
                        meal.id = data.id
                        mealsData.add(meal)
                    }
                }
            }
    }
}

data class Meal(
    var id: String = "",
    var calories: Int = 0,
    var name: String = "",
    var grams: Int = 0
)
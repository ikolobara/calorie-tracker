package hr.ferit.ivankolobara.calorietracker.ui.data

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class MealViewModel: ViewModel() {
    private val db = Firebase.firestore

    fun searchMeals(query: String, onResult: (List<Meal>) -> Unit) {
        db.collection("meals")
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", "$query\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val mealsList = result.documents.mapNotNull { document ->
                    document.toObject(Meal::class.java)?.apply {
                        id = document.id
                    }
                }
                onResult(mealsList)
            }
            .addOnFailureListener { exception ->
                onResult(emptyList())
            }
    }


}

data class Meal(
    var id: String = "",
    var calories: Int = 0,
    var name: String = "",
    var grams: Int = 0
)
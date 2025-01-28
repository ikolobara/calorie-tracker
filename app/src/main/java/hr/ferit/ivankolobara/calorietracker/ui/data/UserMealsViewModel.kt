package hr.ferit.ivankolobara.calorietracker.ui.data

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserMealsViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _userMealsData = MutableStateFlow<List<UserMeals>>(emptyList())
    val userMealsData: StateFlow<List<UserMeals>> get() = _userMealsData

    private val _mealDetailsMap = MutableStateFlow<Map<String, MealDetails>>(emptyMap())
    val mealDetailsMap: StateFlow<Map<String, MealDetails>> get() = _mealDetailsMap

    init {
        fetchDatabaseData("q74Tl77ZmwvcLmGDpvxV")
    }

    private fun fetchDatabaseData(userId: String) {
        db.collection("users").document(userId).collection("meals")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Error fetching real-time updates", exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val mealsList = snapshot.documents.mapNotNull { document ->
                        document.toObject(UserMeals::class.java)?.apply {
                            id = document.id
                        }
                    }

                    _userMealsData.value = mealsList

                    mealsList.forEach { meal ->
                        meal.mealId?.let {
                            fetchMealDetails(it)
                        }
                    }
                } else {
                    _userMealsData.value = emptyList()
                }
            }
    }

    private fun fetchMealDetails(mealDocRef: DocumentReference) {
        mealDocRef.get().addOnSuccessListener { document ->
            val name = document.getString("name") ?: "Unknown Meal"
            val grams = document.getLong("servingSize")?.toInt() ?: 100
            val calories = document.getLong("calories")?.toInt() ?: 100
            val mealDetails = MealDetails(document.id, name, grams, calories)

            _mealDetailsMap.value += (mealDocRef.id to mealDetails)
        }
    }

    fun deleteMealFromDatabase(mealId: String) {
        db.collection("users")
            .document("q74Tl77ZmwvcLmGDpvxV")
            .collection("meals")
            .document(mealId)
            .delete()
    }

    fun addMealToDatabase(meal: MealToAdd, mealId: String) {
        meal.mealId = db.collection("meals").document(mealId)

        db.collection("users")
            .document("q74Tl77ZmwvcLmGDpvxV")
            .collection("meals")
            .add(meal)
    }
}


data class UserMeals(
    var id: String = "",
    var date: Timestamp? = null,
    var servingSize: Int = 0,
    var mealId: DocumentReference? = null,
    var mealType: String = ""
)

data class MealDetails(
    var id: String? = "",
    var name: String?,
    var grams: Int,
    var calories: Int
)

data class MealToAdd(
    var date: Timestamp? = null,
    var servingSize: Int = 0,
    var mealId: DocumentReference? = null,
    var mealType: String = ""
)
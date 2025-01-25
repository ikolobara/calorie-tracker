package hr.ferit.ivankolobara.calorietracker.ui.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    private val db = Firebase.firestore
    val userData = mutableStateOf<User?>(null)

    init {
        fetchDatabaseData("q74Tl77ZmwvcLmGDpvxV")
    }

    private fun fetchDatabaseData(userId: String){
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { result ->
                if (result.exists()) {
                    Log.d("Firestore", "User data: ${result.data}")
                    val user = result.toObject(User::class.java)
                    if (user != null) {
                        user.id = result.id
                        userData.value = user
                    }
                }
            }

    }
}

data class User(
    var id: String = "",
    var activityLevel: String = "",
    var age: Int = 0,
    var dailyCalorieGoal: Int = 0,
    var gender: String = "",
    var height: Int = 0,
    var meals: Map<String, List<UserMeals>> = emptyMap(),
    var weight: Int = 0
)

data class UserMeals(
    var date: String = "",
    var servingSize: Int = 0,
    var mealId: DocumentReference? = null,
    var mealType: String = ""
)
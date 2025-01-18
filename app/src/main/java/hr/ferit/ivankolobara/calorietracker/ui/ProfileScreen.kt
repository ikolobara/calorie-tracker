package hr.ferit.ivankolobara.calorietracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.ferit.ivankolobara.calorietracker.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navigation: NavHostController) {
    var isEditing by remember { mutableStateOf(false) }
    var age by remember { mutableStateOf("25") }
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf("175 cm") }
    var weight by remember { mutableStateOf("70 kg") }
    var activityLevel by remember { mutableStateOf("Moderate") }

    val genders = listOf("Male", "Female")
    val activityLevels = listOf("Low", "Moderate", "High", "Very High")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navigation.navigate(Routes.Dashboard) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { isEditing = false }) {
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
                .fillMaxSize(),
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
            ProfileField("Height", height, isEditing, onValueChange = { height = it })
            ProfileField("Weight", weight, isEditing, onValueChange = { weight = it })
            ProfileDropdownField(
                label = "Activity Level",
                selectedValue = activityLevel,
                isEditing = isEditing,
                options = activityLevels,
                onValueChange = { activityLevel = it }
            )

            if (isEditing) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                    Button(
                        onClick = {
                            isEditing = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            }
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

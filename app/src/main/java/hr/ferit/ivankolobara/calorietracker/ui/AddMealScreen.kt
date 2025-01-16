package hr.ferit.ivankolobara.calorietracker.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hr.ferit.ivankolobara.calorietracker.R

@Preview(showBackground = true)
@Composable
fun AddMealScreen() {
    var selectedMealType by remember { mutableStateOf("") }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snacks")

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
            .padding(top = 128.dp)
    ) {
        SearchMeal(R.drawable.ic_search, "Search...")
        DropdownMenuComponent(
            selectedMealType = selectedMealType,
            mealTypes = mealTypes,
            onMealTypeSelected = { selectedMeal ->
                selectedMealType = selectedMeal
            }
        )
        InputServingSize()
        AddMealButton("Add Meal")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMeal(
    @DrawableRes iconResource: Int,
    labelText: String,
) {
    var searchInput by remember {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = searchInput,
        onValueChange = { searchInput = it },
        label = {
            Text("Search...")
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconResource),
                contentDescription = labelText,
                tint = DarkGray,
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun DropdownMenuComponent(
    selectedMealType: String,
    mealTypes: List<String>,
    onMealTypeSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clickable { isDropdownExpanded = true }) {
        OutlinedTextField(
            value = selectedMealType,
            onValueChange = {},
            label = { Text("Select Meal Type") },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.clickable { isDropdownExpanded = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isDropdownExpanded = true }
        )

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            mealTypes.forEach { mealType ->
                DropdownMenuItem(
                    onClick = {
                        onMealTypeSelected(mealType)
                        isDropdownExpanded = false
                    },
                    text = { Text(text = mealType) }
                )
            }
        }
    }
}


@Composable
fun InputServingSize() {
    var servingSize by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)){
        OutlinedTextField(
            value = servingSize,
            onValueChange = { servingSize = it },
            label = { Text("Serving Size (grams)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AddMealButton(
    text: String,
    // navigation: NavHostController
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {  },
            // onClick = { navigation.navigate(Routes.Dashboard) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.check_mark),
                    contentDescription = text,
                    modifier = Modifier
                        .height(24.dp).width(24.dp)
                )
            }
        }
    }
}

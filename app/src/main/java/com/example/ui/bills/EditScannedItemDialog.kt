package com.example.ui.bills

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodItem
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScannedItemDialog(
    allFoods: List<FoodItem>,
    onDismiss: () -> Unit,
    onFoodSelected: (FoodItem) -> Unit
) {
    var query by remember { mutableStateOf("") }
    
    val filteredFoods = remember(query) {
        if (query.isEmpty()) {
            allFoods.take(50)
        } else {
            allFoods.filter { it.name.contains(query, ignoreCase = true) }.take(50)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Correct Match", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search food...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(filteredFoods) { food ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onFoodSelected(food) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = food.name,
                                fontSize = 16.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        HorizontalDivider(color = Color(0xFFF0F5F1))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GreenPrimary, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White
    )
}

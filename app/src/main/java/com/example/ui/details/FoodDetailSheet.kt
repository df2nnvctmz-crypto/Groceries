package com.example.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodItem
import com.example.ui.components.ScoreRing
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailSheet(
    food: FoodItem,
    swapFood: FoodItem?,
    onDismiss: () -> Unit,
    onSwapClick: (FoodItem) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = BackgroundLight,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Header Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { /* Todo: Favorite */ }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = TextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            // Title & Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = food.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "per 100g",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${food.kcal.toInt()}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "kcal / 100g",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                ScoreRing(score = food.healthScore, size = 80.dp, strokeWidth = 8.dp, textSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Smarter Swap Section
            if (swapFood != null) {
                Text(
                    text = "Smarter Swap",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    onClick = { onSwapClick(swapFood) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF0F5F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🍎", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "RECOMMENDED",
                                color = GreenPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = swapFood.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = swapFood.category,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(GreenPrimary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${swapFood.healthScore} / 100",
                                color = GreenPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            } else if (food.healthScore > 80) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFEAF5EC))
                        .border(1.dp, Color(0xFFD3EAD8), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFD3EAD8), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Checkmark",
                                tint = GreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Great Choice!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "This product is already a healthy option in its category.",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Nutrition Facts
            Text(
                text = "Nutrition Facts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Nutrition Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Per 100g (% of Daily\nValue)",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF0F5F1), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .weight(1f, fill = false)
                        ) {
                            Text(
                                text = "Source: Swiss Food Composition Database",
                                color = GreenPrimary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Daily values roughly based on standard 2000 kcal diet
                    NutritionFactRow(label = "Protein", value = food.proteinG, dailyValue = 50.0)
                    NutritionFactRow(label = "Carbohydrates", value = food.carbsG, dailyValue = 260.0)
                    NutritionFactRow(label = "Sugars", value = food.sugarsG, dailyValue = 50.0, isSubItem = true)
                    NutritionFactRow(label = "Total Fat", value = food.fatG, dailyValue = 70.0)
                    NutritionFactRow(label = "Saturated Fat", value = food.saturatedFatG, dailyValue = 20.0, isSubItem = true)
                    NutritionFactRow(label = "Fiber", value = food.fiberG, dailyValue = 28.0)
                    NutritionFactRow(label = "Salt", value = food.saltG, dailyValue = 6.0, showDivider = false)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            val displayMicros = food.micros.mapNotNull { (key, value) ->
                val (name, unit, dailyValue) = when(key) {
                    "vitamin_a_ug" -> Triple("Vitamin A", "µg", 900.0)
                    "betacarotene_ug" -> Triple("Beta-carotene", "µg", null)
                    "vitamin_b1_mg" -> Triple("Vitamin B1", "mg", 1.2)
                    "vitamin_b2_mg" -> Triple("Vitamin B2", "mg", 1.3)
                    "vitamin_b6_mg" -> Triple("Vitamin B6", "mg", 1.7)
                    "vitamin_b12_ug" -> Triple("Vitamin B12", "µg", 2.4)
                    "niacin_mg" -> Triple("Niacin", "mg", 16.0)
                    "folate_ug" -> Triple("Folate", "µg", 400.0)
                    "pantothenic_acid_mg" -> Triple("Pantothenic acid", "mg", 5.0)
                    "vitamin_c_mg" -> Triple("Vitamin C", "mg", 80.0)
                    "vitamin_d_ug" -> Triple("Vitamin D", "µg", 20.0)
                    "vitamin_e_mg" -> Triple("Vitamin E", "mg", 12.0)
                    "sodium_mg" -> Triple("Sodium", "mg", 2300.0)
                    "potassium_mg" -> Triple("Potassium", "mg", 4700.0)
                    "chloride_mg" -> Triple("Chloride", "mg", 2300.0)
                    "calcium_mg" -> Triple("Calcium", "mg", 1000.0)
                    "magnesium_mg" -> Triple("Magnesium", "mg", 420.0)
                    "phosphorus_mg" -> Triple("Phosphorus", "mg", 700.0)
                    "iron_mg" -> Triple("Iron", "mg", 14.0)
                    "iodide_ug" -> Triple("Iodide", "µg", 150.0)
                    "zinc_mg" -> Triple("Zinc", "mg", 11.0)
                    else -> return@mapNotNull null
                }
                
                val amountStr = if (value % 1.0 == 0.0) "${value.toInt()}$unit" else "${String.format(java.util.Locale.US, "%.1f", value)}$unit"
                val percentage = dailyValue?.let { ((value / it) * 100).toInt() }
                MicronutrientData(name, amountStr, percentage)
            }.sortedBy { it.name }

            if (displayMicros.isNotEmpty()) {
                // Vitamins & Minerals
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Vitamins & Minerals (per 100g)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        displayMicros.forEachIndexed { index, micro ->
                            MicronutrientFactRow(
                                label = micro.name,
                                amountStr = micro.amount,
                                percentage = micro.percentage,
                                showDivider = index < displayMicros.size - 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

data class MicronutrientData(val name: String, val amount: String, val percentage: Int?)

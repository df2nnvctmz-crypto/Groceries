package com.example.ui.bills

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BillEntity
import com.example.data.FoodItem
import com.example.ui.components.ScoreRing
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.AppTheme
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BillsScreen(
    bills: List<BillEntity>,
    allFoods: List<FoodItem>,
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit = {},
    onDeleteBill: (BillEntity) -> Unit = {},
    onFoodClick: (FoodItem) -> Unit = {}
) {
    val overallScore = if (bills.isNotEmpty()) bills.map { it.score }.average().toInt() else 0
    val overallScoreColor = when {
        overallScore >= 80 -> com.example.ui.theme.ScoreRingGreen
        overallScore >= 40 -> com.example.ui.theme.ScoreRingAmber
        else -> com.example.ui.theme.ScoreRingRed
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.paddings.outerScreen),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                Text(
                    text = "Bills",
                    fontSize = AppTheme.fontSizes.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Track your receipts and health points",
                    fontSize = AppTheme.fontSizes.bodyMedium,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppTheme.paddings.innerCard * 1.5f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ScoreRing(
                            score = overallScore,
                            size = AppTheme.iconSizes.largeIcon * 3.75f,
                            strokeWidth = AppTheme.iconSizes.largeIcon * 0.375f,
                            textSize = AppTheme.fontSizes.titleLarge * 1.125f
                        )
                        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                        Text(
                            text = "Overall health points",
                            color = overallScoreColor,
                            fontSize = AppTheme.fontSizes.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                
                Button(
                    onClick = onScanClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
                    Text("Scan Receipt", fontSize = AppTheme.fontSizes.bodyMedium, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer * 1.5f))
                
                Text(
                    text = "THIS WEEK",
                    fontSize = AppTheme.fontSizes.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
            }
            
            items(bills) { bill ->
                BillItemCard(
                    bill = bill,
                    allFoods = allFoods,
                    onDelete = { onDeleteBill(bill) },
                    onFoodClick = onFoodClick
                )
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
            }
        }
    }
}

@Composable
fun BillItemCard(
    bill: BillEntity,
    allFoods: List<FoodItem>,
    onDelete: () -> Unit,
    onFoodClick: (FoodItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
    val dateStr = dateFormat.format(Date(bill.date))
    val billColor = when {
        bill.score >= 80 -> com.example.ui.theme.ScoreRingGreen
        bill.score >= 40 -> com.example.ui.theme.ScoreRingAmber
        else -> com.example.ui.theme.ScoreRingRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(AppTheme.paddings.innerCard),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F5F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = bill.storeName,
                        fontWeight = FontWeight.Bold,
                        fontSize = AppTheme.fontSizes.bodyMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$dateStr • ${bill.totalItems} items",
                        color = GreenPrimary,
                        fontSize = AppTheme.fontSizes.bodySmall
                    )
                }
                
                Box(
                    modifier = Modifier
                        .background(billColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${bill.score}%",
                        color = billColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = AppTheme.fontSizes.bodySmall
                    )
                }
                
                Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
                
                IconButton(onClick = onDelete, modifier = Modifier.size(AppTheme.iconSizes.standardIcon)) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = TextSecondary,
                        modifier = Modifier.size(AppTheme.iconSizes.standardIcon * 0.85f)
                    )
                }
                
                Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
                
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = TextSecondary
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = AppTheme.paddings.innerCard, end = AppTheme.paddings.innerCard, bottom = AppTheme.paddings.innerCard)
                ) {
                    val parsedItems = remember(bill.itemsJson) {
                        val result = mutableListOf<Pair<String, FoodItem?>>()
                        try {
                            val jsonArray = JSONArray(bill.itemsJson)
                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                val text = obj.getString("originalText")
                                val matchedId = if (obj.has("matchedFoodId") && !obj.isNull("matchedFoodId")) obj.getString("matchedFoodId") else null
                                val matchedFood = if (matchedId != null) allFoods.find { it.id == matchedId } else null
                                result.add(text to matchedFood)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        result
                    }

                    parsedItems.forEach { (text, food) ->
                        if (food != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = AppTheme.paddings.elementSpacer / 2),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onFoodClick(food) }
                                            .padding(AppTheme.paddings.innerCard * 0.75f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    color = when (food.nutriGrade) {
                                                        "A" -> Color(0xFF008b4c)
                                                        "B" -> Color(0xFF80c142)
                                                        "C" -> Color(0xFFfeca0b)
                                                        "D" -> Color(0xFFf08100)
                                                        "E" -> Color(0xFFe63e11)
                                                        else -> Color.Gray
                                                    }.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(18.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = food.healthScore.toString(),
                                                color = when (food.nutriGrade) {
                                                    "A" -> Color(0xFF008b4c)
                                                    "B" -> Color(0xFF80c142)
                                                    "C" -> Color(0xFFfeca0b)
                                                    "D" -> Color(0xFFf08100)
                                                    "E" -> Color(0xFFe63e11)
                                                    else -> Color.Gray
                                                },
                                                fontWeight = FontWeight.Bold,
                                                fontSize = AppTheme.fontSizes.bodySmall
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer))
                                        
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = food.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = AppTheme.fontSizes.bodyMedium,
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = text,
                                                fontSize = AppTheme.fontSizes.bodySmall,
                                                color = GreenPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(AppTheme.iconSizes.smallIcon)
                                        )
                                    }
                                    
                                    if (food.swapSuggestionId != null) {
                                        val swapFood = allFoods.find { it.id == food.swapSuggestionId }
                                        if (swapFood != null) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { onFoodClick(swapFood) }
                                                    .background(Color(0xFFE8F5E9))
                                                    .padding(horizontal = AppTheme.paddings.innerCard, vertical = AppTheme.paddings.innerCard * 0.5f)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(
                                                            text = "SWAP: ",
                                                            fontSize = AppTheme.fontSizes.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = GreenPrimary
                                                        )
                                                        Text(
                                                            text = swapFood.name,
                                                            fontSize = AppTheme.fontSizes.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = TextPrimary,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
                                                    Box(
                                                        modifier = Modifier
                                                            .background(Color.White, RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = swapFood.healthScore.toString(),
                                                            fontSize = AppTheme.fontSizes.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = GreenPrimary
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

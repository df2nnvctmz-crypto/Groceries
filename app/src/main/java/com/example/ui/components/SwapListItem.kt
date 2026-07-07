package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodItem
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.ScoreRingAmber
import com.example.ui.theme.ScoreRingRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import com.example.ui.theme.AppTheme

@Composable
fun SwapListItem(
    original: FoodItem,
    swap: FoodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteToggle: (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(AppTheme.paddings.innerCard)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Original food
                Column(modifier = Modifier.weight(1f)) {
                    val colorOriginal = when {
                        original.healthScore >= 80 -> GreenPrimary
                        original.healthScore >= 40 -> ScoreRingAmber
                        else -> ScoreRingRed
                    }
                    Text(
                        text = original.healthScore.toString(),
                        color = colorOriginal,
                        fontWeight = FontWeight.Bold,
                        fontSize = AppTheme.fontSizes.titleMedium
                    )
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
                    Text(
                        text = original.name,
                        color = TextPrimary,
                        fontSize = AppTheme.fontSizes.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 8))
                    Text(
                        text = "${original.kcal.toInt()} kcal / 100g",
                        color = TextSecondary,
                        fontSize = AppTheme.fontSizes.bodySmall
                    )
                }
                
                // Arrow
                Box(
                    modifier = Modifier
                        .padding(horizontal = AppTheme.paddings.elementSpacer / 2)
                        .size(AppTheme.iconSizes.largeIcon)
                        .background(GreenPrimary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Swap to",
                        tint = GreenPrimary,
                        modifier = Modifier.size(AppTheme.iconSizes.smallIcon)
                    )
                }

                // Swap food
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = swap.healthScore.toString(),
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = AppTheme.fontSizes.titleMedium
                    )
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
                    Text(
                        text = swap.name,
                        color = TextPrimary,
                        fontSize = AppTheme.fontSizes.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 8))
                    Text(
                        text = "${swap.kcal.toInt()} kcal / 100g",
                        color = TextSecondary,
                        fontSize = AppTheme.fontSizes.bodySmall
                    )
                }

                // Actions (Favorite + Expand)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = AppTheme.paddings.elementSpacer / 4)
                ) {
                    if (onFavoriteToggle != null) {
                        IconButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier.size(AppTheme.iconSizes.largeIcon)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite Swap",
                                tint = if (isFavorite) Color(0xFFF43F5E) else TextSecondary,
                                modifier = Modifier.size(AppTheme.iconSizes.standardIcon * 0.85f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = TextSecondary,
                        modifier = Modifier.size(AppTheme.iconSizes.standardIcon)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Healthier option in the same\ncategory",
                    color = TextSecondary,
                    fontSize = AppTheme.fontSizes.bodySmall,
                    lineHeight = AppTheme.fontSizes.bodySmall * 1.33f
                )
                
                Box(
                    modifier = Modifier
                        .background(GreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                        .padding(horizontal = AppTheme.paddings.elementSpacer * 0.75f, vertical = AppTheme.paddings.elementSpacer * 0.35f)
                ) {
                    Text(
                        text = "+${swap.healthScore - original.healthScore}%",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = AppTheme.fontSizes.bodySmall
                    )
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Restaurant
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
import com.example.ui.theme.*

import com.example.ui.theme.AppTheme

@Composable
fun FoodListItem(
    item: FoodItem,
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
        Row(
            modifier = Modifier.padding(AppTheme.paddings.innerCard),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(AppTheme.iconSizes.largeIcon * 1.5f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F5F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Restaurant,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(AppTheme.iconSizes.standardIcon)
                )
            }
            
            Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer))
            
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = AppTheme.fontSizes.bodyMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
                Text(
                    text = "${item.category} • ${item.kcal.toInt()} kcal/100g",
                    color = TextSecondary,
                    fontSize = AppTheme.fontSizes.bodySmall
                )
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 2))
                NutriScoreBadge(grade = item.nutriGrade)
            }
            
            Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
            
            // Favorite Toggle
            if (onFavoriteToggle != null) {
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(AppTheme.iconSizes.largeIcon)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFF43F5E) else TextSecondary,
                        modifier = Modifier.size(AppTheme.iconSizes.standardIcon * 0.85f)
                    )
                }
            } else {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color(0xFFF43F5E) else TextSecondary,
                    modifier = Modifier.size(AppTheme.iconSizes.smallIcon)
                )
            }
            
            Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
            
            // Score
            ScoreRing(
                score = item.healthScore,
                size = AppTheme.iconSizes.largeIcon * 1.5f,
                strokeWidth = AppTheme.iconSizes.smallIcon * 0.35f,
                textSize = AppTheme.fontSizes.titleMedium * 0.9f
            )
        }
    }
}

@Composable
fun NutriScoreBadge(grade: String) {
    val (bgColor, textColor) = when (grade.uppercase()) {
        "A" -> NutriA to Color.White
        "B" -> NutriB to Color.White
        "C" -> NutriC to Color.White
        "D" -> NutriD to Color.White
        else -> NutriE to Color.White
    }
    
    Box(
        modifier = Modifier
            .background(bgColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
            .padding(horizontal = AppTheme.paddings.elementSpacer / 2, vertical = AppTheme.paddings.elementSpacer / 8)
    ) {
        Text(
            text = "NUTRI SCORE $grade",
            color = bgColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = AppTheme.fontSizes.labelSmall
        )
    }
}

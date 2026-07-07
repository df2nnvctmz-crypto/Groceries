package com.example.ui.swaps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodItem
import com.example.ui.components.SwapListItem
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.AppTheme

@Composable
fun SwapsScreen(
    badFoods: List<FoodItem>,
    getSwapsFor: (FoodItem) -> List<FoodItem>,
    onFoodClick: (FoodItem) -> Unit,
    modifier: Modifier = Modifier,
    favoriteFoodIds: Set<String> = emptySet(),
    favoriteSwapIds: Set<String> = emptySet(),
    onToggleFavorite: (String) -> Unit = {},
    onToggleSwapFavorite: (String, String) -> Unit = { _, _ -> }
) {
    var favoritesOnly by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = AppTheme.paddings.outerScreen)
    ) {
        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
        Text(
            text = "Smarter Swaps",
            fontSize = AppTheme.fontSizes.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Tap any card to compare nutrients",
            fontSize = AppTheme.fontSizes.bodyMedium,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommended for You",
                fontSize = AppTheme.fontSizes.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { favoritesOnly = !favoritesOnly },
                    modifier = Modifier.size(AppTheme.iconSizes.largeIcon * 1.125f)
                ) {
                    Icon(
                        imageVector = if (favoritesOnly) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorites Only",
                        tint = if (favoritesOnly) Color(0xFFF43F5E) else TextSecondary,
                        modifier = Modifier.size(AppTheme.iconSizes.standardIcon * 0.85f)
                    )
                }
                Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 4))
                Box(
                    modifier = Modifier
                        .background(GreenPrimary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Balanced",
                        color = GreenPrimary,
                        fontSize = AppTheme.fontSizes.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
        
        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer)
        ) {
            items(badFoods) { badFood ->
                val swaps = getSwapsFor(badFood)
                if (swaps.isNotEmpty()) {
                    val swap = swaps.first()
                    val swapKey = "${badFood.id}::${swap.id}"
                    val isFavoriteSwap = favoriteSwapIds.contains(swapKey)
                    val isBadFoodFavorite = favoriteFoodIds.contains(badFood.id)
                    val isSwapFoodFavorite = favoriteFoodIds.contains(swap.id)
                    
                    val passesFavoritesFilter = !favoritesOnly || (isFavoriteSwap || isBadFoodFavorite || isSwapFoodFavorite)
                    
                    if (passesFavoritesFilter) {
                        SwapListItem(
                            original = badFood, 
                            swap = swap,
                            onClick = { onFoodClick(swap) },
                            isFavorite = isFavoriteSwap,
                            onFavoriteToggle = { onToggleSwapFavorite(badFood.id, swap.id) }
                        )
                    }
                }
            }
        }
    }
}

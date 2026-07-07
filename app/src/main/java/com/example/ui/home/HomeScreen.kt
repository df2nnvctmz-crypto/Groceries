package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.FoodItem
import com.example.ui.components.ScoreRing
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.AppTheme

@Composable
fun HomeScreen(
    userProfile: com.example.domain.UserProfile,
    spotlightFood: FoodItem?,
    recommendedFoods: List<FoodItem>,
    onFoodClick: (FoodItem) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    bills: List<com.example.data.BillEntity> = emptyList(),
    favoriteFoodIds: Set<String> = emptySet(),
    favoriteSwapIds: Set<String> = emptySet(),
    allFoods: List<FoodItem> = emptyList(),
    onToggleFavorite: (String) -> Unit = {},
    onToggleSwapFavorite: (String, String) -> Unit = { _, _ -> }
) {
    var showNutrientGuide by remember { mutableStateOf(false) }

    if (showNutrientGuide) {
        DailyNutrientGuideDialog(
            profile = userProfile,
            onDismiss = { showNutrientGuide = false }
        )
    }

    val overallScore = if (bills.isNotEmpty()) bills.map { it.score }.average().toInt() else 66
    val overallScoreColor = when {
        overallScore >= 80 -> com.example.ui.theme.ScoreRingGreen
        overallScore >= 40 -> com.example.ui.theme.ScoreRingAmber
        else -> com.example.ui.theme.ScoreRingRed
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
            .padding(AppTheme.paddings.outerScreen)
    ) {
        // Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Groceries",
                    fontSize = AppTheme.fontSizes.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .size(AppTheme.iconSizes.largeIcon * 1.5f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = TextPrimary,
                        modifier = Modifier.size(AppTheme.iconSizes.standardIcon)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Smart Nutrition Guide",
                    fontSize = AppTheme.fontSizes.bodyMedium,
                    color = TextSecondary
                )
                
                // Calorie Target Button (Top Right)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFEAF2EC))
                        .clickable { showNutrientGuide = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Whatshot,
                        contentDescription = "Calorie Target",
                        tint = GreenPrimary,
                        modifier = Modifier.size(AppTheme.iconSizes.smallIcon)
                    )
                    val formatter = java.text.DecimalFormat("#,###")
                    val calText = formatter.format(userProfile.dailyCalorieTarget).replace(',', '.')
                    Text(
                        text = "$calText kcal",
                        fontSize = AppTheme.fontSizes.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
            
            // Personalise Button (Bottom Left)
            val prefText = if (userProfile.dietaryPreference == com.example.domain.DietaryPreference.BALANCED) {
                "Personalise"
            } else {
                userProfile.dietaryPreference.title.substringBefore(" (")
            }
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(1.dp, GreenPrimary.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .clickable { onProfileClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Personalise",
                    tint = GreenPrimary,
                    modifier = Modifier.size(AppTheme.iconSizes.smallIcon)
                )
                Text(
                    text = "$prefText • Recommended",
                    fontSize = AppTheme.fontSizes.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
        
        // Health Points Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(AppTheme.paddings.innerCard),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreRing(
                    score = overallScore,
                    size = AppTheme.iconSizes.largeIcon * 2.5f,
                    strokeWidth = AppTheme.iconSizes.smallIcon * 0.5f,
                    textSize = AppTheme.fontSizes.titleMedium
                )
                Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "THIS WEEK",
                        color = overallScoreColor,
                        fontSize = AppTheme.fontSizes.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
                    Text(
                        text = "Health Points",
                        fontWeight = FontWeight.Bold,
                        fontSize = AppTheme.fontSizes.titleMedium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
                    Text(
                        text = "Scan a receipt to start earning points",
                        color = TextSecondary,
                        fontSize = AppTheme.fontSizes.bodySmall
                    )
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer * 0.75f))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(AppTheme.iconSizes.smallIcon))
                        Spacer(modifier = Modifier.width(AppTheme.paddings.elementSpacer / 2))
                        Text("Scan a receipt", fontSize = AppTheme.fontSizes.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
        
        // Today's Spotlight
        if (spotlightFood != null) {
            Card(
                onClick = { onFoodClick(spotlightFood) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(AppTheme.paddings.innerCard)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TODAY'S SPOTLIGHT",
                                color = GreenPrimary,
                                fontSize = AppTheme.fontSizes.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
                            Text(
                                text = spotlightFood.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = AppTheme.fontSizes.titleMedium,
                                color = TextPrimary
                            )
                        }
                        ScoreRing(score = spotlightFood.healthScore, size = AppTheme.iconSizes.largeIcon * 2)
                    }
                    
                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BackgroundLight, RoundedCornerShape(16.dp))
                            .padding(AppTheme.paddings.innerCard),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NutrientStat("${spotlightFood.kcal.toInt()} kcal", "Calories", isBold = true)
                        NutrientStat("${String.format(java.util.Locale.US, "%.1f", spotlightFood.proteinG)}g", "Protein")
                        NutrientStat("${String.format(java.util.Locale.US, "%.1f", spotlightFood.carbsG)}g", "Carbs")
                        NutrientStat("${String.format(java.util.Locale.US, "%.1f", spotlightFood.fatG)}g", "Fat")
                    }
                }
            }
        }
        
        // My Favorites Section
        val savedFoods = allFoods.filter { favoriteFoodIds.contains(it.id) }
        val savedSwaps = favoriteSwapIds.mapNotNull { key ->
            val parts = key.split("::")
            if (parts.size == 2) {
                val original = allFoods.find { it.id == parts[0] }
                val alternative = allFoods.find { it.id == parts[1] }
                if (original != null && alternative != null) {
                    original to alternative
                } else null
            } else null
        }

        if (savedFoods.isNotEmpty() || savedSwaps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
            
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFF43F5E), // Tailwind rose-500
                    modifier = Modifier.size(AppTheme.iconSizes.standardIcon)
                )
                Text(
                    text = "My Favorites",
                    fontSize = AppTheme.fontSizes.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            
            // Sub-Section A: Saved Foods
            if (savedFoods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                Text(
                    text = "SAVED FOODS",
                    fontSize = AppTheme.fontSizes.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA3A3A3),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(savedFoods) { food ->
                        val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
                        Card(
                            onClick = { onFoodClick(food) },
                            modifier = Modifier.width(140.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier.padding(AppTheme.paddings.innerCard),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ScoreRing(score = food.healthScore, size = AppTheme.iconSizes.largeIcon * 2)
                                    }
                                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                                    Text(
                                        text = food.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = AppTheme.fontSizes.bodyMedium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
                                    Text(
                                        text = "${food.kcal.toInt()} kcal / 100g",
                                        fontSize = AppTheme.fontSizes.bodySmall,
                                        color = TextSecondary,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                                
                                // Absolute-positioned filled Heart button in the top-right corner
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                            onToggleFavorite(food.id)
                                        },
                                        modifier = Modifier.size(AppTheme.iconSizes.largeIcon)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Unfavorite",
                                            tint = Color(0xFFF43F5E),
                                            modifier = Modifier.size(AppTheme.iconSizes.standardIcon * 0.75f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Sub-Section B: Saved Swaps
            if (savedSwaps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                Text(
                    text = "SAVED SWAPS",
                    fontSize = AppTheme.fontSizes.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA3A3A3),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    savedSwaps.forEach { (original, alternative) ->
                        val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
                        Card(
                            onClick = { onFoodClick(alternative) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .padding(AppTheme.paddings.innerCard)
                                        .padding(end = AppTheme.iconSizes.largeIcon) // Prevent overlapping with Heart button!
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = original.name,
                                            fontSize = AppTheme.fontSizes.bodySmall,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = TextSecondary,
                                            modifier = Modifier.size(AppTheme.iconSizes.smallIcon)
                                        )
                                        Text(
                                            text = alternative.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = AppTheme.fontSizes.bodyMedium,
                                            color = Color(0xFF519D46),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 2))
                                    Text(
                                        text = "Upgrade to ${alternative.name} and boost score by +${alternative.healthScore - original.healthScore}%",
                                        fontSize = AppTheme.fontSizes.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                
                                // Absolute-positioned filled Heart button in the top-right corner
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                ) {
                                    IconButton(
                                        onClick = {
                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                            onToggleSwapFavorite(original.id, alternative.id)
                                        },
                                        modifier = Modifier.size(AppTheme.iconSizes.largeIcon)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Unfavorite",
                                            tint = Color(0xFFF43F5E),
                                            modifier = Modifier.size(AppTheme.iconSizes.standardIcon * 0.75f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer * 1.5f))
        
        // Recommended for You
        Text(
            text = "Recommended for You",
            fontSize = AppTheme.fontSizes.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer)) {
            items(recommendedFoods) { food ->
                RecommendedFoodCard(food = food, onClick = { onFoodClick(food) })
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav
    }
}

@Composable
fun NutrientStat(value: String, label: String, isBold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            fontSize = AppTheme.fontSizes.bodyMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
        Text(
            text = label,
            fontSize = AppTheme.fontSizes.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
fun RecommendedFoodCard(food: FoodItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.paddings.innerCard),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScoreRing(score = food.healthScore, size = AppTheme.iconSizes.largeIcon * 2)
            Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
            Text(
                text = food.name,
                fontWeight = FontWeight.Bold,
                fontSize = AppTheme.fontSizes.bodyMedium,
                color = TextPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer / 4))
            Text(
                text = "${food.kcal.toInt()} kcal / 100g",
                fontSize = AppTheme.fontSizes.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyNutrientGuideDialog(
    profile: com.example.domain.UserProfile,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppTheme.paddings.outerScreen)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppTheme.iconSizes.largeIcon * 1.25f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFEAF2EC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Whatshot,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(AppTheme.iconSizes.standardIcon * 0.85f)
                            )
                        }
                        Column {
                            Text(
                                text = "Daily Nutrient Guide",
                                fontSize = AppTheme.fontSizes.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "BASED ON YOUR PROFILE",
                                fontSize = AppTheme.fontSizes.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    
                    // Close Button
                    Box(
                        modifier = Modifier
                            .size(AppTheme.iconSizes.largeIcon)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF3F4F6))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = TextPrimary,
                            modifier = Modifier.size(AppTheme.iconSizes.smallIcon)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                
                // Scrollable Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Daily Budget Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF4F7F5))
                                .padding(AppTheme.paddings.innerCard)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 2)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Daily Budget:",
                                        fontSize = AppTheme.fontSizes.bodyMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    val formatter = java.text.DecimalFormat("#,###")
                                    val calText = formatter.format(profile.dailyCalorieTarget).replace(',', '.')
                                    Text(
                                        text = "$calText kcal",
                                        fontSize = AppTheme.fontSizes.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                
                                val sexText = profile.sex.name.lowercase().replaceFirstChar { it.uppercase() }
                                val activityText = profile.activityLevel.title
                                val prefText = profile.dietaryPreference.title.substringBefore(" (")
                                
                                Text(
                                    text = "Optimised for: $sexText, ${profile.age} yrs, ${profile.weight}kg, ${profile.height}cm ($activityText) with a \"$prefText\" dietary preference.",
                                    fontSize = AppTheme.fontSizes.bodySmall,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                        
                        // Macronutrient Split Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "MACRONUTRIENT SPLIT",
                                fontSize = AppTheme.fontSizes.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            
                            val calorieTarget = profile.dailyCalorieTarget
                            val (proteinPct, carbsPct, fatPct) = when (profile.dietaryPreference) {
                                com.example.domain.DietaryPreference.HIGH_PROTEIN -> Triple(0.30, 0.40, 0.30)
                                com.example.domain.DietaryPreference.LOW_CARB -> Triple(0.25, 0.15, 0.60)
                                else -> Triple(0.20, 0.50, 0.30)
                            }
                            
                            val proteinG = (calorieTarget * proteinPct / 4).toInt()
                            val carbsG = (calorieTarget * carbsPct / 4).toInt()
                            val fatG = (calorieTarget * fatPct / 9).toInt()
                            
                            // Protein Row
                            MacroRow(
                                label = "Protein",
                                grams = proteinG,
                                percentage = (proteinPct * 100).toInt(),
                                color = Color(0xFF2ECC71),
                                progress = proteinPct.toFloat()
                            )
                            
                            // Carbs Row
                            MacroRow(
                                label = "Carbohydrates",
                                grams = carbsG,
                                percentage = (carbsPct * 100).toInt(),
                                color = Color(0xFFF39C12),
                                progress = carbsPct.toFloat()
                            )
                            
                            // Sugars nested row (< 10% of total calories limit)
                            val sugarsPct = 0.10
                            val sugarsG = (calorieTarget * sugarsPct / 4).toInt()
                            SubMacroRow(
                                label = "Sugars (Limit)",
                                grams = sugarsG,
                                percentage = (sugarsPct * 100).toInt(),
                                color = Color(0xFFF1C40F),
                                progress = 0.4f
                            )
                            
                            // Fats Row
                            MacroRow(
                                label = "Fats",
                                grams = fatG,
                                percentage = (fatPct * 100).toInt(),
                                color = Color(0xFFE74C3C),
                                progress = fatPct.toFloat()
                            )
                            
                            // Saturated Fat nested row (< 10% of total calories limit)
                            val satFatPct = 0.10
                            val satFatG = (calorieTarget * satFatPct / 9).toInt()
                            SubMacroRow(
                                label = "Saturated Fat (Limit)",
                                grams = satFatG,
                                percentage = (satFatPct * 100).toInt(),
                                color = Color(0xFFC0392B),
                                progress = 0.3f
                            )
                            
                            // Unsaturated Fat nested row (Remaining fats)
                            val unsatFatPct = fatPct - satFatPct
                            val unsatFatG = (calorieTarget * unsatFatPct / 9).toInt()
                            SubMacroRow(
                                label = "Unsaturated Fat (Goal)",
                                grams = unsatFatG,
                                percentage = (unsatFatPct * 100).toInt(),
                                color = Color(0xFF1ABC9C),
                                progress = 0.7f
                            )
                            
                            // Dietary Fiber Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(AppTheme.iconSizes.smallIcon / 2)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF1ABC9C))
                                    )
                                    Text(
                                        text = "Dietary Fiber",
                                        fontSize = AppTheme.fontSizes.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                }
                                Text(
                                    text = "28g",
                                    fontSize = AppTheme.fontSizes.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                        
                        // Recommended Micronutrients Section
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "RECOMMENDED MICRONUTRIENTS",
                                fontSize = AppTheme.fontSizes.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            
                            val ironAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "8 mg" else "18 mg"
                            val zincAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "11 mg" else "8 mg"
                            val vitAAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "900 mcg" else "700 mcg"
                            val vitCAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "90 mg" else "75 mg"
                            val potassiumAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "3.400 mg" else "2.600 mg"
                            val magnesiumAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "400 mg" else "310 mg"
                            val thiaminAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "1.2 mg" else "1.1 mg"
                            val riboflavinAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "1.3 mg" else "1.1 mg"
                            val niacinAmt = if (profile.sex == com.example.domain.BiologicalSex.MALE) "16 mg" else "14 mg"
                            
                            MicroRow(label = "Calcium", value = "1.000 mg", color = Color(0xFF9B59B6))
                            MicroRow(label = "Vitamin D", value = "600 IU (15 mcg)", color = Color(0xFFF1C40F))
                            MicroRow(label = "Iron", value = ironAmt, color = Color(0xFFE67E22))
                            MicroRow(label = "Zinc", value = zincAmt, color = Color(0xFF34495E))
                            MicroRow(label = "Vitamin A", value = vitAAmt, color = Color(0xFF1ABC9C))
                            MicroRow(label = "Vitamin C", value = vitCAmt, color = Color(0xFFE74C3C))
                            MicroRow(label = "Sodium / Salt", value = "< 5.0 g", color = Color(0xFF95A5A6))
                            MicroRow(label = "Potassium", value = potassiumAmt, color = Color(0xFF2ECC71))
                            MicroRow(label = "Magnesium", value = magnesiumAmt, color = Color(0xFF3498DB))
                            MicroRow(label = "Phosphorus", value = "700 mg", color = Color(0xFFD35400))
                            MicroRow(label = "Vitamin B1 (Thiamin)", value = thiaminAmt, color = Color(0xFF16A085))
                            MicroRow(label = "Vitamin B2 (Riboflavin)", value = riboflavinAmt, color = Color(0xFF27AE60))
                            MicroRow(label = "Vitamin B3 (Niacin)", value = niacinAmt, color = Color(0xFF2980B9))
                            MicroRow(label = "Vitamin B5 (Pantothenic Acid)", value = "5 mg", color = Color(0xFF8E44AD))
                            MicroRow(label = "Vitamin B6", value = "1.3 mg", color = Color(0xFF2C3E50))
                            MicroRow(label = "Vitamin B9 (Folate)", value = "400 mcg", color = Color(0xFFF39C12))
                            MicroRow(label = "Vitamin B12", value = "2.4 mcg", color = Color(0xFFC0392B))
                            MicroRow(label = "Vitamin E", value = "15 mg", color = Color(0xFF7F8C8D))
                            MicroRow(label = "Iodide (Iodine)", value = "150 mcg", color = Color(0xFF113F67))
                            MicroRow(label = "Chloride", value = "2.3 g", color = Color(0xFF38598B))
                        }
                        
                        Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer * 1.5f))
                    }
                }
                
                Spacer(modifier = Modifier.height(AppTheme.paddings.elementSpacer))
                
                // Got it! Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Got it!",
                        fontSize = AppTheme.fontSizes.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MacroRow(
    label: String,
    grams: Int,
    percentage: Int,
    color: Color,
    progress: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 4)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 2)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppTheme.iconSizes.smallIcon / 2)
                        .clip(RoundedCornerShape(4.dp))
                        .background(color)
                )
                Text(
                    text = label,
                    fontSize = AppTheme.fontSizes.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 4)) {
                Text(
                    text = "${grams}g",
                    fontSize = AppTheme.fontSizes.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "($percentage%)",
                    fontSize = AppTheme.fontSizes.bodySmall,
                    color = TextSecondary
                )
            }
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFFF3F4F6)
        )
    }
}

@Composable
fun SubMacroRow(
    label: String,
    grams: Int,
    percentage: Int,
    color: Color,
    progress: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = AppTheme.paddings.outerScreen),
        verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 4)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 2)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppTheme.iconSizes.smallIcon / 4)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color.copy(alpha = 0.8f))
                )
                Text(
                    text = label,
                    fontSize = AppTheme.fontSizes.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 4)) {
                Text(
                    text = "${grams}g",
                    fontSize = AppTheme.fontSizes.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "($percentage%)",
                    fontSize = AppTheme.fontSizes.labelSmall,
                    color = TextSecondary
                )
            }
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color.copy(alpha = 0.8f),
            trackColor = Color(0xFFF3F4F6)
        )
    }
}

@Composable
fun MicroRow(
    label: String,
    value: String,
    color: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 4)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.paddings.elementSpacer / 2)
            ) {
                Box(
                    modifier = Modifier
                        .size(AppTheme.iconSizes.smallIcon * 0.35f)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
                Text(
                    text = label,
                    fontSize = AppTheme.fontSizes.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
            Text(
                text = value,
                fontSize = AppTheme.fontSizes.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        LinearProgressIndicator(
            progress = 1.0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color.copy(alpha = 0.35f),
            trackColor = Color(0xFFF3F4F6)
        )
    }
}

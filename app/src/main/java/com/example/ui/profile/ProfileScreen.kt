package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ActivityLevel
import com.example.domain.BiologicalSex
import com.example.domain.DietaryPreference
import com.example.domain.UserProfile
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile,
    onProfileChange: (UserProfile) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "My Profile",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Personalise your nutrition experience",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Language Select
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Language / Sprache",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "App language",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
                
                Row(
                    modifier = Modifier
                        .background(BackgroundLight, RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    LanguageToggle(
                        text = "English", 
                        selected = profile.language == "English",
                        onClick = { onProfileChange(profile.copy(language = "English")) }
                    )
                    LanguageToggle(
                        text = "Deutsch", 
                        selected = profile.language == "Deutsch",
                        onClick = { onProfileChange(profile.copy(language = "Deutsch")) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Calorie Target Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F5F1)), // Light green tint
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DAILY CALORIE TARGET",
                        color = GreenPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val formatter = DecimalFormat("#,###")
                    Text(
                        text = "${formatter.format(profile.dailyCalorieTarget)} kcal",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calculated from your profile • Tap to view breakdown",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 24.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Personal Info Section
        SectionTitle(icon = "👤", title = "Personal Info")
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Biological sex",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SexToggleButton(
                        text = "Male",
                        selected = profile.sex == BiologicalSex.MALE,
                        onClick = { onProfileChange(profile.copy(sex = BiologicalSex.MALE)) },
                        modifier = Modifier.weight(1f)
                    )
                    SexToggleButton(
                        text = "Female",
                        selected = profile.sex == BiologicalSex.FEMALE,
                        onClick = { onProfileChange(profile.copy(sex = BiologicalSex.FEMALE)) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VitalInput(
                        label = "Age",
                        value = if (profile.age == 0) "" else profile.age.toString(),
                        unit = "yrs",
                        onValueChange = { 
                            if (it.isEmpty()) onProfileChange(profile.copy(age = 0))
                            else {
                                val num = it.toIntOrNull()
                                if (num != null && num <= 120) onProfileChange(profile.copy(age = num)) 
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    VitalInput(
                        label = "Weight",
                        value = if (profile.weight == 0) "" else profile.weight.toString(),
                        unit = "kg",
                        onValueChange = { 
                            if (it.isEmpty()) onProfileChange(profile.copy(weight = 0))
                            else {
                                val num = it.toIntOrNull()
                                if (num != null && num <= 300) onProfileChange(profile.copy(weight = num)) 
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    VitalInput(
                        label = "Height",
                        value = if (profile.height == 0) "" else profile.height.toString(),
                        unit = "cm",
                        onValueChange = { 
                            if (it.isEmpty()) onProfileChange(profile.copy(height = 0))
                            else {
                                val num = it.toIntOrNull()
                                if (num != null && num <= 300) onProfileChange(profile.copy(height = num)) 
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Activity Level
        SectionTitle(icon = "〽️", title = "Activity Level")
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ActivityLevel.entries.forEachIndexed { index, level ->
                    ActivityLevelItem(
                        level = level,
                        selected = profile.activityLevel == level,
                        onClick = { onProfileChange(profile.copy(activityLevel = level)) }
                    )
                    if (index < ActivityLevel.entries.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Dietary Preference
        SectionTitle(icon = "🌿", title = "Dietary Preference")
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Customises your recommendations, smart swaps, and search filters.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                DietaryPreferenceItem(
                    preference = DietaryPreference.BALANCED,
                    selected = profile.dietaryPreference == DietaryPreference.BALANCED,
                    onClick = { onProfileChange(profile.copy(dietaryPreference = DietaryPreference.BALANCED)) }
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DietaryPreferenceItem(
                        preference = DietaryPreference.HIGH_PROTEIN,
                        selected = profile.dietaryPreference == DietaryPreference.HIGH_PROTEIN,
                        onClick = { onProfileChange(profile.copy(dietaryPreference = DietaryPreference.HIGH_PROTEIN)) },
                        modifier = Modifier.weight(1f)
                    )
                    DietaryPreferenceItem(
                        preference = DietaryPreference.LOW_CARB,
                        selected = profile.dietaryPreference == DietaryPreference.LOW_CARB,
                        onClick = { onProfileChange(profile.copy(dietaryPreference = DietaryPreference.LOW_CARB)) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DietaryPreferenceItem(
                        preference = DietaryPreference.VEGETARIAN,
                        selected = profile.dietaryPreference == DietaryPreference.VEGETARIAN,
                        onClick = { onProfileChange(profile.copy(dietaryPreference = DietaryPreference.VEGETARIAN)) },
                        modifier = Modifier.weight(1f)
                    )
                    DietaryPreferenceItem(
                        preference = DietaryPreference.VEGAN,
                        selected = profile.dietaryPreference == DietaryPreference.VEGAN,
                        onClick = { onProfileChange(profile.copy(dietaryPreference = DietaryPreference.VEGAN)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun SectionTitle(icon: String, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
fun LanguageToggle(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) GreenPrimary else TextSecondary
        )
    }
}

@Composable
fun SexToggleButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) GreenPrimary else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) GreenPrimary else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else TextPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalInput(label: String, value: String, unit: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = unit,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun ActivityLevelItem(level: ActivityLevel, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color(0xFFF0F5F1) else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) GreenPrimary else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = level.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (selected) GreenPrimary else TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = level.description,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun DietaryPreferenceItem(preference: DietaryPreference, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Color(0xFFF0F5F1) else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) GreenPrimary else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = preference.title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (selected) GreenPrimary else TextSecondary
        )
    }
}

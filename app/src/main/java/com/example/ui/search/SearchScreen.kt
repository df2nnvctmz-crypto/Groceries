package com.example.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FoodItem
import com.example.ui.SearchFilter
import com.example.ui.components.FoodListItem
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchFilter: SearchFilter,
    onFilterChange: (SearchFilter) -> Unit,
    searchResults: List<FoodItem>,
    loadedFoodsCount: Int,
    onFoodClick: (FoodItem) -> Unit,
    onLoadMore: () -> Unit,
    allCategories: List<String>,
    allSubcategories: List<String>,
    modifier: Modifier = Modifier
) {
    var showFilters by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Search",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = "Filter foods by keyword, sub-category, or name.",
            fontSize = 14.sp,
            color = TextSecondary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search over 20+ foods...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFEFEFEF),
                    unfocusedContainerColor = Color(0xFFEFEFEF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = TextPrimary
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(if (showFilters) GreenPrimary else Color(0xFFEFEFEF), RoundedCornerShape(16.dp))
                    .clickable { showFilters = !showFilters },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Tune, contentDescription = "Filter", tint = if (showFilters) Color.White else TextPrimary)
            }
        }
        
        AnimatedVisibility(
            visible = showFilters,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FilterPanel(
                searchFilter = searchFilter,
                onFilterChange = onFilterChange,
                allCategories = allCategories,
                allSubcategories = allSubcategories
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "QUICK FILTER",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(allCategories) { cat ->
                val isSelected = searchFilter.category == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { 
                        if (isSelected) {
                            onFilterChange(searchFilter.copy(category = "All"))
                        } else {
                            onFilterChange(searchFilter.copy(category = cat))
                        }
                    },
                    label = { Text(cat, color = if (isSelected) GreenPrimary else TextPrimary) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.White,
                        selectedContainerColor = GreenPrimary.copy(alpha = 0.1f)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) GreenPrimary else Color(0xFFE0E0E0),
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (searchQuery.isEmpty() && searchFilter.category == "All" && searchFilter.nutriScores.isEmpty() && searchFilter.maxCalories >= 1000f) "POPULAR FOODS" else "SEARCH RESULTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 1.sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        val listState = rememberLazyListState()
        
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val foodsToShow = searchResults.take(loadedFoodsCount)
            items(foodsToShow) { food ->
                FoodListItem(item = food, onClick = { onFoodClick(food) })
            }
            if (foodsToShow.size < searchResults.size) {
                item {
                    Button(
                        onClick = onLoadMore,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .height(56.dp)
                    ) {
                        Text("Load 20 more", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    searchFilter: SearchFilter,
    onFilterChange: (SearchFilter) -> Unit,
    allCategories: List<String>,
    allSubcategories: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Favorites Only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Favorites Only", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Switch(
                    checked = searchFilter.favoritesOnly,
                    onCheckedChange = { onFilterChange(searchFilter.copy(favoritesOnly = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Category Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text("CATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    var catExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                        TextField(
                            value = searchFilter.category,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                            modifier = Modifier.menuAnchor(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                            DropdownMenuItem(text = { Text("All") }, onClick = { onFilterChange(searchFilter.copy(category = "All")); catExpanded = false })
                            allCategories.forEach { cat ->
                                DropdownMenuItem(text = { Text(cat) }, onClick = { onFilterChange(searchFilter.copy(category = cat)); catExpanded = false })
                            }
                        }
                    }
                }
                
                // Subcategory Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    Text("SUBCATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    var subcatExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = subcatExpanded, onExpandedChange = { subcatExpanded = it }) {
                        TextField(
                            value = searchFilter.subcategory,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subcatExpanded) },
                            modifier = Modifier.menuAnchor(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(expanded = subcatExpanded, onDismissRequest = { subcatExpanded = false }) {
                            DropdownMenuItem(text = { Text("All") }, onClick = { onFilterChange(searchFilter.copy(subcategory = "All")); subcatExpanded = false })
                            allSubcategories.forEach { subcat ->
                                DropdownMenuItem(text = { Text(subcat.split("/").lastOrNull() ?: subcat) }, onClick = { onFilterChange(searchFilter.copy(subcategory = subcat)); subcatExpanded = false })
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("NUTRI SCORE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("A", "B", "C", "D", "E").forEach { score ->
                    val isSelected = searchFilter.nutriScores.contains(score)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) GreenPrimary else Color(0xFFF5F5F5))
                            .clickable {
                                val newScores = if (isSelected) searchFilter.nutriScores - score else searchFilter.nutriScores + score
                                onFilterChange(searchFilter.copy(nutriScores = newScores))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(score, color = if (isSelected) Color.White else TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("MAX CALORIES (/ 100g)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
                Text("${searchFilter.maxCalories.toInt()} kcal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
            }
            
            Slider(
                value = searchFilter.maxCalories,
                onValueChange = { onFilterChange(searchFilter.copy(maxCalories = it)) },
                valueRange = 0f..1000f,
                colors = SliderDefaults.colors(
                    thumbColor = GreenPrimary,
                    activeTrackColor = GreenPrimary,
                    inactiveTrackColor = Color(0xFFE0E0E0)
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("0", fontSize = 10.sp, color = TextSecondary)
                Text("500", fontSize = 10.sp, color = TextSecondary)
                Text("1000+", fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

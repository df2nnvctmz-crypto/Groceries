#!/bin/bash
sed -i 's|val all = _uiState.value.allFoods.filter { it.healthScore >= 80 }.take(10)|val all = _uiState.value.allFoods.filter { it.healthScore >= 80 }|g' app/src/main/java/com/example/ui/MainViewModel.kt
sed -i 's|return filterByDiet(all, _uiState.value.userProfile.dietaryPreference).take(5)|val dietFiltered = filterByDiet(all, _uiState.value.userProfile.dietaryPreference)\n        return getRankedFoods(dietFiltered).take(10)|g' app/src/main/java/com/example/ui/MainViewModel.kt

#!/bin/bash
sed -i 's|_uiState.update { it.copy(displayedFoods = results) }|        results = filterByDiet(results, state.userProfile.dietaryPreference)\n        if (state.searchQuery.isEmpty()) {\n            results = getRankedFoods(results)\n        }\n        _uiState.update { it.copy(displayedFoods = results) }|g' app/src/main/java/com/example/ui/MainViewModel.kt

#!/bin/bash
sed -i '/private fun applyFilters() {/i \
    private fun getRankedFoods(foods: List<FoodItem>): List<FoodItem> {\
        val bills = _uiState.value.bills\
        val boughtFoodIds = mutableMapOf<String, Int>()\
        val boughtCategories = mutableMapOf<String, Int>()\
        \
        try {\
            bills.forEach { bill ->\
                val jsonArray = org.json.JSONArray(bill.itemsJson)\
                for (i in 0 until jsonArray.length()) {\
                    val obj = jsonArray.getJSONObject(i)\
                    if (obj.has("matchedFoodId") \&\& !obj.isNull("matchedFoodId")) {\
                        val id = obj.getString("matchedFoodId")\
                        boughtFoodIds[id] = boughtFoodIds.getOrDefault(id, 0) + 1\
                    }\
                }\
            }\
        } catch (e: Exception) {\
            e.printStackTrace()\
        }\
        \
        foods.forEach {\
            if (boughtFoodIds.containsKey(it.id)) {\
                boughtCategories[it.category] = boughtCategories.getOrDefault(it.category, 0) + 1\
            }\
        }\
\
        return foods.sortedByDescending { food ->\
            var score = 0.0\
            if (boughtFoodIds.containsKey(food.id)) {\
                score += boughtFoodIds[food.id]!! * 10\
            }\
            if (boughtCategories.containsKey(food.category)) {\
                score += boughtCategories[food.category]!! * 2\
            }\
            score += (food.healthScore / 10.0)\
            score\
        }\
    }\
' app/src/main/java/com/example/ui/MainViewModel.kt

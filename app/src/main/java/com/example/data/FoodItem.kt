package com.example.data

data class FoodItem(
    val id: String = "",
    val name: String,
    val category: String,
    val subcategory: String = "",
    val kcal: Double,
    val proteinG: Double,
    val carbsG: Double,
    val sugarsG: Double,
    val fatG: Double,
    val saturatedFatG: Double,
    val fiberG: Double,
    val saltG: Double,
    val vitaminAMcg: Double = 0.0,
    val vitaminCMg: Double = 0.0,
    val calciumMg: Double = 0.0,
    val ironMg: Double = 0.0,
    val isBeverage: Boolean = category.equals("Beverages", ignoreCase = true),
    val swapSuggestionId: String? = null,
    var nutriGrade: String = "A",
    var healthScore: Int = 100,
    val micros: Map<String, Double> = emptyMap()
)

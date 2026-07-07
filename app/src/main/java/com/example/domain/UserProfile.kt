package com.example.domain

enum class BiologicalSex {
    MALE, FEMALE
}

enum class ActivityLevel(val title: String, val description: String, val multiplier: Double) {
    SEDENTARY("Sedentary", "Little to no exercise", 1.2),
    LIGHTLY_ACTIVE("Lightly Active", "Light exercise (1-3 days/week)", 1.375),
    MODERATELY_ACTIVE("Moderately Active", "Moderate exercise (3-5 days/week)", 1.55),
    VERY_ACTIVE("Very Active", "Heavy exercise (6-7 days/week)", 1.725),
    EXTRA_ACTIVE("Extra Active", "Very heavy training or physical job", 1.9)
}

enum class DietaryPreference(val title: String) {
    BALANCED("Balanced (No Filter)"),
    HIGH_PROTEIN("High Protein"),
    LOW_CARB("Low Carb"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan")
}

data class UserProfile(
    val sex: BiologicalSex = BiologicalSex.FEMALE,
    val age: Int = 23,
    val weight: Int = 50,
    val height: Int = 170,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
    val dietaryPreference: DietaryPreference = DietaryPreference.BALANCED,
    val language: String = "English"
) {
    val dailyCalorieTarget: Int
        get() {
            val bmr = if (sex == BiologicalSex.MALE) {
                10 * weight + 6.25 * height - 5 * age + 5
            } else {
                10 * weight + 6.25 * height - 5 * age - 161
            }
            return (bmr * activityLevel.multiplier).toInt()
        }
        
    val proteinTargetG: Int
        get() = (weight * 1.6).toInt() // Example: 1.6g per kg of body weight
        
    val fatTargetG: Int
        get() = (dailyCalorieTarget * 0.25 / 9).toInt() // 25% of calories from fat
        
    val carbsTargetG: Int
        get() = ((dailyCalorieTarget - (proteinTargetG * 4) - (fatTargetG * 9)) / 4).toInt()
}

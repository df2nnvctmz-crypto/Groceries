package com.example.domain

import com.example.data.FoodItem
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object NutriScoreCalculator {

    fun calculate(item: FoodItem): Pair<String, Int> {
        if (item.name.contains("water", ignoreCase = true)) {
            return "A" to 100
        }

        val energyKj = item.kcal * 4.184
        var pointsA = 0
        var pointsC = 0

        val isFatOrOil = item.name.contains("oil", ignoreCase = true) || item.name.contains("nut", ignoreCase = true)
        val isRedMeat = item.name.contains("beef", ignoreCase = true) || item.name.contains("pork", ignoreCase = true)

        if (item.isBeverage) {
            // Energy
            pointsA += min(10, (energyKj / 27.0).toInt())
            // Sugars
            pointsA += min(10, (item.sugarsG / 1.35).toInt())
            // Saturated fat
            pointsA += min(10, (item.saturatedFatG / 1.0).toInt())
            // Salt
            pointsA += min(10, (item.saltG / 0.09).toInt())
            
            // 2024 Nutri-Score Update: Beverages with artificial sweeteners are penalized with 4 points
            val hasSweetener = item.name.contains("sweetener", ignoreCase = true) || 
                               item.subcategory.contains("energy reduced", ignoreCase = true)
            if (hasSweetener) {
                pointsA += 4
            }
            
            // Protein
            pointsC += min(7, (item.proteinG / 1.6).toInt())
            // Fiber
            pointsC += min(7, (item.fiberG / 0.9).toInt())
        } else {
            // Energy
            val energyScore = if (isFatOrOil) {
                // Energy from saturated fats instead of total energy
                (item.saturatedFatG * 37 / 120.0).toInt()
            } else {
                (energyKj / 335.0).toInt()
            }
            pointsA += min(10, energyScore)
            // Sugars
            pointsA += min(10, (item.sugarsG / 4.5).toInt())
            // Saturated fat
            pointsA += min(10, (item.saturatedFatG / 1.0).toInt())
            // Salt
            pointsA += min(10, (item.saltG / 0.09).toInt())

            // Protein
            var proteinPoints = min(7, (item.proteinG / 1.6).toInt())
            if (isFatOrOil) proteinPoints = min(proteinPoints, 7)
            if (isRedMeat) proteinPoints = min(proteinPoints, 2)
            
            pointsC += proteinPoints
            // Fiber
            pointsC += min(7, (item.fiberG / 0.9).toInt())
        }

        val finalScore = pointsA - pointsC

        val grade = when {
            item.name.contains("water", ignoreCase = true) -> "A"
            item.isBeverage -> {
                when {
                    finalScore <= 1 -> "B"
                    finalScore <= 5 -> "C"
                    finalScore <= 9 -> "D"
                    else -> "E"
                }
            }
            else -> {
                when {
                    finalScore <= -1 -> "A"
                    finalScore <= 2 -> "B"
                    finalScore <= 10 -> "C"
                    finalScore <= 18 -> "D"
                    else -> "E"
                }
            }
        }

        // Map finalScore to 1-100 health_score
        // Lower finalScore is better. 
        val raw = 100 - (finalScore + 5) * 4
        val healthScore = max(1, min(100, raw))

        return grade to healthScore
    }
}

package com.example.data

import android.content.Context
import com.example.R
import com.example.domain.NutriScoreCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

class FoodRepository(private val context: Context) {

    private val _foods = mutableListOf<FoodItem>()

    // Synchronous load for initial state if needed, but prefer suspend function
    fun getAllFoods(): List<FoodItem> = _foods

    suspend fun loadFoods(): List<FoodItem> = withContext(Dispatchers.IO) {
        if (_foods.isNotEmpty()) return@withContext _foods

        try {
            val inputStream = context.resources.openRawResource(R.raw.foods)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val nutrients = obj.getJSONObject("nutrients_per_100")
                val microsObj = nutrients.getJSONObject("micros")
                val microsMap = mutableMapOf<String, Double>()
                val keys = microsObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (!microsObj.isNull(key)) {
                        microsMap[key] = microsObj.getDouble(key)
                    }
                }

                val item = FoodItem(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = obj.getString("category"),
                    subcategory = obj.optString("swiss_category", ""),
                    kcal = nutrients.optDouble("kcal", 0.0),
                    proteinG = nutrients.optDouble("protein_g", 0.0),
                    carbsG = nutrients.optDouble("carbs_g", 0.0),
                    sugarsG = nutrients.optDouble("sugars_g", 0.0),
                    fatG = nutrients.optDouble("fat_g", 0.0),
                    saturatedFatG = nutrients.optDouble("saturated_fat_g", 0.0),
                    fiberG = nutrients.optDouble("fiber_g", 0.0),
                    saltG = nutrients.optDouble("salt_g", 0.0),
                    vitaminAMcg = if (microsObj.isNull("vitamin_a_ug")) 0.0 else microsObj.getDouble("vitamin_a_ug"),
                    vitaminCMg = if (microsObj.isNull("vitamin_c_mg")) 0.0 else microsObj.getDouble("vitamin_c_mg"),
                    calciumMg = if (microsObj.isNull("calcium_mg")) 0.0 else microsObj.getDouble("calcium_mg"),
                    ironMg = if (microsObj.isNull("iron_mg")) 0.0 else microsObj.getDouble("iron_mg"),
                    swapSuggestionId = if (obj.isNull("swap_suggestion_id")) null else obj.getString("swap_suggestion_id"),
                    micros = microsMap
                )
                
                // Recalculate score to ensure it aligns with 2024 calculation
                val (grade, score) = NutriScoreCalculator.calculate(item)
                item.nutriGrade = grade
                item.healthScore = score
                
                _foods.add(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext _foods
    }

    fun searchFoods(query: String): List<FoodItem> {
        if (query.isBlank()) return _foods
        val lowerQuery = query.trim().lowercase()
        return _foods.filter { it.name.contains(lowerQuery, ignoreCase = true) }
            .sortedWith(compareBy<FoodItem> {
                val lowerName = it.name.lowercase()
                when {
                    lowerName == lowerQuery -> 0
                    lowerName.startsWith("$lowerQuery ") || lowerName.startsWith("$lowerQuery,") -> 1
                    lowerName.startsWith(lowerQuery) -> 2
                    lowerName.contains(" $lowerQuery") -> 3
                    else -> 4
                }
            }.thenBy { it.name.length }
            .thenBy { it.name })
    }
    
    fun getRecommendedSwaps(food: FoodItem, userPreference: String? = null): List<FoodItem> {
        if (food.healthScore > 80) return emptyList()
        
        return _foods.filter { 
            it.id != food.id &&
            it.healthScore >= food.healthScore + 20 &&
            (it.category.equals(food.category, ignoreCase = true) || 
             it.subcategory.equals(food.subcategory, ignoreCase = true))
        }.map { candidate ->
            candidate to evaluateSwap(food, candidate, userPreference)
        }.sortedByDescending { it.second }
         .map { it.first }
    }

    private fun evaluateSwap(original: FoodItem, candidate: FoodItem, userPreference: String?): Double {
        var points = 0.0
        
        // Add points based on score difference: sqrt(candidate_score - original_score) * 2 (cap candidate score at 90)
        val cappedCandidateScore = candidate.healthScore.coerceAtMost(90)
        val scoreDiff = cappedCandidateScore - original.healthScore
        if (scoreDiff > 0) {
            points += kotlin.math.sqrt(scoreDiff.toDouble()) * 2.0
        }
        
        // Add +10 points if category matches.
        if (original.category.equals(candidate.category, ignoreCase = true)) {
            points += 10.0
        }
        
        // Add +20 points if subCategory matches.
        // Add +20 points if swiss_category matches.
        // Since subcategory in our model maps to swiss_category, I will add 40 points if it matches and is not blank.
        if (original.subcategory.isNotBlank() && original.subcategory.equals(candidate.subcategory, ignoreCase = true)) {
            points += 40.0
        }
        
        // String matching: Split both product names into lowercase words (ignore special chars). 
        // Add +15 points for every word > 2 characters that matches between the original and candidate food names.
        val originalWords = original.name.lowercase().replace(Regex("[^a-z0-9\\s]"), "").split("\\s+".toRegex()).filter { it.length > 2 }.toSet()
        val candidateWords = candidate.name.lowercase().replace(Regex("[^a-z0-9\\s]"), "").split("\\s+".toRegex()).filter { it.length > 2 }.toSet()
        val commonWords = originalWords.intersect(candidateWords)
        points += commonWords.size * 15.0
        
        // Calorie penalty: If kcal per 100g > 400, deduct ((kcal - 400) / 100) * 15 points.
        if (candidate.kcal > 400.0) {
            points -= ((candidate.kcal - 400.0) / 100.0) * 15.0
        }
        
        // User Preferences: If preference is "High Protein", add +5 points if candidate has more protein. 
        // If "Low Carb", add +5 points if candidate has fewer carbs.
        if (userPreference != null) {
            if (userPreference.contains("High Protein", ignoreCase = true) && candidate.proteinG > original.proteinG) {
                points += 5.0
            }
            if (userPreference.contains("Low Carb", ignoreCase = true) && candidate.carbsG < original.carbsG) {
                points += 5.0
            }
        }
        
        return points
    }
}

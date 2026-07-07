package com.example.domain

import android.content.Context
import android.net.Uri
import com.example.data.FoodItem
import com.example.data.FoodRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.math.min

data class ScannedItem(
    val originalText: String,
    var matchedFood: FoodItem?
)

class ReceiptScanner(private val repository: FoodRepository) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun scanReceipt(context: Context, imageUri: Uri): List<ScannedItem> {
        val image = InputImage.fromFilePath(context, imageUri)
        val result = suspendCancellableCoroutine { continuation ->
            recognizer.process(image)
                .addOnSuccessListener { text ->
                    continuation.resume(text)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

        val allFoods = repository.getAllFoods()
        
        val scannedItems = mutableListOf<ScannedItem>()
        for (block in result.textBlocks) {
            for (line in block.lines) {
                val text = line.text
                // basic filter to ignore prices, dates, etc
                if (text.length > 2 && !text.matches(Regex(".*\\d{2}:\\d{2}.*")) && !text.matches(Regex(".*[0-9]+,[0-9]+.*")) && !text.matches(Regex("^[0-9.,\\s]+$"))) {
                    val matchedFood = matchFood(text, allFoods)
                    if (matchedFood != null) {
                        scannedItems.add(ScannedItem(text, matchedFood))
                    } else if (text.length > 4) {
                        scannedItems.add(ScannedItem(text, null))
                    }
                }
            }
        }
        return scannedItems
    }

    private fun matchFood(ocrText: String, foods: List<FoodItem>): FoodItem? {
        val sanitizedOcr = sanitizeAndExpand(ocrText)
        val ocrWords = sanitizedOcr.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        
        if (ocrWords.isEmpty()) return null

        var bestMatch: FoodItem? = null
        var highestScore = 0.0

        for (food in foods) {
            val sanitizedFoodName = sanitizeAndExpand(food.name)
            val foodWords = sanitizedFoodName.split("\\s+".toRegex()).filter { it.isNotEmpty() }
            
            var score = 0.0
            
            // Keyword matching
            var matchCount = 0
            for (word in ocrWords) {
                if (foodWords.contains(word)) {
                    matchCount++
                } else {
                    // Check for partial/fuzzy match
                    for (fWord in foodWords) {
                        if (fWord.length > 3 && word.length > 3) {
                            if (fWord.contains(word) || word.contains(fWord)) {
                                matchCount++
                                break
                            }
                            val dist = levenshtein(word, fWord)
                            if (dist <= 1) { // 1 typo allowed for words > 3 chars
                                matchCount++
                                break
                            }
                        }
                    }
                }
            }
            
            if (matchCount > 0) {
                 score += matchCount * 10.0
                 // Bonus if exact name match
                 if (sanitizedOcr.contains(sanitizedFoodName) || sanitizedFoodName.contains(sanitizedOcr)) {
                     score += 20.0
                 }
                 
                 // Penalty for extra words in food name (we want the most generic match if possible, or exact)
                 score -= (foodWords.size - matchCount) * 1.0
            }

            if (score > highestScore && score > 5.0) { // Threshold
                highestScore = score
                bestMatch = food
            }
        }
        
        return bestMatch
    }

    private val abbreviations = mapOf(
        "tom" to "tomato",
        "choc" to "chocolate",
        "veg" to "vegetable",
        "swts" to "sweets",
        "drnk" to "drink",
        "pot" to "potato",
        "chk" to "chicken",
        "org" to "organic",
        "pnt" to "peanut",
        "btr" to "butter",
        "strw" to "strawberry",
        "mlk" to "milk",
        "chz" to "cheese"
    )

    private fun sanitizeAndExpand(text: String): String {
        var clean = text.lowercase().replace(Regex("[^a-z\\s]"), " ").trim()
        val words = clean.split("\\s+".toRegex())
        val expanded = words.map { abbreviations[it] ?: it }
        return expanded.joinToString(" ")
    }

    private fun levenshtein(lhs: CharSequence, rhs: CharSequence): Int {
        if (lhs == rhs) { return 0 }
        if (lhs.isEmpty()) { return rhs.length }
        if (rhs.isEmpty()) { return lhs.length }

        val lhsLength = lhs.length + 1
        val rhsLength = rhs.length + 1

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1 until rhsLength) {
            newCost[0] = i
            for (j in 1 until lhsLength) {
                val match = if (lhs[j - 1] == rhs[i - 1]) 0 else 1
                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1
                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }
            val swap = cost
            cost = newCost
            newCost = swap
        }
        return cost[lhsLength - 1]
    }
}

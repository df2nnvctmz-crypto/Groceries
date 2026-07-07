import kotlin.math.max
import kotlin.math.min

class FoodItem(
    val name: String,
    val category: String,
    val kcal: Double,
    val proteinG: Double,
    val carbsG: Double,
    val sugarsG: Double,
    val fatG: Double,
    val saturatedFatG: Double,
    val fiberG: Double,
    val saltG: Double,
    val isBeverage: Boolean = category.equals("Beverages", ignoreCase = true)
)

fun main() {
    val item = FoodItem(
        name = "Cola beverage, sweetened",
        category = "Beverages",
        kcal = 40.0,
        proteinG = 0.0,
        carbsG = 10.0,
        sugarsG = 10.0,
        fatG = 0.0,
        saturatedFatG = 0.0,
        fiberG = 0.0,
        saltG = 0.02
    )
    val energyKj = item.kcal * 4.184
    var pointsA = 0
    var pointsC = 0

    pointsA += min(10, (energyKj / 27.0).toInt())
    pointsA += min(10, (item.sugarsG / 1.35).toInt())
    pointsA += min(10, (item.saturatedFatG / 1.0).toInt())
    pointsA += min(10, (item.saltG / 0.09).toInt())

    val finalScore = pointsA - pointsC
    val grade = when {
        finalScore <= 1 -> "B"
        finalScore <= 5 -> "C"
        finalScore <= 9 -> "D"
        else -> "E"
    }
    println("Score: \$finalScore, Grade: \$grade")
}

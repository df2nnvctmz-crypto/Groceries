package com.example.ui

import android.content.Context

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FoodItem
import com.example.data.FoodRepository
import com.example.data.AppDatabase
import com.example.data.BillEntity
import com.example.data.ScannedItemData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.collectLatest
import com.example.domain.DietaryPreference
import com.example.domain.ReceiptScanner
import com.example.domain.ScannedItem
import com.example.domain.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository(application)
    private val scanner = ReceiptScanner(repository)
    private val db = AppDatabase.getDatabase(application)
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val prefs = application.getSharedPreferences("food_favorites", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val savedFoodIds = prefs.getStringSet("favorite_food_ids", emptySet()) ?: emptySet()
        val savedSwapIds = prefs.getStringSet("favorite_swap_ids", emptySet()) ?: emptySet()
        _uiState.update { it.copy(favoriteFoodIds = savedFoodIds, favoriteSwapIds = savedSwapIds) }

        viewModelScope.launch {
            val foods = repository.loadFoods()
            _uiState.update { it.copy(allFoods = foods, displayedFoods = foods) }
            launch {
                db.billDao().getAllBills().collectLatest { bills ->
                    _uiState.update { state -> state.copy(bills = bills) }
                }
            }
        }
    }

    fun search(query: String) {
        _uiState.update { it.copy(searchQuery = query, loadedFoodsCount = 20) }
        applyFilters()
    }

    fun scanReceipt(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanningReceipt = true) }
            try {
                val results = scanner.scanReceipt(getApplication(), uri)
                _uiState.update { it.copy(isScanningReceipt = false, scannedItems = results) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isScanningReceipt = false, scannedItems = emptyList()) }
                e.printStackTrace()
            }
        }
    }

    fun updateScannedItem(index: Int, newFood: FoodItem?) {
        val currentItems = _uiState.value.scannedItems.toMutableList()
        if (index in currentItems.indices) {
            val item = currentItems[index]
            currentItems[index] = item.copy(matchedFood = newFood)
            _uiState.update { it.copy(scannedItems = currentItems) }
        }
    }

    fun removeScannedItem(index: Int) {
        val currentItems = _uiState.value.scannedItems.toMutableList()
        if (index in currentItems.indices) {
            currentItems.removeAt(index)
            _uiState.update { it.copy(scannedItems = currentItems) }
        }
    }

    fun saveScannedBill() {
        viewModelScope.launch {
            val items = _uiState.value.scannedItems
            if (items.isEmpty()) return@launch
            
            val itemsData = items.map { ScannedItemData(it.originalText, it.matchedFood?.id) }
            val type = Types.newParameterizedType(List::class.java, ScannedItemData::class.java)
            val adapter = moshi.adapter<List<ScannedItemData>>(type)
            val json = adapter.toJson(itemsData)
            
            var totalScore = 0
            var matchedCount = 0
            items.forEach {
                if (it.matchedFood != null) {
                    totalScore += it.matchedFood!!.healthScore
                    matchedCount++
                }
            }
            val avgScore = if (matchedCount > 0) totalScore / matchedCount else 0
            
            val bill = BillEntity(
                date = System.currentTimeMillis(),
                storeName = "Local Store",
                totalItems = items.size,
                score = avgScore,
                itemsJson = json
            )
            db.billDao().insertBill(bill)
        }
    }

    fun deleteBill(bill: BillEntity) {
        viewModelScope.launch {
            db.billDao().deleteBill(bill)
        }
    }

    fun generateDemoReceipt() {
        _uiState.update { it.copy(isScanningReceipt = true) }
        viewModelScope.launch {
            val allFoods = _uiState.value.allFoods
            if (allFoods.isNotEmpty()) {
                val itemCount = (5..15).random()
                val randomFoods = allFoods.shuffled().take(itemCount)
                val scannedItems = randomFoods.map { ScannedItem(it.name, it) }
                _uiState.update { it.copy(isScanningReceipt = false, scannedItems = scannedItems) }
            } else {
                _uiState.update { it.copy(isScanningReceipt = false) }
            }
        }
    }

    fun clearScannedItems() {
        _uiState.update { it.copy(scannedItems = emptyList()) }
    }

    fun updateSearchFilter(filter: SearchFilter) {
        _uiState.update { it.copy(searchFilter = filter, loadedFoodsCount = 20) }
        applyFilters()
    }
    
    fun toggleFavorite(foodId: String) {
        _uiState.update { state ->
            val newFavorites = if (state.favoriteFoodIds.contains(foodId)) {
                state.favoriteFoodIds - foodId
            } else {
                state.favoriteFoodIds + foodId
            }
            prefs.edit().putStringSet("favorite_food_ids", newFavorites).apply()
            state.copy(favoriteFoodIds = newFavorites)
        }
        applyFilters() // In case favoritesOnly is true
    }

    fun toggleSwapFavorite(fromId: String, toId: String) {
        val swapId = "$fromId::$toId"
        _uiState.update { state ->
            val newFavorites = if (state.favoriteSwapIds.contains(swapId)) {
                state.favoriteSwapIds - swapId
            } else {
                state.favoriteSwapIds + swapId
            }
            prefs.edit().putStringSet("favorite_swap_ids", newFavorites).apply()
            state.copy(favoriteSwapIds = newFavorites)
        }
    }

    private fun getRankedFoods(foods: List<FoodItem>): List<FoodItem> {
        val bills = _uiState.value.bills
        val boughtFoodIds = mutableMapOf<String, Int>()
        val boughtCategories = mutableMapOf<String, Int>()
        
        try {
            bills.forEach { bill ->
                val jsonArray = org.json.JSONArray(bill.itemsJson)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    if (obj.has("matchedFoodId") && !obj.isNull("matchedFoodId")) {
                        val id = obj.getString("matchedFoodId")
                        boughtFoodIds[id] = boughtFoodIds.getOrDefault(id, 0) + 1
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        foods.forEach {
            if (boughtFoodIds.containsKey(it.id)) {
                boughtCategories[it.category] = boughtCategories.getOrDefault(it.category, 0) + 1
            }
        }

        return foods.sortedByDescending { food ->
            var score = 0.0
            if (boughtFoodIds.containsKey(food.id)) {
                score += boughtFoodIds[food.id]!! * 10
            }
            if (boughtCategories.containsKey(food.category)) {
                score += boughtCategories[food.category]!! * 2
            }
            score += (food.healthScore / 10.0)
            score
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        var results = repository.searchFoods(state.searchQuery)
        
        val filter = state.searchFilter
        
        results = results.filter { food ->
            val matchesFavorite = if (filter.favoritesOnly) state.favoriteFoodIds.contains(food.id) else true
            val matchesCategory = if (filter.category != "All") food.category.equals(filter.category, ignoreCase = true) else true
            val matchesSubcategory = if (filter.subcategory != "All") food.subcategory.equals(filter.subcategory, ignoreCase = true) else true
            val matchesNutriScore = if (filter.nutriScores.isNotEmpty()) filter.nutriScores.contains(food.nutriGrade) else true
            val matchesCalories = food.kcal <= filter.maxCalories
            
            matchesFavorite && matchesCategory && matchesSubcategory && matchesNutriScore && matchesCalories
        }
        
                results = filterByDiet(results, state.userProfile.dietaryPreference)
        if (state.searchQuery.isEmpty()) {
            results = getRankedFoods(results)
        }
        _uiState.update { it.copy(displayedFoods = results) }
    }

    fun selectFood(food: FoodItem?) {
        _uiState.update { it.copy(selectedFood = food) }
    }
    
    fun getSwapsFor(food: FoodItem): List<FoodItem> {
        val userPref = _uiState.value.userProfile.dietaryPreference.title
        val allSwaps = repository.getRecommendedSwaps(food, userPref)
        return filterByDiet(allSwaps, _uiState.value.userProfile.dietaryPreference)
    }

    fun getRecommendedFoods(): List<FoodItem> {
        val all = _uiState.value.allFoods.filter { it.healthScore >= 80 }
        val dietFiltered = filterByDiet(all, _uiState.value.userProfile.dietaryPreference)
        return getRankedFoods(dietFiltered).take(10)
    }

    fun updateUserProfile(newProfile: UserProfile) {
        _uiState.update { it.copy(userProfile = newProfile) }
        search(_uiState.value.searchQuery) // Refresh search results with new filter
    }

    fun loadMoreFoods() {
        _uiState.update { it.copy(loadedFoodsCount = it.loadedFoodsCount + 20) }
    }

    private fun filterByDiet(foods: List<FoodItem>, preference: DietaryPreference): List<FoodItem> {
        return foods.filter { food ->
            when (preference) {
                DietaryPreference.BALANCED -> true
                DietaryPreference.HIGH_PROTEIN -> food.proteinG >= 10.0 // Arbitrary threshold for demo
                DietaryPreference.LOW_CARB -> food.carbsG <= 15.0
                DietaryPreference.VEGETARIAN -> !food.name.contains("beef", true) && 
                                               !food.name.contains("pork", true) && 
                                               !food.name.contains("chicken", true) &&
                                               !food.name.contains("fish", true) &&
                                               !food.name.contains("meat", true)
                DietaryPreference.VEGAN -> !food.name.contains("beef", true) && 
                                          !food.name.contains("pork", true) && 
                                          !food.name.contains("chicken", true) &&
                                          !food.name.contains("fish", true) &&
                                          !food.name.contains("meat", true) &&
                                          !food.category.equals("Dairy", true) &&
                                          !food.name.contains("egg", true) &&
                                          !food.name.contains("honey", true) &&
                                          !food.name.contains("milk", true) &&
                                          !food.name.contains("cheese", true)
            }
        }
    }
}

data class SearchFilter(
    val favoritesOnly: Boolean = false,
    val category: String = "All",
    val subcategory: String = "All",
    val nutriScores: Set<String> = emptySet(),
    val maxCalories: Float = 1000f
)

data class UiState(
    val allFoods: List<FoodItem> = emptyList(),
    val displayedFoods: List<FoodItem> = emptyList(),
    val searchQuery: String = "",
    val searchFilter: SearchFilter = SearchFilter(),
    val selectedFood: FoodItem? = null,
    val userProfile: UserProfile = UserProfile(),
    val loadedFoodsCount: Int = 20,
    val favoriteFoodIds: Set<String> = emptySet(),
    val favoriteSwapIds: Set<String> = emptySet(),
    val isScanningReceipt: Boolean = false,
    val scannedItems: List<ScannedItem> = emptyList(),
    val bills: List<com.example.data.BillEntity> = emptyList()
)

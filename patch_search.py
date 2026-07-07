import re

with open("app/src/main/java/com/example/ui/search/SearchScreen.kt", "r") as f:
    content = f.read()

# Add imports
content = content.replace("import androidx.compose.foundation.lazy.items", "import androidx.compose.foundation.lazy.items\nimport androidx.compose.foundation.lazy.rememberLazyListState\nimport androidx.compose.runtime.LaunchedEffect")

# Update signature
old_sig = """fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<FoodItem>,
    onFoodClick: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {"""

new_sig = """fun SearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<FoodItem>,
    loadedFoodsCount: Int,
    onFoodClick: (FoodItem) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {"""
content = content.replace(old_sig, new_sig)

# Add GreenPrimary import
content = content.replace("import com.example.ui.theme.BackgroundLight", "import com.example.ui.theme.BackgroundLight\nimport com.example.ui.theme.GreenPrimary")

# Update LazyColumn
old_lazy = """        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(searchResults) { food ->
                FoodListItem(item = food, onClick = { onFoodClick(food) })
            }
        }"""

new_lazy = """        val listState = rememberLazyListState()
        
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val foodsToShow = searchResults.take(loadedFoodsCount)
            items(foodsToShow) { food ->
                FoodListItem(item = food, onClick = { onFoodClick(food) })
            }
            if (foodsToShow.size < searchResults.size) {
                item {
                    LaunchedEffect(Unit) {
                        onLoadMore()
                    }
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GreenPrimary)
                    }
                }
            }
        }"""
content = content.replace(old_lazy, new_lazy)

with open("app/src/main/java/com/example/ui/search/SearchScreen.kt", "w") as f:
    f.write(content)


#!/bin/bash
sed -i 's|import com.example.data.FoodRepository|import com.example.data.FoodRepository\nimport com.example.data.AppDatabase\nimport com.example.data.BillEntity\nimport com.example.data.ScannedItemData\nimport com.squareup.moshi.Moshi\nimport com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory\nimport com.squareup.moshi.Types\nimport kotlinx.coroutines.flow.collectLatest|g' app/src/main/java/com/example/ui/MainViewModel.kt

sed -i 's|private val scanner = ReceiptScanner(repository)|private val scanner = ReceiptScanner(repository)\n    private val db = AppDatabase.getDatabase(application)\n    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()|g' app/src/main/java/com/example/ui/MainViewModel.kt

sed -i '/_uiState.update { it.copy(allFoods = foods, displayedFoods = foods) }/a \
            launch {\
                db.billDao().getAllBills().collectLatest { bills ->\
                    _uiState.update { state -> state.copy(bills = bills) }\
                }\
            }' app/src/main/java/com/example/ui/MainViewModel.kt

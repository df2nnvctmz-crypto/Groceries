#!/bin/bash
sed -i 's|import com.example.ui.components.NutriScoreBadge|import com.example.ui.components.NutriScoreBadge\nimport com.example.data.FoodItem\nimport androidx.compose.runtime.mutableStateOf\nimport androidx.compose.runtime.remember\nimport androidx.compose.runtime.getValue\nimport androidx.compose.runtime.setValue|g' app/src/main/java/com/example/ui/bills/ScanResultScreen.kt

sed -i 's|scannedItems: List<ScannedItem>,|scannedItems: List<ScannedItem>,\n    allFoods: List<FoodItem>,\n    onUpdateItem: (Int, FoodItem?) -> Unit,|g' app/src/main/java/com/example/ui/bills/ScanResultScreen.kt

sed -i '/modifier: Modifier = Modifier/a \
    var editingIndex by remember { mutableStateOf<Int?>(null) }\
\
    if (editingIndex != null) {\
        EditScannedItemDialog(\
            allFoods = allFoods,\
            onDismiss = { editingIndex = null },\
            onFoodSelected = { food -> \
                onUpdateItem(editingIndex!!, food)\
                editingIndex = null\
            }\
        )\
    }' app/src/main/java/com/example/ui/bills/ScanResultScreen.kt

sed -i 's|IconButton(onClick = { onEditItem(index) }, modifier = Modifier.size(32.dp)) {|IconButton(onClick = { editingIndex = index }, modifier = Modifier.size(32.dp)) {|g' app/src/main/java/com/example/ui/bills/ScanResultScreen.kt


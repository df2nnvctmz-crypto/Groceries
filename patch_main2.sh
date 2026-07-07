#!/bin/bash
sed -i 's|scannedItems = uiState.scannedItems,|scannedItems = uiState.scannedItems,\n                    allFoods = uiState.allFoods,\n                    onUpdateItem = { index, food -> viewModel.updateScannedItem(index, food) },|g' app/src/main/java/com/example/MainActivity.kt

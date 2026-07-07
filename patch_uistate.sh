#!/bin/bash
sed -i 's|val scannedItems: List<ScannedItem> = emptyList()|val scannedItems: List<ScannedItem> = emptyList(),\n    val bills: List<com.example.data.BillEntity> = emptyList()|g' app/src/main/java/com/example/ui/MainViewModel.kt

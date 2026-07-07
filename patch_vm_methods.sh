#!/bin/bash
sed -i '/fun clearScannedItems()/i \
    fun saveScannedBill() {\
        viewModelScope.launch {\
            val items = _uiState.value.scannedItems\
            if (items.isEmpty()) return@launch\
            \
            val itemsData = items.map { ScannedItemData(it.originalText, it.matchedFood?.id) }\
            val type = Types.newParameterizedType(List::class.java, ScannedItemData::class.java)\
            val adapter = moshi.adapter<List<ScannedItemData>>(type)\
            val json = adapter.toJson(itemsData)\
            \
            var totalScore = 0\
            var matchedCount = 0\
            items.forEach {\
                if (it.matchedFood != null) {\
                    totalScore += it.matchedFood!!.healthScore\
                    matchedCount++\
                }\
            }\
            val avgScore = if (matchedCount > 0) totalScore / matchedCount else 0\
            \
            val bill = BillEntity(\
                date = System.currentTimeMillis(),\
                storeName = "Local Store",\
                totalItems = items.size,\
                score = avgScore,\
                itemsJson = json\
            )\
            db.billDao().insertBill(bill)\
        }\
    }\
\
    fun deleteBill(bill: BillEntity) {\
        viewModelScope.launch {\
            db.billDao().deleteBill(bill)\
        }\
    }\
\
    fun generateDemoReceipt() {\
        _uiState.update { it.copy(isScanningReceipt = true) }\
        viewModelScope.launch {\
            val allFoods = _uiState.value.allFoods\
            if (allFoods.isNotEmpty()) {\
                val itemCount = (5..15).random()\
                val randomFoods = allFoods.shuffled().take(itemCount)\
                val scannedItems = randomFoods.map { ScannedItem(it.name, it) }\
                _uiState.update { it.copy(isScanningReceipt = false, scannedItems = scannedItems) }\
            } else {\
                _uiState.update { it.copy(isScanningReceipt = false) }\
            }\
        }\
    }\
' app/src/main/java/com/example/ui/MainViewModel.kt

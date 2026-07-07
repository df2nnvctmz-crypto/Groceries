#!/bin/bash
sed -i 's|ScanReceiptScreen(|ScanReceiptScreen(\n                    onDemoReceiptClick = { viewModel.generateDemoReceipt(); navController.navigate("scan_result") },|g' app/src/main/java/com/example/MainActivity.kt

sed -i 's|BillsScreen(onScanClick = { navController.navigate("scan") })|BillsScreen(\n                    bills = uiState.bills,\n                    allFoods = uiState.allFoods,\n                    onScanClick = { navController.navigate("scan") },\n                    onDeleteBill = { viewModel.deleteBill(it) },\n                    onFoodClick = { food ->\n                        viewModel.selectFood(food)\n                        navController.navigate("food_detail")\n                    }\n                )|g' app/src/main/java/com/example/MainActivity.kt

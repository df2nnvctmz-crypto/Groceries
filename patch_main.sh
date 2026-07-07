#!/bin/bash
sed -i 's|import com.example.ui.bills.BillsScreen|import com.example.ui.bills.BillsScreen\nimport com.example.ui.bills.ScanReceiptScreen\nimport com.example.ui.bills.ScanResultScreen|g' app/src/main/java/com/example/MainActivity.kt
sed -i 's|BillsScreen()|BillsScreen(\n                    onScanClick = { navController.navigate("scan_receipt") }\n                )|g' app/src/main/java/com/example/MainActivity.kt
sed -i '/composable(Screen.Profile.route)/i \
            composable("scan_receipt") {\
                ScanReceiptScreen(\
                    onNavigateBack = { navController.popBackStack() },\
                    onImageSelected = { uri ->\
                        viewModel.scanReceipt(uri)\
                        navController.navigate("scan_result")\
                    }\
                )\
            }\
            composable("scan_result") {\
                ScanResultScreen(\
                    scannedItems = uiState.scannedItems,\
                    isScanning = uiState.isScanningReceipt,\
                    onNavigateBack = { navController.popBackStack() },\
                    onRemoveItem = { viewModel.removeScannedItem(it) },\
                    onEditItem = { /* Handle edit */ },\
                    onSaveBill = {\
                        navController.popBackStack(Screen.Bills.route, inclusive = false)\
                        viewModel.clearScannedItems()\
                    }\
                )\
            }' app/src/main/java/com/example/MainActivity.kt

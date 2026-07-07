#!/bin/bash
sed -i 's|BillsScreen(|BillsScreen(\n                    bills = uiState.bills,\n                    allFoods = uiState.allFoods,\n                    onDeleteBill = { viewModel.deleteBill(it) },\n                    onFoodClick = { food ->\n                        viewModel.selectFood(food)\n                    },|g' app/src/main/java/com/example/MainActivity.kt

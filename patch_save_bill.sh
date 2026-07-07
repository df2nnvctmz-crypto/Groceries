#!/bin/bash
sed -i 's|onSaveBill = {|onSaveBill = {\n                        viewModel.saveScannedBill()|g' app/src/main/java/com/example/MainActivity.kt

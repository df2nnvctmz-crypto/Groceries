#!/bin/bash
sed -i 's|fun ScanReceiptScreen(|fun ScanReceiptScreen(\n    onDemoReceiptClick: () -> Unit,|g' app/src/main/java/com/example/ui/bills/ScanReceiptScreen.kt

sed -i 's|onClick = { /\* Demo not implemented \*/ }|onClick = onDemoReceiptClick|g' app/src/main/java/com/example/ui/bills/ScanReceiptScreen.kt

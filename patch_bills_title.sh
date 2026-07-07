#!/bin/bash
sed -i 's|text = "RECENT BILLS",|text = "THIS WEEK",|g' app/src/main/java/com/example/ui/bills/BillsScreen.kt
sed -i 's|color = TextSecondary,|color = GreenPrimary,|g' app/src/main/java/com/example/ui/bills/BillsScreen.kt

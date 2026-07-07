package com.example.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun NutritionFactRow(
    label: String,
    value: Double,
    unit: String = "g",
    dailyValue: Double? = null,
    isSubItem: Boolean = false,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .padding(start = if (isSubItem) 16.dp else 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = if (isSubItem) 14.sp else 16.sp,
                color = if (isSubItem) TextSecondary else TextPrimary,
                fontWeight = if (isSubItem) FontWeight.Normal else FontWeight.Medium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format(Locale.US, "%.1f%s", value, unit),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (dailyValue != null && dailyValue > 0) {
                    Spacer(modifier = Modifier.width(16.dp))
                    val percentage = ((value / dailyValue) * 100).toInt()
                    Text(
                        text = "$percentage%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        modifier = Modifier.width(40.dp)
                    )
                } else if (dailyValue != null) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "N/A",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }
        }
        
        // Progress bar indicator below text
        if (dailyValue != null && dailyValue > 0) {
            val fraction = (value / dailyValue).coerceIn(0.0, 1.0).toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f) // Match the design's partial line length
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFEFEFEF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GreenPrimary)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (showDivider) {
            HorizontalDivider(color = Color(0xFFEFEFEF), thickness = 1.dp)
        }
    }
}

@Composable
fun MicronutrientFactRow(
    label: String,
    amountStr: String,
    percentage: Int?,
    showDivider: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = amountStr,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(16.dp))
                if (percentage != null) {
                    Text(
                        text = "$percentage%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        modifier = Modifier.width(40.dp)
                    )
                } else {
                    Text(
                        text = "N/A",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        modifier = Modifier.width(40.dp)
                    )
                }
            }
        }
        
        if (percentage != null && percentage > 0) {
            val fraction = (percentage / 100f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFEFEFEF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GreenPrimary)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (showDivider) {
            HorizontalDivider(color = Color(0xFFEFEFEF), thickness = 1.dp)
        }
    }
}

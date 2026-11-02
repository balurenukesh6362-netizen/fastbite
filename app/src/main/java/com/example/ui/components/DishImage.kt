package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.model.MealCategory

@Composable
fun DishImage(
    imageResName: String,
    category: MealCategory,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(imageResName, "drawable", context.packageName)
    val fallbackId = when (category) {
        MealCategory.BREAKFAST -> context.resources.getIdentifier("dish_breakfast", "drawable", context.packageName)
        MealCategory.LUNCH -> context.resources.getIdentifier("dish_lunch", "drawable", context.packageName)
        MealCategory.SNACKS -> context.resources.getIdentifier("dish_snack", "drawable", context.packageName)
    }

    val targetRes = if (resId != 0) resId else fallbackId

    if (targetRes != 0) {
        Image(
            painter = painterResource(id = targetRes),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

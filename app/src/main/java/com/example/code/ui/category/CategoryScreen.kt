package com.example.code.ui.category

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.code.ui.AnimalViewModel
import com.example.code.ui.common.AnimalCard
import com.example.code.ui.common.CategoryChip

@Composable
fun CategoryScreen(
    viewModel: AnimalViewModel,
    onAnimalClick: (Int) -> Unit
) {
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val currentCategory = categories.getOrNull(selectedTab)
    val animals by (
        if (currentCategory != null) viewModel.animalsByCategory(currentCategory)
        else viewModel.allAnimals
    ).collectAsState(initial = emptyList())

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category chips row
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories.size) { index ->
                        CategoryChip(
                            label = categories[index],
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
            }

            // Result count
            item {
                Text(
                    text = "${animals.size} 种动物",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            // Animal list
            items(animals, key = { it.id }) { animal ->
                AnimalCard(
                    animal = animal,
                    onClick = { onAnimalClick(animal.id) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

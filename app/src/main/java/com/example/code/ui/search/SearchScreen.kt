package com.example.code.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.code.data.local.entity.AnimalEntity
import com.example.code.ui.AnimalViewModel
import com.example.code.ui.common.AnimalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: AnimalViewModel,
    onAnimalClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("search_history", 0) }
    val focusRequester = remember { FocusRequester() }

    var query by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<AnimalEntity>() }
    val history = remember { mutableStateListOf<String>().apply {
        val saved = prefs.getString("history", "") ?: ""
        if (saved.isNotEmpty()) addAll(saved.split(","))
    } }

    val hotSearches = listOf("东北虎", "大熊猫", "丹顶鹤", "金丝猴", "中华鲟", "朱鹮", "藏羚羊", "扬子鳄")

    // 实时搜索
    LaunchedEffect(query) {
        if (query.isBlank()) {
            searchResults.clear()
        } else {
            viewModel.searchAnimals(query).collect { results ->
                searchResults.clear()
                searchResults.addAll(results)
            }
        }
    }

    fun doSearch(keyword: String) {
        if (keyword.isBlank()) return
        query = keyword
        history.remove(keyword)
        history.add(0, keyword)
        if (history.size > 20) history.removeAt(history.size - 1)
        prefs.edit().putString("history", history.joinToString(",")).apply()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = { Text("搜索动物名称（中/英文）") },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        leadingIcon = {
                            Icon(Icons.Filled.Search, contentDescription = "搜索")
                        },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "清除")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                            focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                            cursorColor = MaterialTheme.colorScheme.onPrimary,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            focusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                            focusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        if (query.isBlank()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                if (history.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "搜索历史",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = {
                                history.clear()
                                prefs.edit().remove("history").apply()
                            }) {
                                Text("清空", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    item {
                        ChipFlow(items = history, onClick = { doSearch(it) })
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                item {
                    Text(
                        "热门搜索",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    ChipFlow(items = hotSearches, onClick = { doSearch(it) })
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (searchResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "未找到相关动物",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    items(searchResults, key = { it.id }) { animal ->
                        AnimalCard(
                            animal = animal,
                            onClick = {
                                doSearch(query)
                                onAnimalClick(animal.id)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFlow(
    items: List<String>,
    onClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        var currentRow = mutableListOf<String>()
        var currentWidth = 0f
        val maxWidth = 320f // 近似最大宽度

        for (item in items) {
            val itemWidth = item.length * 16f + 32f
            if (currentWidth + itemWidth > maxWidth && currentRow.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (label in currentRow) {
                        Surface(
                            modifier = Modifier.clickable { onClick(label) },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
                currentRow = mutableListOf()
                currentWidth = 0f
            }
            currentRow.add(item)
            currentWidth += itemWidth + 8f
        }
        if (currentRow.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (label in currentRow) {
                    Surface(
                        modifier = Modifier.clickable { onClick(label) },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

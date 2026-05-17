package com.example.code.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.code.data.local.entity.AnimalEntity
import com.example.code.data.remote.dto.DescriptionInfo
import com.example.code.ui.AnimalViewModel
import com.example.code.ui.auth.AuthViewModel
import com.example.code.ui.common.ConservationBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    animalId: Int,
    viewModel: AnimalViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var animal by remember { mutableStateOf<AnimalEntity?>(null) }
    val apiDescriptions by viewModel.apiDescriptions.collectAsState()
    val isLoading by viewModel.isLoadingDescription.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    var showFullImage by remember { mutableStateOf(false) }

    LaunchedEffect(animalId) {
        animal = viewModel.getAnimalById(animalId)
        animal?.let {
            viewModel.loadSpeciesDescription(it.name, it.category)
            currentUser?.let { user ->
                viewModel.loadFavoriteStatus(user.id, animalId)
                viewModel.addHistory(user.id, animalId)
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearDescription() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(animal?.name ?: "加载中...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (currentUser != null) {
                        IconButton(onClick = {
                            currentUser?.let { user ->
                                viewModel.toggleFavorite(user.id, animalId)
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "取消收藏" else "收藏",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onPrimary
                            )
                        }
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
        animal?.let { a ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Image
                Box {
                    AsyncImage(
                        model = a.imageUrl,
                        contentDescription = a.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 10f)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = { showFullImage = true }
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.4f)
                                    ),
                                    startY = 300f
                                )
                            )
                    )
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = a.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ConservationBadge(status = a.conservationStatus)
                    }
                    Text(
                        text = a.latinName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = a.category,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    InfoCard(
                        icon = Icons.Filled.Info,
                        title = "简介",
                        content = a.description,
                        iconTint = MaterialTheme.colorScheme.primary
                    )
                    InfoCard(
                        icon = Icons.Filled.LocationOn,
                        title = "栖息地",
                        content = a.habitat,
                        iconTint = MaterialTheme.colorScheme.secondary
                    )
                    InfoCard(
                        icon = Icons.Filled.Star,
                        title = "食性",
                        content = a.diet,
                        iconTint = MaterialTheme.colorScheme.tertiary
                    )
                    if (a.bodySize.isNotEmpty()) {
                        InfoCard(
                            icon = Icons.Filled.Info,
                            title = "体型",
                            content = a.bodySize,
                            iconTint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (a.distribution.isNotEmpty()) {
                        InfoCard(
                            icon = Icons.Filled.LocationOn,
                            title = "分布区域",
                            content = a.distribution,
                            iconTint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (a.taxonomy.isNotEmpty()) {
                        InfoCard(
                            icon = Icons.Filled.Star,
                            title = "分类学信息",
                            content = a.taxonomy,
                            iconTint = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "正在加载数据库资料...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    if (apiDescriptions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "数据库资料",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        apiDescriptions.forEach { desc ->
                            ApiDescriptionCard(desc)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 全屏图片查看（覆盖在 Column 之上）
            if (showFullImage) {
                FullScreenImage(
                    imageUrl = a.imageUrl,
                    contentDescription = a.name,
                    onDismiss = { showFullImage = false }
                )
            }
        }
    }
}

@Composable
private fun FullScreenImage(
    imageUrl: String?,
    contentDescription: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismiss() })
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun ApiDescriptionCard(desc: DescriptionInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = desc.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = desc.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
            desc.refs?.firstOrNull()?.let { ref ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "参考: $ref",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    title: String,
    content: String,
    iconTint: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}

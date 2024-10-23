package com.example.newsapplication.ui.news

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.newsapplication.utilities.formatDurationBetween
import com.example.newsapplication.utilities.getPublishedAtAsLocalDateTime


@Composable
fun NewsDetailScreen(
    navController: NavController,
    viewModel: NewsViewModel,
) {
    BackHandler {
        navController.navigateUp()
    }
    val article = viewModel.uiState.collectAsStateWithLifecycle().value.selectedArticle
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Blue)
                    .padding(top = 40.dp, bottom = 20.dp),


            ) {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "News Details",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    ) { paddingValues ->
        Spacer(modifier = Modifier.height(200.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues = paddingValues)
                .verticalScroll(scrollState),

            ) {
            AsyncImage(
                model = article?.urlToImage,
                contentDescription = "Image for ${article?.title}",
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {

                Spacer(modifier = Modifier.size(10.dp))
                article?.title?.let {
                    Text(
                        text = it, color = Color.Black,
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    article?.author?.let {
                        Text(
                            text = it, color = Color.Black,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        text = article?.getPublishedAtAsLocalDateTime()?.formatDurationBetween()
                            ?: "",
                        color = Color.Black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))
                article?.content?.let {
                    Text(
                        text = it, color = Color.Black,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NewsDetailsScreenPreview() {
    NewsDetailScreen(
        navController = NavController(LocalContext.current),
        viewModel = hiltViewModel()
    )
}
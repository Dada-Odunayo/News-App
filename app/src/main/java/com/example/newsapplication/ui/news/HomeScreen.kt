package com.example.newsapplication.ui.news

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.newsapplication.NewsNavigation
import com.example.newsapplication.model.Article
import com.example.newsapplication.utilities.formatDurationBetween
import com.example.newsapplication.utilities.getPublishedAtAsLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: NewsViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
    val articles by viewModel.newsArticle.collectAsStateWithLifecycle()
    BackHandler {
        //prevent backward navigation
    }
    Scaffold(
        topBar = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Blue)
                    .padding(top=40.dp)

            ) {
                Text("News App", style = MaterialTheme.typography.displaySmall, color = Color.White)
            }
        },
    ) { paddingValues ->
        val keyboard = LocalSoftwareKeyboardController.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(20.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = uiState.query,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    onValueChange = viewModel::updateQuery,
                    placeholder = {
                        Text("Search News...")
                    },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search News",
                                    tint = Color.Gray
                                )
                            },
                            onClick = {
                                keyboard?.hide()
                                viewModel.fetchNews()
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                        autoCorrectEnabled = true
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboard?.hide()
                            viewModel.fetchNews()
                        }
                    )
                )
                Spacer(Modifier.height(5.dp))
                if (!uiState.fromNetwork && articles.isNotEmpty()) {
                    Text(
                        "Data Fetched Locally",
                        color = Color.Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                if (articles.isEmpty() && loadingState !is LoadingState.Loading) {

                    Text(
                        "No articles found",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.titleMedium
                    )

                }
                Spacer(Modifier.height(30.dp))
                LazyColumn(
                    state = rememberLazyListState().also { listState ->
                        // Trigger fetch when reaching the end
                        LaunchedEffect(listState) {
                            snapshotFlow { listState.layoutInfo }
                                .collect { layoutInfo ->
                                    val visibleItems = layoutInfo.visibleItemsInfo
                                    val totalItems = layoutInfo.totalItemsCount
                                    if (visibleItems.isNotEmpty() && visibleItems.last().index == totalItems - 1) {
                                        viewModel.fetchNews()
                                    }
                                }
                        }
                    }
                ) {
                    items(articles) { article ->
                        NewsItem(article) {
                            viewModel.updateSelectedArticle(article.url)
                            navController.navigate(NewsNavigation.NewsDetailsScreen.route)
                        }
                        Spacer(Modifier.height(15.dp))
                    }
                }
            }

            // Show CircularProgressIndicator if loading
            when (loadingState) {
                is LoadingState.Empty -> {

                }

                is LoadingState.Success -> {

                }

                is LoadingState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = 0.5f))
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is LoadingState.Error -> {
                    val errorMessage = (loadingState as LoadingState.Error).error
                    ModalBottomSheet(
                        onDismissRequest = viewModel::resetUI
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Error: $errorMessage",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        }
                    }

                }

            }
        }
    }
}


@Composable
fun NewsItem(
    article: Article,
    goToNewsDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                goToNewsDetails()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RectangleShape
    ) {
        Row(modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {


        Row(
            modifier = Modifier.width(105.dp)
        ) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = "Image for ${article.title}",
            )

        }
        Spacer(Modifier.width(4.dp))
        Row {
            Column(
                modifier = Modifier
                    .height(100.dp)
                    .padding(3.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = article.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.getPublishedAtAsLocalDateTime().formatDurationBetween(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }}

    }
}


@Preview
@Composable
fun NewsScreenPreview() {
    HomeScreen(NavController(LocalContext.current), hiltViewModel())
}
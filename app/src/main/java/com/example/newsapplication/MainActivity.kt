package com.example.newsapplication

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.newsapplication.ui.news.HomeScreen
import com.example.newsapplication.ui.news.NewsDetailScreen
import com.example.newsapplication.ui.news.NewsViewModel
import com.example.newsapplication.ui.theme.NewsApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            NewsApplicationTheme {
                val newsViewModel: NewsViewModel = hiltViewModel()
                NavHost(
                    navController = navController,
                    startDestination = NewsNavigation.HomeScreen.route
                ) {
                    composable(NewsNavigation.HomeScreen.route) {
                        HomeScreen(navController = navController, viewModel = newsViewModel)
                    }
                    composable(NewsNavigation.NewsDetailsScreen.route) {
                        NewsDetailScreen(navController = navController, viewModel = newsViewModel)
                    }

                }

            }
        }
    }
}


sealed class NewsNavigation(val route: String) {
    object HomeScreen : NewsNavigation(route = "HomeScreen")
    object NewsDetailsScreen : NewsNavigation(route = "NewsDetailsScreen")
}


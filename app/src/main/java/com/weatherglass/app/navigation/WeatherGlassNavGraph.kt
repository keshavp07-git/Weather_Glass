package com.weatherglass.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.weatherglass.app.WeatherGlassApplication
import com.weatherglass.app.domain.model.CityLocation
import com.weatherglass.app.feature.cities.ManageCitiesScreen
import com.weatherglass.app.feature.cities.ManageCitiesViewModel
import com.weatherglass.app.feature.details.DetailedWeatherScreen
import com.weatherglass.app.feature.details.DetailedWeatherViewModel
import com.weatherglass.app.feature.home.HomeScreen
import com.weatherglass.app.feature.home.HomeViewModel
import com.weatherglass.app.feature.maps.WeatherMapScreen
import com.weatherglass.app.feature.search.SearchScreen
import com.weatherglass.app.feature.search.SearchViewModel
import com.weatherglass.app.feature.settings.SettingsScreen
import com.weatherglass.app.feature.settings.SettingsViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object NavRoutes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val CITIES = "cities"
    const val SETTINGS = "settings"
    const val MAPS = "maps"
    const val DETAILS = "details/{cityId}"

    fun createDetailsRoute(cityId: String): String {
        val encId = URLEncoder.encode(cityId, StandardCharsets.UTF_8.toString())
        return "details/$encId"
    }
}

@Composable
fun WeatherGlassNavHost(
    navController: NavHostController,
    app: WeatherGlassApplication,
    modifier: Modifier = Modifier
) {
    val container = app.container

    // Shared HomeViewModel so search selection and saved cities selection update the home pager
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(
            weatherRepository = container.weatherRepository,
            citySearchRepository = container.citySearchRepository,
            preferencesRepository = container.userPreferencesRepository,
            locationProvider = container.locationProvider
        )
    )

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSearch = { navController.navigate(NavRoutes.SEARCH) },
                onNavigateToManageCities = { navController.navigate(NavRoutes.CITIES) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) },
                onNavigateToDetails = { city ->
                    navController.navigate(NavRoutes.createDetailsRoute(city.id))
                }
            )
        }

        composable(NavRoutes.SEARCH) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.Factory(
                    citySearchRepository = container.citySearchRepository
                )
            )

            SearchScreen(
                viewModel = searchViewModel,
                onNavigateBack = { navController.popBackStack() },
                onCitySelected = { city ->
                    // Add city to favorites, prefetch weather, and switch home page directly to it
                    homeViewModel.selectOrAddCity(city)
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.CITIES) {
            val citiesViewModel: ManageCitiesViewModel = viewModel(
                factory = ManageCitiesViewModel.Factory(
                    citySearchRepository = container.citySearchRepository,
                    weatherRepository = container.weatherRepository,
                    preferencesRepository = container.userPreferencesRepository
                )
            )

            ManageCitiesScreen(
                viewModel = citiesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSearch = { navController.navigate(NavRoutes.SEARCH) },
                onCitySelected = { selectedIndex ->
                    // Switch home page to the tapped city
                    homeViewModel.onPageChanged(selectedIndex)
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoutes.SETTINGS) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(
                    preferencesRepository = container.userPreferencesRepository,
                    weatherRepository = container.weatherRepository
                )
            )

            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.MAPS) {
            WeatherMapScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoutes.DETAILS,
            arguments = listOf(
                navArgument("cityId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cityId = URLDecoder.decode(backStackEntry.arguments?.getString("cityId") ?: "", StandardCharsets.UTF_8.toString())
            val homeUiState by homeViewModel.uiState.collectAsState()
            val city = homeUiState.cities.find { it.id == cityId }
                ?: (container.citySearchRepository as? com.weatherglass.app.data.cities.CitySearchRepositoryImpl)?.defaultStarterCities?.find { it.id == cityId }
                ?: CityLocation(id = cityId, name = "Weather Details", latitude = 0.0, longitude = 0.0)

            val detailsViewModel: DetailedWeatherViewModel = viewModel(
                key = cityId,
                factory = DetailedWeatherViewModel.Factory(
                    city = city,
                    weatherRepository = container.weatherRepository,
                    preferencesRepository = container.userPreferencesRepository
                )
            )

            DetailedWeatherScreen(
                city = city,
                viewModel = detailsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

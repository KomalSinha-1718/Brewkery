package com.brewkery.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brewkery.app.AppContainer
import com.brewkery.app.ui.cart.CartRoute
import com.brewkery.app.ui.cart.CartViewModel
import com.brewkery.app.ui.detail.ItemDetailRoute
import com.brewkery.app.ui.detail.ItemDetailViewModel
import com.brewkery.app.ui.menu.MenuRoute
import com.brewkery.app.ui.menu.MenuViewModel
import com.brewkery.app.ui.order.OrderStatusRoute

private object Routes {
    const val MENU = "menu"
    const val DETAIL = "detail/{itemId}"
    const val CART = "cart"
    const val ORDER = "order"
    const val ARG_ITEM_ID = "itemId"

    fun detail(itemId: Int) = "detail/$itemId"
}

@Composable
fun BrewkeryNavHost(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.MENU) {

        composable(Routes.MENU) {
            MenuRoute(
                viewModel = viewModel<MenuViewModel>(factory = MenuViewModel.factory(container)),
                onItemClick = { id -> navController.navigate(Routes.detail(id)) },
                onCartClick = { navController.navigate(Routes.CART) { launchSingleTop = true } },
                onOrderClick = { navController.navigate(Routes.ORDER) { launchSingleTop = true } },
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument(Routes.ARG_ITEM_ID) { type = NavType.IntType }),
        ) { entry ->
            val itemId = requireNotNull(entry.arguments).getInt(Routes.ARG_ITEM_ID)
            ItemDetailRoute(
                viewModel = viewModel<ItemDetailViewModel>(
                    key = "detail-$itemId",
                    factory = ItemDetailViewModel.factory(container, itemId),
                ),
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.CART) {
            CartRoute(
                viewModel = viewModel<CartViewModel>(factory = CartViewModel.factory(container)),
                onBack = { navController.popBackStack() },
                onOrderPlaced = {
                    // Drop the cart from the back stack so "back" from the receipt goes to the menu.
                    navController.navigate(Routes.ORDER) {
                        popUpTo(Routes.MENU) { inclusive = false }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(Routes.ORDER) {
            OrderStatusRoute(
                orderRepository = container.orderRepository,
                onBackToMenu = { navController.popBackStack(Routes.MENU, inclusive = false) },
            )
        }
    }
}

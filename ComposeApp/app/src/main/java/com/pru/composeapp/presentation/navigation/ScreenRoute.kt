package com.pru.composeapp.presentation.navigation

sealed class ScreenRoute(val route: String, val title: String = "") {
    object MySplashScreen : ScreenRoute("my_splash_screen")
    object HomeScreen : ScreenRoute("home_screen", title = "Home")
    object ProductDetailsScreen : ScreenRoute("product_details_screen")
    object SearchScreen : ScreenRoute("search_screen")
    object ShopByCategoryScreen : ScreenRoute("shop_by_category_screen", title = "Shop by Category")
    object MyOrdersScreen : ScreenRoute("my_orders_screen", title = "My Orders")
    object WishSavedListScreen : ScreenRoute("wish_saved_list_screen", title = "Wish / Saved List")
    object HelpScreen : ScreenRoute("help_screen", title = "Help")
    object MyAccountScreen : ScreenRoute("my_account_screen", title = "My Account")
    object CheckoutScreen : ScreenRoute("check_out_screen")
    object PaymentScreen : ScreenRoute("payment_screen")
    object OrderSummaryScreen : ScreenRoute("order_summary_screen")
    object LoginScreen : ScreenRoute("login_screen", title = "Sign In")
}

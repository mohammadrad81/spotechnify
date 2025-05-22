package com.example.spotechnify

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val authRepository: AuthRepository = hiltViewModel<AuthRepository>()

                val startDestination = if (authRepository.hasAccessToken()) {
                    NavRoutes.Home.route
                } else {
                    NavRoutes.Welcome.route
                }

                AuthNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    onAuthComplete = { navController.navigate(NavRoutes.Home.route) }
                )
            }
        }
    }
}
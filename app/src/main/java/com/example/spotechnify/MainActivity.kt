package com.example.spotechnify

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel()

                // Check auth state when activity starts
                LaunchedEffect(Unit) {
                    authViewModel.checkAuthState()
                }

                // Observe auth state changes
                val authState by authViewModel.authState.collectAsState()

                val startDestination = if (authState.isAuthenticated) {
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
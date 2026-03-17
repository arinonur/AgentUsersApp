package com.example.AgentUsersApp.feature.auth.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.AgentUsersApp.core.firebase.FirebaseModule
import com.example.AgentUsersApp.feature.auth.data.FirebaseAuthRepository
import com.example.AgentUsersApp.feature.calendar.presentation.CalendarTabScreenRoute
import com.example.AgentUsersApp.feature.home.data.FirestoreTaskRepository
import com.example.AgentUsersApp.feature.home.presentation.CreateTaskScreenRoute
import com.example.AgentUsersApp.feature.home.presentation.CreateTaskViewModel
import com.example.AgentUsersApp.feature.home.presentation.EditTaskScreenRoute
import com.example.AgentUsersApp.feature.home.presentation.EditTaskViewModel
import com.example.AgentUsersApp.feature.home.presentation.TaskDetailScreenRoute
import com.example.AgentUsersApp.feature.home.presentation.TaskDetailViewModel
import com.example.AgentUsersApp.feature.home.presentation.TaskHomeScreenRoute
import com.example.AgentUsersApp.feature.home.presentation.TaskHomeViewModel

private object AuthRoutes {
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val Calendar = "calendar"
    const val CreateTask = "create_task"
    const val EditTask = "edit_task"
    const val TaskDetail = "task_detail"
    const val TaskIdArg = "taskId"
}

@Composable
fun AuthNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val startDestination = if (FirebaseModule.auth.currentUser != null) {
        AuthRoutes.Home
    } else {
        AuthRoutes.Login
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(AuthRoutes.Login) {
            val repository = FirebaseAuthRepository(
                auth = FirebaseModule.auth,
                firestore = FirebaseModule.firestore,
            )
            val viewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return LoginViewModel(repository) as T
                    }
                }
            )

            LoginScreenRoute(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(AuthRoutes.Home) {
                        popUpTo(AuthRoutes.Login) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onSignUpClick = {
                    navController.navigate(AuthRoutes.Register)
                },
            )
        }

        composable(AuthRoutes.Register) {
            val repository = FirebaseAuthRepository(
                auth = FirebaseModule.auth,
                firestore = FirebaseModule.firestore,
            )
            val viewModel: RegisterViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return RegisterViewModel(repository) as T
                    }
                }
            )

            RegisterScreenRoute(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(AuthRoutes.Home) {
                        popUpTo(AuthRoutes.Login) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onSignInClick = {
                    navController.popBackStack()
                },
            )
        }

        composable(AuthRoutes.Home) {
            val repository = FirestoreTaskRepository(
                auth = FirebaseModule.auth,
                firestore = FirebaseModule.firestore,
            )
            val viewModel: TaskHomeViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskHomeViewModel(repository) as T
                    }
                }
            )

            TaskHomeScreenRoute(
                viewModel = viewModel,
                onLogoutClick = {
                    FirebaseModule.auth.signOut()
                    navController.navigate(AuthRoutes.Login) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onCreateTaskClick = {
                    navController.navigate(AuthRoutes.CreateTask)
                },
                onTaskClick = { taskId ->
                    navController.navigate("${AuthRoutes.TaskDetail}/$taskId")
                },
                onBottomTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigateToTopLevel(AuthRoutes.Home)
                        1 -> navController.navigateToTopLevel(AuthRoutes.Calendar)
                    }
                },
                selectedTabIndex = 0,
            )
        }

        composable(AuthRoutes.Calendar) {
            val repository = FirestoreTaskRepository(
                auth = FirebaseModule.auth,
                firestore = FirebaseModule.firestore,
            )
            val viewModel: TaskHomeViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskHomeViewModel(repository) as T
                    }
                }
            )

            CalendarTabScreenRoute(
                viewModel = viewModel,
                onAddClick = {
                    navController.navigate(AuthRoutes.CreateTask)
                },
                onTaskClick = { taskId ->
                    navController.navigate("${AuthRoutes.TaskDetail}/$taskId")
                },
                onBottomTabSelected = { tabIndex ->
                    when (tabIndex) {
                        0 -> navController.navigateToTopLevel(AuthRoutes.Home)
                        1 -> navController.navigateToTopLevel(AuthRoutes.Calendar)
                    }
                },
            )
        }

        composable(AuthRoutes.CreateTask) {
            val repository = FirestoreTaskRepository(
                auth = FirebaseModule.auth,
                firestore = FirebaseModule.firestore,
            )
            val viewModel: CreateTaskViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CreateTaskViewModel(repository) as T
                    }
                }
            )

            CreateTaskScreenRoute(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onTaskCreated = {
                    navController.navigate(AuthRoutes.Home) {
                        popUpTo(AuthRoutes.Home) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = "${AuthRoutes.EditTask}/{${AuthRoutes.TaskIdArg}}",
            arguments = listOf(
                navArgument(AuthRoutes.TaskIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString(AuthRoutes.TaskIdArg).orEmpty()
            val repository = FirestoreTaskRepository(
                auth = FirebaseModule.auth,
                firestore = FirebaseModule.firestore,
            )
            val viewModel: EditTaskViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return EditTaskViewModel(
                            taskId = taskId,
                            repository = repository,
                        ) as T
                    }
                }
            )

            EditTaskScreenRoute(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onTaskUpdated = {
                    navController.navigate(AuthRoutes.Home) {
                        popUpTo(AuthRoutes.Home) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(
            route = "${AuthRoutes.TaskDetail}/{${AuthRoutes.TaskIdArg}}",
            arguments = listOf(
                navArgument(AuthRoutes.TaskIdArg) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString(AuthRoutes.TaskIdArg).orEmpty()
            val repository = FirestoreTaskRepository(
                auth = FirebaseModule.auth,
                firestore = FirebaseModule.firestore,
            )
            val viewModel: TaskDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return TaskDetailViewModel(
                            taskId = taskId,
                            repository = repository,
                        ) as T
                    }
                }
            )
            TaskDetailScreenRoute(
                viewModel = viewModel,
                onEditClick = { editedTaskId ->
                    navController.navigate("${AuthRoutes.EditTask}/$editedTaskId")
                },
                onTaskDeleted = {
                    navController.navigate(AuthRoutes.Home) {
                        popUpTo(AuthRoutes.Home) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

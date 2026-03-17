package com.example.AgentUsersApp.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val RegisterMajorSectionSpacing = 32.dp
private val RegisterRelatedElementSpacing = 16.dp

@Composable
fun RegisterScreenRoute(
    viewModel: RegisterViewModel,
    modifier: Modifier = Modifier,
    onAlternateRegisterClick: (provider: String) -> Unit = {},
    onRegisterSuccess: () -> Unit = {},
    onSignInClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isRegistered) {
        if (uiState.isRegistered) {
            onRegisterSuccess()
        }
    }

    RegisterScreen(
        fullName = uiState.fullName,
        email = uiState.email,
        password = uiState.password,
        confirmPassword = uiState.confirmPassword,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        modifier = modifier,
        isLoading = uiState.isLoading,
        errorMessage = uiState.error,
        successMessage = uiState.successMessage,
        onRegisterClick = { _, _, _, _ -> viewModel.register() },
        onAlternateRegisterClick = onAlternateRegisterClick,
        onSignInClick = onSignInClick,
    )
}

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterClick: (fullName: String, email: String, password: String, confirmPassword: String) -> Unit =
        { _, _, _, _ -> },
    onAlternateRegisterClick: (provider: String) -> Unit = {},
    onSignInClick: () -> Unit = {},
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    RegisterScreen(
        fullName = fullName,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        onFullNameChange = { fullName = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onConfirmPasswordChange = { confirmPassword = it },
        modifier = modifier,
        onRegisterClick = onRegisterClick,
        onAlternateRegisterClick = onAlternateRegisterClick,
        onSignInClick = onSignInClick,
    )
}

@Composable
fun RegisterScreen(
    fullName: String,
    email: String,
    password: String,
    confirmPassword: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    successMessage: String? = null,
    onRegisterClick: (fullName: String, email: String, password: String, confirmPassword: String) -> Unit =
        { _, _, _, _ -> },
    onAlternateRegisterClick: (provider: String) -> Unit = {},
    onSignInClick: () -> Unit = {},
) {
    AuthScaffold(
        modifier = modifier,
        bottomContent = {
            BottomInlineAuthAction(
                prompt = "Already have an account?",
                actionLabel = "Sign In",
                onActionClick = onSignInClick,
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(RegisterMajorSectionSpacing)
        ) {
            AuthHeader(
                title = "Create your Account",
                subtitle = "Enter your details to register and continue",
            )

            Column(verticalArrangement = Arrangement.spacedBy(RegisterRelatedElementSpacing)) {
                AuthTextField(
                    value = fullName,
                    onValueChange = onFullNameChange,
                    label = "Full Name",
                    placeholder = "Enter your full name",
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                )

                AuthTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    placeholder = "Enter your email",
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                )

                PasswordTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Password",
                    placeholder = "Create a password",
                    enabled = !isLoading,
                )

                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = "Confirm Password",
                    placeholder = "Re-enter your password",
                    enabled = !isLoading,
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (successMessage != null) {
                Text(
                    text = successMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(RegisterRelatedElementSpacing)) {
                PrimaryAuthButton(
                    text = if (isLoading) "Creating Account..." else "Create Account",
                    enabled = !isLoading,
                    showLoading = isLoading,
                    onClick = {
                        onRegisterClick(fullName, email, password, confirmPassword)
                    },
                )

                OrDivider()

                AlternateLoginButton(
                    text = "Continue with Google",
                    enabled = !isLoading,
                    onClick = { onAlternateRegisterClick("Google") },
                )

                AlternateLoginButton(
                    text = "Continue with Facebook",
                    enabled = !isLoading,
                    onClick = { onAlternateRegisterClick("Facebook") },
                )
            }

            Spacer(modifier = Modifier.height(RegisterMajorSectionSpacing))
        }
    }
}

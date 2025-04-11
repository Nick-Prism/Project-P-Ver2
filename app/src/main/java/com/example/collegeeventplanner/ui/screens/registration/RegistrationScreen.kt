package com.example.collegeeventplanner.ui.screens.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.collegeeventplanner.ui.components.AppTopBar
import com.example.collegeeventplanner.ui.components.FormTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Event Registration",
                navController = navController,
                showBackButton = true
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FormTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = "Full Name",
                placeholder = "Enter your full name"
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChanged,
                label = "Email",
                placeholder = "Enter your email",
                isError = state.emailError != null,
                errorMessage = state.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            FormTextField(
                value = state.studentId,
                onValueChange = viewModel::onStudentIdChanged,
                label = "Student ID",
                placeholder = "Enter your student ID"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isFormValid && !state.isLoading
            ) {
                Text(if (state.isLoading) "Processing..." else "Register")
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
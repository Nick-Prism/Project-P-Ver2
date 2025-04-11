package com.example.collegeeventplanner.ui.screens.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.collegeeventplanner.ui.components.FormTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventRegistrationScreen(
    navController: NavController,
    viewModel: EventRegistrationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isRegistrationComplete) {
        if (state.isRegistrationComplete) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Registration") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Register for: ${state.eventTitle}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            FormTextField(
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                label = "Full Name",
                placeholder = "Enter your full name",
                isError = state.nameError != null,
                errorMessage = state.nameError
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
                placeholder = "Enter your student ID",
                isError = state.studentIdError != null,
                errorMessage = state.studentIdError
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.registerForEvent() },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.isFormValid && !state.isLoading
            ) {
                Text(if (state.isLoading) "Registering..." else "Register")
            }

            if (state.error != null) {
                Text(
                    text = state.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (state.qrCodeBitmap != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Registration Successful!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Show this QR code at the event for check-in",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                // QR code display would be implemented here
            }
        }
    }
}
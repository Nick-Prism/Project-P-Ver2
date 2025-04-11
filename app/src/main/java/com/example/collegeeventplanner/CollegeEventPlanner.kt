package com.example.collegeeventplanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.collegeeventplanner.navigation.NavGraph

@Composable
fun CollegeEventPlannerApp() {
    NavGraph()
}

@Preview(showBackground = true)
@Composable
fun CollegeEventPlannerPreview() {
    CollegeEventPlannerApp()
}

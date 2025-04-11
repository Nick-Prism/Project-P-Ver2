package com.example.collegeeventplanner.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.collegeeventplanner.R
import com.example.collegeeventplanner.domain.model.Event
import com.example.collegeeventplanner.ui.theme.CollegeEventPlannerTheme

@Composable
fun EventCard(
    event: Event,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${event.date} • ${event.time}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (event.clubLogoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = event.clubLogoUrl,
                        contentDescription = "Club Logo",
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit,
                        placeholder = painterResource(R.drawable.ic_club_placeholder)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_club_placeholder),
                        contentDescription = "Club Logo Placeholder",
                        modifier = Modifier.size(40.dp)
                    )
                }
                Button(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Register")
                }
            }
        }
    }
}

@Preview
@Composable
fun EventCardPreview() {
    CollegeEventPlannerTheme {
        EventCard(
            event = Event(
                id = "1",
                title = "Tech Symposium",
                date = "Oct 15, 2023",
                time = "10:00 AM",
                clubLogoUrl = ""
            ),
            onRegisterClick = {}
        )
    }
}
package com.anahjanes.feature_weather.week

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anahjanes.feature_weather.week.model.WeekUiModel

@Composable
fun WeeklyForecastCard(
    forecast: WeekUiModel,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 18.dp) // más aire
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🟠 Icono con fondo (más grande)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = Color(0xFFC9E8FF),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                forecast.iconUrl?.let {
                    coil.compose.AsyncImage(
                        model = it,
                        contentDescription = forecast.weather,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            // 📅 Día + descripción (más grande y más legible)
            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = forecast.day,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    lineHeight = 24.sp
                )

                if (forecast.date != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = forecast.date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = forecast.weather,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 🌡 Temperaturas (más grandes)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "↑ ${forecast.maxTemp}°",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "↓ ${forecast.minTemp}°",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview
@Composable
fun WeeklyForecastCardPreview(){
    WeeklyForecastCard(forecast = weeklyForecasts.first())
}
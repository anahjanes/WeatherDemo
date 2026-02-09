package com.anahjanes.feature_weather.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anahjanes.feature_weather.R
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage

@Composable
fun CurrentWeatherCard(
    temperature: String,
    condition: String,
    feelsLike: String,
    iconUrl: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color = Color.Transparent, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (iconUrl != null) {
                    AsyncImage(
                        model = iconUrl,
                        contentDescription = condition,
                        modifier = Modifier.size(84.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = temperature,
                fontSize = 86.sp,
                color = Color.White,
                fontWeight = FontWeight.Light
            )

            Text(
                text = condition,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.feels_like) + " " + feelsLike,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary            )
            Spacer(modifier = Modifier.height(4.dp))


        }
    }
}

@Preview
@Composable
fun CurrentWeatherCardPreview() {

        CurrentWeatherCard(
            temperature = "12°",
            condition = "Clear",
            feelsLike = "10°",
            iconUrl = null
        )

}

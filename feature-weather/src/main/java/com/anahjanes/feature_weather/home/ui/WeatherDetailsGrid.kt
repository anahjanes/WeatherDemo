package com.anahjanes.feature_weather.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anahjanes.feature_weather.R
import com.anahjanes.feature_weather.ui.theme.CloudsColor
import com.anahjanes.feature_weather.ui.theme.HumidityColor
import com.anahjanes.feature_weather.ui.theme.TempColor
import com.anahjanes.feature_weather.ui.theme.WindColor


@Composable
fun WeatherDetailsGrid(
    tempMax: String,
    tempMin: String,
    clouds: String,
    wind: String,
    humidity: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_temp,
                label = stringResource(id = R.string.weather_detail_temp_hl),
                value = "$tempMax / $tempMin",
                iconBackgroundColor = TempColor
            )
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_cloud,
                label = stringResource(id = R.string.weather_detail_clouds),
                value = clouds,
                iconBackgroundColor = CloudsColor
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_wind,
                label = stringResource(id = R.string.weather_detail_wind),
                value = wind,
                iconBackgroundColor = WindColor
            )
            WeatherDetailItem(
                modifier = Modifier.weight(1f),
                icon = R.drawable.ic_humidity,
                label = stringResource(id = R.string.weather_detail_humidity),
                value = humidity,
                iconBackgroundColor = HumidityColor
            )
        }
    }
}

@Preview(
    name = "Small phone",
    widthDp = 320,
    heightDp = 568,
    showBackground = true
)
@Composable
fun WeatherDetailsGridPreview() {

        WeatherDetailsGrid(
            tempMax = "25°C",
            tempMin = "15°C",
            clouds = "25%",
            wind = "10 km/h",
            humidity = "50%"
        )

}

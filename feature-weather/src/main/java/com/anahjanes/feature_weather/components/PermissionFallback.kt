package com.anahjanes.feature_weather.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anahjanes.feature_weather.R


@Composable
fun PermissionFallback(
    onOpenCity: () -> Unit,
    onRequestPermissionAgain: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.need_location_message),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(16.dp))

            Button(onClick = onRequestPermissionAgain, modifier = Modifier.width( 200.dp)) {
                Text(stringResource(R.string.give_permission))
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = onOpenCity, modifier = Modifier.width( 200.dp)) {
                Text(stringResource(R.string.select_city))
            }
        }
    }
}

@Preview
@Composable
fun PreviewPermissionFallback() {
    PermissionFallback({}, {})
}
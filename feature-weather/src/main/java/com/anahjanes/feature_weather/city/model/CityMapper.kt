package com.anahjanes.feature_weather.city.model

import com.anahjanes.core.data.remote.dto.GeoCityDto

fun GeoCityDto.toUiItem(): CityUiModel =
    CityUiModel(
        name = name,
        country = country,
        lat = lat,
        lon = lon
    )
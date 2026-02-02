package com.anahjanes.feature_weather.city.model


import com.anahjanes.core_domain.model.CityResult

fun CityResult.toUiItem(): CityUiModel =
    CityUiModel(
        name = name,
        country = country,
        lat = lat,
        lon = lon
    )
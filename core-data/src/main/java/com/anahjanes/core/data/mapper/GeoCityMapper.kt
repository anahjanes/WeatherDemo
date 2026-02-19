package com.anahjanes.core.data.mapper

import com.anahjanes.core.data.remote.dto.GeoCityDto
import com.anahjanes.core_domain.model.CityResult

internal fun GeoCityDto.toModel(): CityResult =
    CityResult(
        name = name,
        country = country,
        lat = lat,
        lon = lon
    )
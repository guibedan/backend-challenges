package com.guibedan.investment.aggregator.controller.dto;

import java.time.Instant;

public record UserDto(String username, String email, Instant createdAt) {
}

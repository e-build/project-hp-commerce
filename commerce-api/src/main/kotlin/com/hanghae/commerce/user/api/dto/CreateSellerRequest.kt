package com.hanghae.commerce.user.api.dto

import jakarta.validation.constraints.NotBlank

data class CreateSellerRequest(
    @field:NotBlank(message = "이름을 공백으로 할 수 없습니다.")
    val name: String,
    val age: Int,
    @field:NotBlank(message = "이메일을 공백으로 할 수 없습니다.")
    val email: String,
    @field:NotBlank(message = "주소를 공백으로 할 수 없습니다.")
    val address: String,
)

package com.debusun.base.model

interface BaseResult {
    val code: Int
    val message: String
    val data: Any
}

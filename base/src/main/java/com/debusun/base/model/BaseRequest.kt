package com.debusun.base.model

interface BaseRequest {
    val gid: String
    val action: String
    val data: Any
}

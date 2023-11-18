package com.debusun.remotecontrol.model

import com.debusun.base.model.BaseResult

data class AirResult(override val code: Int, override val message: String, override val data: Any) : BaseResult

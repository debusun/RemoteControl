package com.debusun.remotecontrol.model

import com.debusun.base.model.BaseRequest

data class AirRequest(
    override val gid: String,
    override val action: String,
    override val data: Any
) : BaseRequest

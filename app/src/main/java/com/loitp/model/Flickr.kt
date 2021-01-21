package com.loitp.model

import com.core.common.Constants
import java.io.Serializable

data class Flickr(
        var title: String = "",
        var flickrId: String = Constants.FLICKR_ID_VN_DOCDAOTHUVI
) : Serializable

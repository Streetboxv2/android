package com.zeepos.models.entities

import com.google.maps.model.LatLng
import com.zeepos.models.ConstVar

/**
 * Created by Arif S. on 5/27/20
 */
class MapData(var type: String) {
    var latLngList: List<LatLng> = ArrayList()
    var uniqueId: String = ConstVar.EMPTY_STRING
    var latLng: LatLng? = null
    var exData: Any? = null
}
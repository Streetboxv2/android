package com.zeepos.models.master

import com.zeepos.models.ConstVar
import com.zeepos.models.converter.EmptyListConverter
import com.zeepos.models.converter.StringListConverter
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

/**
 * Created by Arif S. on 5/12/20
 */
@Entity
class ParkingSpace {
    @Id(assignable = true)
    var id: Long = 0L
    var name: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var description: String? = ConstVar.EMPTY_STRING
    var point: Long = 0L
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var totalSpace: Int = 0
    var startTime: String? = ConstVar.EMPTY_STRING
    var endTime: String? = ConstVar.EMPTY_STRING
    var rating: Double = 0.0
    var createdAt: String = ConstVar.EMPTY_STRING
    var updatedAt: String = ConstVar.EMPTY_STRING
    var landlordInfo: String? = ConstVar.EMPTY_STRING
    var startContract: String? = ConstVar.EMPTY_STRING
    var endContract: String? = ConstVar.EMPTY_STRING
    var distance: String? = ConstVar.EMPTY_STRING
    var isCheckin: Boolean = false

    @Convert(converter = StringListConverter::class, dbType = String::class)
    var documentsMeta: List<String>? = ArrayList()

    @Convert(converter = StringListConverter::class, dbType = String::class)
    var imagesMeta: List<String>? = ArrayList()

    @Backlink(to = "parkingSpace")
    lateinit var parkingSlots: ToMany<ParkingSlot>

    /**
     * Only for api response not save to DB
     */
    @Convert(converter = EmptyListConverter::class, dbType = String::class)
    var slot: List<ParkingSlot> = ArrayList()

}
package com.zeepos.models.transaction

import com.google.gson.annotations.SerializedName
import com.zeepos.models.ConstVar
import com.zeepos.models.converter.StringListConverter
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

/**
 * Created by Arif S. on 5/15/20
 */
@Entity
class ParkingSales {
    @Id(assignable = true)
    var id: Long = 0L
    var uniqueId: String? = ConstVar.EMPTY_STRING
    var orderUniqueId: String? = ConstVar.EMPTY_STRING
    var parkingSpaceId: Long = 0L
    var orderId = 0L

    @SerializedName("merchant_id")
    var merchantId = 0L
    var name: String? = ConstVar.EMPTY_STRING
    var address: String? = ConstVar.EMPTY_STRING
    var rating: Double = 0.0
    var description: String? = ConstVar.EMPTY_STRING
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var isTracking: Boolean = false
    var price: Double = 0.0
    var subtotal: Double = 0.0
    var businessDate: Long = 0L
    var createdAt: Long = 0L
    var updatedAt: Long = 0L
    var startTime: String? = ConstVar.EMPTY_STRING
    var endTime: String? = ConstVar.EMPTY_STRING
    var trxVisitSalesId: Long = 0L
    var profilePicture: String = ConstVar.EMPTY_STRING

    @Convert(converter = StringListConverter::class, dbType = String::class)
    var images: List<String>? = arrayListOf()

    lateinit var order: ToOne<Order>

    @Backlink(to = "parkingSales")
    lateinit var parkingSlotSales: ToMany<ParkingSlotSales>

    var tasksId = 0L
    var platNo: String? = ConstVar.EMPTY_STRING
}
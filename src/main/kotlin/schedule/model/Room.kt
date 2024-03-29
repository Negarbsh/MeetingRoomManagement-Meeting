package schedule.model

import schedule.model.enums.Feature
import schedule.model.enums.Office
import org.bson.types.ObjectId

class Room(val id: ObjectId, val name: String, val office: Office, val features: List<Feature>, val capacity: Int) {
    fun isCapacityFine(minCapacity: Int, maxCapacity: Int): Boolean {
        return this.capacity in minCapacity..maxCapacity
    }

    fun hasFeatures(features: List<Feature>?): Boolean {
        if (features == null) return true
        for (feature in features) {
            if (!this.features.contains(feature)) return false
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        return other is Room && this.id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
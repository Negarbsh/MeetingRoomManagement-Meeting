package scheduler.model

import scheduler.model.enums.Feature
import scheduler.model.enums.Office
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
}
package scheduler.dao

import scheduler.model.Room
import scheduler.model.enums.Feature
import org.bson.types.ObjectId

interface RoomReader {
    fun getAllRooms(): HashMap<ObjectId, Room>
    fun searchRooms(
        features: List<Feature>? = null,
        minCapacity: Int = 0,
        maxCapacity: Int = -1
    ): HashMap<ObjectId, Room>

}
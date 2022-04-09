package schedule.dao.roomGrpc

import org.bson.types.ObjectId
import schedule.model.Room
import schedule.model.enums.Feature

interface RoomReader {
    fun findAllRooms(): List<Room>
    fun searchRooms(
        features: List<Feature>? = null,
        minCapacity: Int = 0,
        maxCapacity: Int = -1
    ): List<Room>

    fun findAllByIds(roomIds: List<ObjectId>): List<Room>

}
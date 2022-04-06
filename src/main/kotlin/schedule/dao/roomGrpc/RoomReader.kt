package schedule.dao.roomGrpc

import schedule.model.Room
import schedule.model.enums.Feature

interface RoomReader {
    fun getAllRooms(): List<Room>
    fun searchRooms(
        features: List<Feature>? = null,
        minCapacity: Int = 0,
        maxCapacity: Int = -1
    ): List<Room>

}
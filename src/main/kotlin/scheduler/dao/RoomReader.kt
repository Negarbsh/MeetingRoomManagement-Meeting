package scheduler.dao

import scheduler.model.Room
import scheduler.model.enums.Feature

interface RoomReader {
    fun getAllRooms(): List<Room>
    fun searchRooms(
        features: List<Feature>? = null,
        minCapacity: Int = 0,
        maxCapacity: Int = -1
    ): List<Room>

}
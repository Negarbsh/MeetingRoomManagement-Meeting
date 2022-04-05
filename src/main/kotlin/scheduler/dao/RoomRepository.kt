package scheduler.dao

import scheduler.model.Room
import scheduler.model.enums.Feature
import org.springframework.stereotype.Component

@Component
class RoomRepository : RoomReader {
    override fun getAllRooms(): List<Room> {
        TODO("Not yet implemented")
    }

    override fun searchRooms(features: List<Feature>?, minCapacity: Int, maxCapacity: Int): List<Room> {
        TODO("Not yet implemented")
    }
}

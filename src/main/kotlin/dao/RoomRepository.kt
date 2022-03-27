package dao

import model.Room
import model.enums.Feature
import org.bson.types.ObjectId

class RoomRepository : RoomReader {
    override fun getAllRooms(): HashMap<ObjectId, Room> {
        TODO("Not yet implemented")
    }

    override fun searchRooms(features: List<Feature>?, minCapacity: Int, maxCapacity: Int): HashMap<ObjectId, Room> {
        TODO("Not yet implemented")
    }
}
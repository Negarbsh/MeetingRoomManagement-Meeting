package schedule.dao.roomGrpc

import io.grpc.ManagedChannelBuilder
import org.bson.types.ObjectId
import schedule.model.Room
import schedule.model.enums.Feature
import org.springframework.stereotype.Component

@Component
class RoomRepository : RoomReader {
    private final val channel = ManagedChannelBuilder.forAddress("localhost", 8980).usePlaintext().build()
    val grpcRoomClient = GrpcRoomClient(channel)

    override fun findAllRooms(): List<Room> {
        return grpcRoomClient.getAllRooms()
    }

    //todo move it to the room system
    override fun searchRooms(features: List<Feature>?, minCapacity: Int, maxCapacity: Int): List<Room> {
        val allRooms = findAllRooms()
        val searchResults = arrayListOf<Room>()
        for (room in allRooms) {
            if (room.isCapacityFine(minCapacity, maxCapacity) && room.hasFeatures(features))
                searchResults.add(room)
        }
        return searchResults
    }

    //todo move it to the room system
    override fun findAllByIds(roomIds: List<ObjectId>): List<Room> {
        val rooms: List<Room> = findAllRooms()
        val searchResult = arrayListOf<Room>()
        for (room in rooms) {
            if (room.id in roomIds) searchResult.add(room)
        }
        return searchResult
    }


}

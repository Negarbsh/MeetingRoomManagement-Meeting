package schedule.dao.roomGrpc

import io.grpc.ManagedChannelBuilder
import schedule.model.Room
import schedule.model.enums.Feature
import org.springframework.stereotype.Component

@Component
class RoomRepository : RoomReader {
    private final val channel = ManagedChannelBuilder.forAddress("localhost", 8980).usePlaintext().build()
    val grpcRoomClient = GrpcRoomClient(channel)

    override fun getAllRooms(): List<Room> {
        return grpcRoomClient.getAllRooms()
    }

    //todo change this function to a query thing in database!
    override fun searchRooms(features: List<Feature>?, minCapacity: Int, maxCapacity: Int): List<Room> {
        val allRooms = getAllRooms()
        val searchResults = arrayListOf<Room>()
        for (room in allRooms) {
            if (room.isCapacityFine(minCapacity, maxCapacity) && room.hasFeatures(features))
                searchResults.add(room)
        }
        return searchResults
    }
}

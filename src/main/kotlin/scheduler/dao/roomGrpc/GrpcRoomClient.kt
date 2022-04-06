package scheduler.dao.roomGrpc

import io.grpc.ManagedChannel
import org.bson.types.ObjectId
import roomPackage.RoomGrpc
import roomPackage.RoomOuterClass
import roomPackage.RoomOuterClass.GetAllRoomsParam
import roomPackage.RoomOuterClass.RoomIdRequest
import roomPackage.RoomOuterClass.RoomInfo
import scheduler.model.Room
import scheduler.model.enums.Feature
import scheduler.model.enums.Office
import java.io.Closeable
import java.util.concurrent.TimeUnit


class GrpcRoomClient(private val channel: ManagedChannel) : Closeable {
    private val roomStub = RoomGrpc.newBlockingStub(channel)

    fun getAllRooms(): ArrayList<Room> {
        val request = GetAllRoomsParam.newBuilder().build()
        val response = roomStub.getAllRooms(request)
        val roomsList: List<RoomInfo> = response.roomsList
        val roomsArray = arrayListOf<Room>()
        for (roomInfo in roomsList) {
            val room = Room(
                ObjectId(roomInfo.id.hexString),
                roomInfo.name,
                Office.valueOf(roomInfo.office.name.uppercase()),
                getFeatures(roomInfo.featuresList),
                roomInfo.capacity
            )
            roomsArray.add(room)
        }
        return roomsArray
    }

    private fun getFeatures(featuresList: List<RoomOuterClass.Feature>): List<Feature> {
        val enumFeatures = arrayListOf<Feature>()
        for (featureMessage in featuresList) {
            enumFeatures.add(Feature.valueOf(featureMessage.name.uppercase()))
        }
        return enumFeatures
    }

    fun getCapacity(): Int {
        val request = RoomIdRequest.newBuilder().setId(123123).build()
        val response = roomStub.getCapacity(request)
        return response.capacity
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

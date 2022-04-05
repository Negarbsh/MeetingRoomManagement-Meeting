package scheduler.dao.grpc
import io.grpc.ManagedChannelBuilder

class RoomRepo {

    val channel = ManagedChannelBuilder.forAddress("localhost", 8980).usePlaintext().build()
//    val stub = RouteGuideCoroutineStub(channel)
}
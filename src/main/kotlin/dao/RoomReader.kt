package dao

import model.Room

interface RoomReader {
    fun getAllRooms(): List<Room>

}
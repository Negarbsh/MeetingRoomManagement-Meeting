package model

import model.enums.Feature
import model.enums.Office
import org.bson.types.ObjectId

class Room(val id: ObjectId, val name: String, val office: Office, val features: List<Feature>, val capacity: Int)
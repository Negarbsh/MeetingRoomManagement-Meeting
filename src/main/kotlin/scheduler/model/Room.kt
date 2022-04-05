package scheduler.model

import scheduler.model.enums.Feature
import scheduler.model.enums.Office
import org.bson.types.ObjectId

class Room(val id: ObjectId, val name: String, val office: Office, val features: List<Feature>, val capacity: Int)
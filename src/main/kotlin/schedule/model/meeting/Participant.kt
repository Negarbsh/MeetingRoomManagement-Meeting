package schedule.model.meeting

import com.fasterxml.jackson.annotation.JsonProperty

data class Participant (@JsonProperty("Email") val email: String)

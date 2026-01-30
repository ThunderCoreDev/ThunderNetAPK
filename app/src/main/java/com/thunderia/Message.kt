@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String? = null,
    val audioPath: String? = null,
    val imagePath: String? = null,
    val sender: String,
    val timestamp: Long
)
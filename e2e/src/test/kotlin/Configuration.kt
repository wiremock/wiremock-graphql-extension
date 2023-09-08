import java.util.*

object Configuration {
    var baseUrl: String

    init {
        Properties().apply {
            load(Configuration::class.java.classLoader.getResourceAsStream("config.properties"))
        }
            .let {
                baseUrl = it.getProperty("baseUrl")
            }
    }

}
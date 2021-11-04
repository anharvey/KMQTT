package mqtt.broker.cluster

import kotlin.random.Random

data class ClusterSettings(
    val name: String = Random.nextLong().toString(),
    val tcpPort: Int = 22222,
    val dnsDiscovery: Boolean = true,
    val dnsName: String = "kmqtt_broker",
	val broadcastAddress: String = "255.255.255.255",
    val discoveryAddress: String? = null,
    val discoveryPort: Int = 22223
)

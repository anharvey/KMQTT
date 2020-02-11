package mqtt

fun String.containsWildcard(): Boolean {
    return this.contains("#") || this.contains("+")
}

fun String.isValidTopic(): Boolean {
    if (this.isEmpty())
        return false

    if (this.contains("#")) {
        if (this.count { it.toString().contains("#") } > 1 || (this != "#" && !this.endsWith("/#")))
            return false
    }
    if (this.contains("+")) {
        for (i in 0 until length) {
            if (this[i] == '+') {
                val previousCharacter = this.getOrNull(i - 1)
                val nextCharacter = this.getOrNull(i + 1)

                if (!((previousCharacter == '/' || previousCharacter == null) && (nextCharacter == '/' || nextCharacter == null)))
                    return false
            }
        }
    }

    return true
}

fun String.matchesWildcard(wildcardTopic: String): Boolean {
    if (this.containsWildcard())
        return false
    if (!this.isValidTopic() || !wildcardTopic.isValidTopic())
        return false

    // The Server MUST NOT match Topic Filters starting with a wildcard character (# or +) with Topic Names beginning with a $ character
    if ((wildcardTopic.startsWith("+") || wildcardTopic.startsWith("#")) && this.startsWith("$"))
        return false

    if (this == wildcardTopic) {
        return true
    }

    var positionTopic = 0
    var positionTopicFilter = 0
    while (positionTopic < this.length && positionTopicFilter < wildcardTopic.length) {
        if (this[positionTopic] == wildcardTopic[positionTopicFilter]) {
            positionTopic++
            positionTopicFilter++
            continue
        } else if (wildcardTopic[positionTopicFilter] != '#' && wildcardTopic[positionTopicFilter] != '+') {
            break
        }

        if (wildcardTopic[positionTopicFilter] == '#') {
            return true
        } else if (wildcardTopic[positionTopicFilter] == '+') {
            while (positionTopic < this.length && this[positionTopic] != '/') {
                positionTopic++
            }
            when {
                positionTopic == this.length -> return true
                wildcardTopic.getOrNull(positionTopicFilter + 1) == '/' -> positionTopicFilter++
                else -> return false
            }
        }
    }

    return positionTopic == this.length && positionTopicFilter == wildcardTopic.length
}

fun String.isSharedTopicFilter(): Boolean {
    val split = this.split("/")
    if (split.size < 3)
        return false
    if (split[0] == "\$share" && split[1].isNotEmpty() && !split[1].contains("+") && !split[1].contains("#") && this.substringAfter(
            split[1] + "/"
        ).isValidTopic()
    )
        return true
    return false
}

fun String.getSharedTopicFilter(): String? {
    if (isSharedTopicFilter()) {
        val split = this.split("/")
        return this.substringAfter(split[1] + "/")
    }
    return null
}

fun String.getSharedTopicShareName(): String? {
    if (isSharedTopicFilter()) {
        val split = this.split("/")
        return split[1]
    }
    return null
}

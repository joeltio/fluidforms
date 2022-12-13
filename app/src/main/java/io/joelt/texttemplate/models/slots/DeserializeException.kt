package io.joelt.texttemplate.models.slots

class DeserializeException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

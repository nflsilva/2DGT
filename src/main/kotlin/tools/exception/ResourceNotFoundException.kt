package tools.exception

class ResourceNotFoundException: Exception {
    constructor() : super()
    constructor(resourceName: String) : super("Resource with path '${resourceName}' was not found.")
    constructor(resourceName: String, cause: Throwable) : super("Resource with path '${resourceName}' was not found.", cause)
    constructor(cause: Throwable) : super(cause)
}
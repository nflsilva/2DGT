package tools.exception

class ErrorLoadingResourceException: Exception {
    constructor() : super()
    constructor(resourceName: String) :
            super("There was an error loading resource at '${resourceName}'.")
    constructor(resourceName: String, cause: Throwable) :
            super("There was an error loading resource at '${resourceName}'.", cause)
    constructor(cause: Throwable) : super(cause)
}
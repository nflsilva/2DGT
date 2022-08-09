package tools

class Log {
    companion object {
        fun d(data: String) {
            println("[DEBUG] $data")
        }
        fun i(data: String) {
            println("[INFO] $data")
        }
        fun w(data: String) {
            println("[WARNING] $data")
        }
        fun e(data: String) {
            println("[ERROR] $data")
        }
    }
}
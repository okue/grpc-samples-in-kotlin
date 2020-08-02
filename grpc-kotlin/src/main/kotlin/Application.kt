package org.okue

import mu.KotlinLogging

class Application {
    fun main() {
        log.info("Starting...")
    }

    companion object {
        private val log = KotlinLogging.logger {}
    }
}

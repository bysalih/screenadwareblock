
package com.screenadwareblock

object VirusDatabase {
    private val virusApps = mutableSetOf(
        "com.fake.cleaner",
        "com.ads.camera.booster",
        "com.loud.ads.game"
    )

    fun isInfected(packageName: String): Boolean {
        return virusApps.contains(packageName)
    }

    fun updateDatabaseFromServer(newList: List<String>) {
        virusApps.clear()
        virusApps.addAll(newList)
    }
}

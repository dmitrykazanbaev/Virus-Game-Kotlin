package com.dmitrykazanbaev.virus_game.model.dao

import io.realm.RealmList
import io.realm.RealmObject


open class FirstLevelDAO : RealmObject() {
    var buildingList = RealmList<BuildingDAO>()
}

open class BuildingDAO(var infectedComputers: Int = 0) : RealmObject()

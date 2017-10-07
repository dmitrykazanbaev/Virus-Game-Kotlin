package com.dmitrykazanbaev.virus_game

import com.dmitrykazanbaev.virus_game.screen.FirstLevelView
import com.dmitrykazanbaev.virus_game.service.*
import java.util.*


class FirstLevelActivity : AbstractLevelActivity() {
    override val levelView by lazy { FirstLevelView(this) }

    override fun tryToInfectPhone(): Boolean {
        if (super.tryToInfectPhone())
            if (levelView.level.infectedPhones == 1) {
                levelView.coinButtonView.showCoin(115)
                return true
            }

        return false
    }

    override fun doSpecificLevelActions() {
        tryToAddCoin()
        tryToAddMessage()
        tryToAddDDoSAttackMinigame()
    }

    private fun tryToAddDDoSAttackMinigame() {
        val random = Random()
        val firstLevel = levelView.level
        if (random.nextInt(100) < 3 && !firstLevel.hasTechWorks && firstLevel.antivirusProgress < 100) {
            val infectedBuildings = firstLevel.buildings.filter { it.infectedComputers > 0 || it.infectedSmartHome > 0 }
            if (infectedBuildings.isNotEmpty()) {
                infectedBuildings[random.nextInt(infectedBuildings.size)].addTechWork()
            }
        }
    }

    private fun tryToAddMessage() {
        val random = Random()
        if (random.nextInt(100) < 20) {
            val notShownMessages = levelView.messageList.filter { !it.isShown }
            if (notShownMessages.isNotEmpty()) {
                val message = notShownMessages.first()

                val firstLevel = levelView.level

                val listPossibleMessages = mutableListOf<String>()
                listPossibleMessages.add(getNormalMessage(this))

                if (firstLevel.infectedSmartHome > 0 || firstLevel.infectedComputers > 0) {
                    listPossibleMessages.add(getStartVirusMessage(this))

                    if (user.virus.abilities.thief.currentLevel == 3)
                        listPossibleMessages.add(getTheftBankMessage(this))
                    if (user.virus.abilities.control.currentLevel >= 1)
                        listPossibleMessages.add(getAppAccessMessage(this))
                    if (user.virus.abilities.control.currentLevel >= 2)
                        listPossibleMessages.add(getSMSMessage(this))
                    if (user.virus.abilities.control.currentLevel == 3)
                        listPossibleMessages.add(getMoneyTransferMessage(this))
                    if (user.virus.abilities.spam.currentLevel >= 1)
                        listPossibleMessages.add(getBlockAdMessage(this))
                    if (user.virus.abilities.spam.currentLevel >= 2)
                        listPossibleMessages.add(getBannerMessage(this))
                    if (user.virus.abilities.spam.currentLevel == 3)
                        listPossibleMessages.add(getPornoBannerMessage(this))
                }

                val randomIndex = random.nextInt(listPossibleMessages.size)
                if (randomIndex != 0)
                    message.showAtInfected(listPossibleMessages[randomIndex])
                else message.showAtRandom(listPossibleMessages[randomIndex])
            }
        }
    }

    private fun tryToAddCoin() {
        val random = Random()
        if (random.nextInt(1000) < 10 && levelView.level.infectedPhones > 1)
            levelView.coinButtonView.showCoin(random.nextInt(50) + 20)
    }

    override fun onDestroy() {
        levelView.level.infectedPhoneThread.quit()
        super.onDestroy()
    }
}

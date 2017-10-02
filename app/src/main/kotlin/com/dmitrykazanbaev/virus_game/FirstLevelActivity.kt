package com.dmitrykazanbaev.virus_game

import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.custom_views.TrapezeButton
import com.dmitrykazanbaev.virus_game.model.User
import com.dmitrykazanbaev.virus_game.model.dao.ServiceInformationDAO
import com.dmitrykazanbaev.virus_game.model.dao.UserDAO
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel
import com.dmitrykazanbaev.virus_game.screen.FirstLevelView
import com.dmitrykazanbaev.virus_game.service.*
import io.realm.Realm
import kotlinx.android.synthetic.main.first_level_activity.*
import kotlinx.coroutines.experimental.*
import java.text.SimpleDateFormat
import java.util.*


class FirstLevelActivity : AppCompatActivity() {
    val firstLevelView by lazy { FirstLevelView(this) }
    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.US)
    private val calendar = GregorianCalendar()

    private var tickJob: Job? = null
    private var drawJob: Job? = null

    val user by lazy { User() }

    private fun startJobs() {
        initDrawJob()
        initTickJob()
    }

    private suspend fun stopJobs() {
        drawJob?.cancel()
        tickJob?.cancel()

        drawJob?.join()
        tickJob?.join()
    }

    private fun initDrawJob() {
        if (drawJob == null || drawJob?.isCompleted!!) {
            drawJob = launch(CommonPool) {
                var canvas: Canvas?
                while (isActive) {
                    canvas = firstLevelView.holder.lockCanvas()

                    synchronized(firstLevelView.holder) {
                        canvas?.let { firstLevelView.drawLevel(it) }
                    }

                    canvas?.let { firstLevelView.holder.unlockCanvasAndPost(it) }
                }
            }
        }
    }

    private fun initTickJob() {
        if (tickJob == null || tickJob?.isCompleted!!) {
            tickJob = launch(CommonPool) {
                while (isActive) {
                    checkWinOrLose()
                    tryToInfectPhone(firstLevelView.level as FirstLevel)
                    tryToInfectComputer(firstLevelView.level as FirstLevel)
                    tryToInfectSmartHome(firstLevelView.level as FirstLevel)
                    tryToDetectVirus(firstLevelView.level as FirstLevel)
                    tryToProgressAntivirus(firstLevelView.level as FirstLevel)
                    tryToCureDevices(firstLevelView.level as FirstLevel)
                    tryToAddCoin()
                    tryToAddMessage()
                    updateDate()
                    delay(500)
                }
            }
        }
    }

    private fun tryToAddMessage() {
        val random = Random()
        if (random.nextInt(100) < 20) {
            val notShownMessages = firstLevelView.messageList.filter { !it.isShown }
            if (notShownMessages.isNotEmpty()) {
                val message = notShownMessages.first()

                val firstLevel = firstLevelView.level as FirstLevel

                val listPossibleMessages = mutableListOf<String>()
                listPossibleMessages.add(getNormalMessage())

                if (firstLevel.infectedSmartHome > 0 || firstLevel.infectedComputers > 0) {
                    listPossibleMessages.add(getStartVirusMessage())

                    if (user.virus.abilities.thief.currentLevel == 3)
                        listPossibleMessages.add(getTheftBankMessage())
                    if (user.virus.abilities.control.currentLevel >= 1)
                        listPossibleMessages.add(getAppAccessMessage())
                    if (user.virus.abilities.control.currentLevel >= 2)
                        listPossibleMessages.add(getSMSMessage())
                    if (user.virus.abilities.control.currentLevel == 3)
                        listPossibleMessages.add(getMoneyTransferMessage())
                    if (user.virus.abilities.spam.currentLevel >= 1)
                        listPossibleMessages.add(getBlockAdMessage())
                    if (user.virus.abilities.spam.currentLevel >= 2)
                        listPossibleMessages.add(getBannerMessage())
                    if (user.virus.abilities.spam.currentLevel == 3)
                        listPossibleMessages.add(getPornoBannerMessage())
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
        if (random.nextInt(1000) < 10 && (firstLevelView.level as FirstLevel).infectedPhones > 1)
            firstLevelView.coinButtonView.showCoin(random.nextInt(50) + 20)
    }

    private fun checkWinOrLose() {
        val firstLevel = firstLevelView.level as FirstLevel

        if (firstLevel.infectedPhones == firstLevel.phones &&
                firstLevel.infectedComputers == firstLevel.computers &&
                firstLevel.infectedSmartHome == firstLevel.infectedSmartHome)

            runOnUiThread { statistics_button.text = "  WIN" }

        if (firstLevel.antivirusProgress == 100 && firstLevel.infectedPhones == 0 &&
                firstLevel.infectedComputers == 0 && firstLevel.infectedSmartHome == 0)

            runOnUiThread { statistics_button.text = "  LOSE" }

    }

    private fun updateDate() {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        runOnUiThread { date_button?.text = "  ${dateFormat.format(calendar.time)}" }
    }

    fun updateBalance(newBalance: Int) {
        runOnUiThread {
            balance_button.text = "  $newBalance$"
            characteristic_window_balance.text = "$newBalance$"
        }
    }

    fun updateModificationTitle(title: String) {
        modification_name.text = title
    }

    fun updateModificationDescription(description: String) {
        modification_description.text = description
    }

    fun updateModificationUpgradeCost(cost: Int) {
        modification_cost.text = "$cost$"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_level_activity)

        ApplicationContextHolder.context = this

        user.balance = 10000
        user.virus.synchronize()

        Realm.init(this)

        firstLevelView.holder.addCallback(firstLevelView)

        mainframe.addView(firstLevelView, 0)

        horizontal_scroll_view_background.setOnTouchListener { _, _ -> true }
        vertical_scroll_view_background.setOnTouchListener { _, _ -> true }

        characteristic_window.visibility = View.INVISIBLE

        radiogroup.setOnCheckedChangeListener { group, viewId ->
            buy_modification_window.visibility = View.INVISIBLE
            updateButtonColor(group)
            updateModificationButtonController(viewId)
        }

        date_button.text = "  ${dateFormat.format(calendar.time)}"

        if (!intent.getBooleanExtra("new_game", false))
            initFromRealm()
    }

    override fun onResume() {
        startJobs()
        super.onResume()
    }

    override fun onPause() {
        runBlocking { stopJobs() }
        super.onPause()
    }

    override fun onStop() {
        saveToRealm()
        super.onStop()
    }

    override fun onDestroy() {
        (firstLevelView.level as FirstLevel).infectedPhoneThread.quit()
        super.onDestroy()
    }

    private fun initFromRealm() {
        firstLevelView.initLevelFromRealm()
        initUserFromRealm()
        initServiceInfoFromRealm()
    }

    private fun initUserFromRealm() {
        val realm = Realm.getDefaultInstance()

        val userDAO = realm.where(UserDAO::class.java).findFirst()
        user.balance = userDAO.balance
        user.virus.propagation.wifi.currentLevel = userDAO.wifiLevel
        user.virus.propagation.bluetooth.currentLevel = userDAO.bluetoothLevel
        user.virus.propagation.ethernet.currentLevel = userDAO.ethernetLevel
        user.virus.propagation.mobile.currentLevel = userDAO.mobileLevel
        user.virus.abilities.thief.currentLevel = userDAO.thiefLevel
        user.virus.abilities.control.currentLevel = userDAO.controlLevel
        user.virus.abilities.spam.currentLevel = userDAO.spamLevel
        user.virus.resistance.invisible.currentLevel = userDAO.invisibleLevel
        user.virus.resistance.mask.currentLevel = userDAO.maskLevel
        user.virus.resistance.newVirus.currentLevel = userDAO.newVirusLevel
        user.virus.devices.phone.currentLevel = userDAO.phoneLevel
        user.virus.devices.pc.currentLevel = userDAO.pcLevel
        user.virus.devices.smartHome.currentLevel = userDAO.smartHomeLevel

        user.virus.synchronize()
    }

    private fun initServiceInfoFromRealm() {
        val realm = Realm.getDefaultInstance()

        val serviceInfo = realm.where(ServiceInformationDAO::class.java).findFirst()
        calendar.time = serviceInfo.date
    }

    private fun saveToRealm() {
        firstLevelView.saveLevelToRealm()
        saveUserToRealm()
        saveServiceInfoToRealm()
    }

    private fun saveUserToRealm() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            it.where(UserDAO::class.java).findAll().deleteAllFromRealm()

            val userDAO = UserDAO(user.balance,
                    user.virus.propagation.wifi.currentLevel,
                    user.virus.propagation.bluetooth.currentLevel,
                    user.virus.propagation.ethernet.currentLevel,
                    user.virus.propagation.mobile.currentLevel,
                    user.virus.abilities.thief.currentLevel,
                    user.virus.abilities.control.currentLevel,
                    user.virus.abilities.spam.currentLevel,
                    user.virus.resistance.invisible.currentLevel,
                    user.virus.resistance.mask.currentLevel,
                    user.virus.resistance.newVirus.currentLevel,
                    user.virus.devices.phone.currentLevel,
                    user.virus.devices.pc.currentLevel,
                    user.virus.devices.smartHome.currentLevel)

            it.copyToRealm(userDAO)
        }
    }

    private fun saveServiceInfoToRealm() {
        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            it.where(ServiceInformationDAO::class.java).findAll().deleteAllFromRealm()

            val serviceInfo = ServiceInformationDAO(calendar.time)

            it.copyToRealm(serviceInfo)
        }
    }

    fun onTouch(view: View) {
        when (view.id) {
            R.id.virus_button -> {
                runBlocking {
                    stopJobs()
                }
                showCharacteristicWindow()
            }
            R.id.close_characteristics_button -> {
                startJobs()
                closeCharacteristicWindow()
            }
            R.id.devices_button, R.id.propagation_button,
            R.id.resistance_button, R.id.abilities_button -> if (!(view as TrapezeButton).isChecked) view.isChecked = true
            R.id.buy_button -> buyModification()
        }
    }

    private fun updateButtonColor(group: RadioGroup) {
        (0 until group.childCount).
                map { i -> group.getChildAt(i) }.
                forEach {
                    val button = it as TrapezeButton
                    if (button.isChecked) {
                        button.buttonPaint.color = button.selectedColor
                    } else button.buttonPaint.color = button.unselectedColor
                    button.invalidate()
                }
    }

    override fun onBackPressed() {
        if (characteristic_window.visibility == View.VISIBLE)
            closeCharacteristicWindow()
    }

    private fun updateModificationButtonController(viewId: Int) {
        modification_controller.removeAllViews()

        val modificationButtonController = ModificationButtonController(this, sourceViewId = viewId)
        modificationButtonController.layoutParams =
                RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)
        modificationButtonController.setOnCheckedChangeListener { radioGroup, _ -> radioGroup.invalidate() }
        modification_controller.addView(modificationButtonController)
    }
}

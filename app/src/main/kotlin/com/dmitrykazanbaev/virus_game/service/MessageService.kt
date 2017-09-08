package com.dmitrykazanbaev.virus_game.service

import com.dmitrykazanbaev.virus_game.R
import java.util.*


fun getNormalMessage(): String {
    return getMessage(R.array.msgs_without_virus)
}

fun getStartVirusMessage(): String {
    return getMessage(R.array.msgs_start_virus)
}

fun getTheftBankMessage(): String {
    return getMessage(R.array.msgs_theft_bank)
}

fun getAppAccessMessage(): String {
    return getMessage(R.array.msgs_app_access)
}

fun getSMSMessage(): String {
    return getMessage(R.array.msgs_sms)
}

fun getMoneyTransferMessage(): String {
    return getMessage(R.array.msgs_money_transfer)
}

fun getBlockAdMessage(): String {
    return getMessage(R.array.msgs_block_ad)
}

fun getBannerMessage(): String {
    return getMessage(R.array.msgs_banner)
}

fun getPornoBannerMessage(): String {
    return getMessage(R.array.msgs_porno_banner)
}

private fun getMessage(resource: Int): String {
    with(ApplicationContextHolder.context) {
        val messages = resources.getStringArray(resource)
        return messages[Random().nextInt(messages.size)]
    }
}
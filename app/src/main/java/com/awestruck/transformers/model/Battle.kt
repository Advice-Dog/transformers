package com.awestruck.transformers.model

import androidx.annotation.VisibleForTesting
import com.awestruck.transformers.model.Transformer.Companion.COURAGE
import com.awestruck.transformers.model.Transformer.Companion.RANK
import com.awestruck.transformers.model.Transformer.Companion.SKILL
import com.awestruck.transformers.model.Transformer.Companion.STRENGTH
import com.awestruck.transformers.util.MASS_EXTINCTION
import com.awestruck.transformers.util.NO_WINNER
import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON

/**
 * Created by Chris on 2018-09-30.
 */
class Battle(transformers: List<Transformer>) {

    val autobots = transformers.filter { it.isAutobot }.sortedByDescending { it[RANK] }
    val decepticons = transformers.filter { it.isDecepticon }.sortedByDescending { it[RANK] }

    val battles = Math.min(autobots.size, decepticons.size)

    val victor: String

    val results = ArrayList<BattleResult>()

    init {

        var autobotsDestroyed = 0
        var decepticonsDestroyed = 0

        for (i in 0 until battles) {
            val autobot = autobots[i]
            val decepticon = decepticons[i]

            val winner = getWinner(autobot, decepticon)

            when (winner) {
                TEAM_AUTOBOT -> decepticonsDestroyed++
                TEAM_DECEPTICON -> autobotsDestroyed++
                NO_WINNER -> {
                    autobotsDestroyed++
                    decepticonsDestroyed++
                }

                MASS_EXTINCTION -> {
                    // TODO: Handle this.
                }
            }

            results.add(BattleResult(autobot, decepticon, winner))

        }

        victor = when {
            autobotsDestroyed < decepticonsDestroyed -> TEAM_AUTOBOT
            autobotsDestroyed > decepticonsDestroyed -> TEAM_DECEPTICON
            else -> NO_WINNER
        }
    }


    companion object {
        @VisibleForTesting
        fun getWinner(autobot: Transformer, decepticon: Transformer): String {
            if (autobot.isLeader() || decepticon.isLeader()) {
                if (autobot.isLeader() && decepticon.isLeader())
                    return MASS_EXTINCTION

                if (autobot.isLeader())
                    return TEAM_AUTOBOT

                return TEAM_DECEPTICON
            }


            if (isIntimidating(autobot, decepticon))
                return TEAM_AUTOBOT

            if (isIntimidating(decepticon, autobot))
                return TEAM_DECEPTICON

            if (isOverpowering(autobot, decepticon))
                return TEAM_AUTOBOT

            if (isOverpowering(decepticon, autobot))
                return TEAM_DECEPTICON


            if (equalPower(autobot, decepticon))
                return NO_WINNER

            return if (isStronger(autobot, decepticon))
                TEAM_AUTOBOT
            else
                TEAM_DECEPTICON
        }

        @VisibleForTesting
        fun isOverpowering(lhs: Transformer, rhs: Transformer) = lhs[STRENGTH] >= rhs[STRENGTH] + 3

        @VisibleForTesting
        fun isIntimidating(lhs: Transformer, rhs: Transformer) = lhs[COURAGE] - 4 >= rhs[COURAGE]

        @VisibleForTesting
        fun isTooSkilled(lhs: Transformer, rhs: Transformer) = lhs[SKILL] - 3 >= rhs[SKILL]

        @VisibleForTesting
        fun equalPower(lhs: Transformer, rhs: Transformer) = lhs.total == rhs.total

        @VisibleForTesting
        fun isStronger(lhs: Transformer, rhs: Transformer) = lhs.total > rhs.total
    }
}
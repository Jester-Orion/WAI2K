/*
 * GPLv3 License
 *
 *  Copyright (c) WAI2K by waicool20
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.waicool20.wai2k.config

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.waicool20.wai2k.Wai2k
import com.waicool20.wai2k.game.GFL
import com.waicool20.waicoolutils.javafx.json.fxJacksonObjectMapper
import com.waicool20.waicoolutils.javafx.toProperty
import com.waicool20.waicoolutils.logging.loggerFor
import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import tornadofx.*
import java.nio.file.Path
import java.time.Duration
import java.time.LocalTime
import kotlin.io.path.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Wai2kProfile(
    val logistics: Logistics = Logistics(),
    val autoBattle: AutoBattle = AutoBattle(),
    val combat: Combat = Combat(),
    val combatReport: CombatReport = CombatReport(),
    val combatSimulation: CombatSimulation = CombatSimulation(),
    val factory: Factory = Factory(),
    val stop: Stop = Stop()
) {
    data class DollCriteria(var id: String = "Gr G11")

    class Logistics(
        enabled: Boolean = false,
        receivalMode: ReceivalMode = ReceivalMode.RANDOM,
        assignments: MutableMap<Int, ListProperty<Int>> = (1..GFL.MAX_ECHELON).associateWith {
            SimpleListProperty(ArrayList<Int>().asObservable())
        }.toMutableMap()
    ) {
        enum class ReceivalMode {
            ALWAYS_CONTINUE, RANDOM, ALWAYS_CANCEL
        }

        val enabledProperty = enabled.toProperty()
        val assignmentsProperty = assignments.toProperty()
        val receiveModeProperty = receivalMode.toProperty()
        var assignments by assignmentsProperty
        var receiveMode by receiveModeProperty
        val enabled by enabledProperty
    }

    class AutoBattle(
        enabled: Boolean = true
    ) {
        val enabledProperty = enabled.toProperty()
        val enabled by enabledProperty
    }

    class Combat(
        enabled: Boolean = false,
        map: String = "0-2",
        repairThreshold: Int = 40,
        battleTimeout: Int = 45,
        draggerSlot: Int = 2,
        draggers: MutableList<DollCriteria> = mutableListOf(DollCriteria(), DollCriteria())
    ) {
        val enabledProperty = enabled.toProperty()
        val mapProperty = map.toProperty()
        val repairThresholdProperty = repairThreshold.toProperty()
        val battleTimeoutProperty = battleTimeout.toProperty()
        val draggerSlotProperty = draggerSlot.toProperty()
        val draggersProperty = draggers.toProperty()

        val enabled by enabledProperty
        val map by mapProperty
        val repairThreshold by repairThresholdProperty
        val battleTimeout by battleTimeoutProperty
        val draggerSlot by draggerSlotProperty
        val draggers by draggersProperty
    }

    class CombatReport(enabled: Boolean = false, type: Type = Type.SPECIAL) {
        enum class Type {
            NORMAL, SPECIAL
        }

        val enabledProperty = enabled.toProperty()
        val typeProperty = type.toProperty()

        val enabled by enabledProperty
        val type by typeProperty
    }

    class CombatSimulation(
        enabled: Boolean = false,
        dataSim: Level = Level.ADVANCED,
        neuralFragment: Level = Level.ADVANCED,
        neuralEchelon: Int = 6,
        coalition: Coalition = Coalition()
    ) {
        enum class Level {
            OFF, BASIC, INTERMEDIATE, ADVANCED;

            val cost = ordinal
        }

        class Coalition(
            enabled: Boolean = false,
            preferredType: Type = Type.RANDOM
        ) {
            enum class Type {
                RANDOM, EXPDISKS, PETRIDISH, DATACHIPS
            }

            val enabledProperty = enabled.toProperty()
            val preferredTypeProperty = preferredType.toProperty()

            val enabled by enabledProperty
            val preferredType by preferredTypeProperty
        }

        val enabledProperty = enabled.toProperty()
        val dataSimProperty = dataSim.toProperty()
        val neuralFragmentProperty = neuralFragment.toProperty()
        val neuralEchelonProperty = neuralEchelon.toProperty()
        val coalitionProperty = coalition.toProperty()

        val enabled by enabledProperty
        val dataSim by dataSimProperty
        val neuralFragment by neuralFragmentProperty
        val neuralEchelon by neuralEchelonProperty
        val coalition by coalitionProperty
    }

    class Factory(
        enhancement: Enhancement = Enhancement(),
        disassembly: Disassembly = Disassembly(),
        alwaysDisassembleAfterEnhance: Boolean = true,
        equipDisassembly: EquipDisassembly = EquipDisassembly()
    ) {
        class Enhancement(
            enabled: Boolean = true
        ) {
            val enabledProperty = enabled.toProperty()

            val enabled by enabledProperty
        }

        class Disassembly(
            enabled: Boolean = false,
            disassemble4Star: Boolean = false
        ) {
            val enabledProperty = enabled.toProperty()
            val disassemble4StarProperty = disassemble4Star.toProperty()

            val enabled by enabledProperty
            val disassemble4Star by disassemble4StarProperty
        }

        val enhancementProperty = enhancement.toProperty()
        val disassemblyProperty = disassembly.toProperty()
        val alwaysDisassembleAfterEnhanceProperty = alwaysDisassembleAfterEnhance.toProperty()

        val enhancement by enhancementProperty
        val disassembly by disassemblyProperty
        val alwaysDisassembleAfterEnhance by alwaysDisassembleAfterEnhanceProperty

        // Equipment ----------

        class EquipDisassembly(
            enabled: Boolean = true,
            disassemble4Star: Boolean = false
        ) {
            val enabledProperty = enabled.toProperty()
            val disassemble4StarProperty = disassemble4Star.toProperty()

            val enabled by enabledProperty
            val disassemble4Star by disassemble4StarProperty
        }

        val equipDisassemblyProperty = equipDisassembly.toProperty()

        val equipDisassembly by equipDisassemblyProperty
    }

    class Stop(
        enabled: Boolean = false,
        exitProgram: Boolean = false,
        time: Time = Time(),
        count: Count = Count()
    ) {
        class Time(
            enabled: Boolean = true,
            mode: Mode = Mode.ELAPSED_TIME,
            @JsonFormat(shape = JsonFormat.Shape.STRING)
            elapsedTime: Duration = Duration.ofHours(8),
            @JsonFormat(pattern = "HH:mm")
            specificTime: LocalTime = LocalTime.of(0, 0)
        ) {
            enum class Mode {
                ELAPSED_TIME, SPECIFIC_TIME
            }

            val enabledProperty = enabled.toProperty()
            val modeProperty = mode.toProperty()
            val elapsedTimeProperty = elapsedTime.toProperty()
            val specificTimeProperty = specificTime.toProperty()

            var enabled by enabledProperty
            var mode by modeProperty
            var elapsedTime by elapsedTimeProperty
            var specificTime by specificTimeProperty
        }

        class Count(
            enabled: Boolean = false,
            sorties: Int = 10
        ) {
            val enabledProperty = enabled.toProperty()
            val sortiesProperty = sorties.toProperty()

            var enabled by enabledProperty
            var sorties by sortiesProperty
        }

        val enabledProperty = enabled.toProperty()
        val exitProgramProperty = exitProgram.toProperty()
        val timeProperty = time.toProperty()
        val countProperty = count.toProperty()

        var enabled by enabledProperty
        var exitProgram by exitProgramProperty
        var time by timeProperty
        var count by countProperty
    }

    companion object Loader {
        private val loaderLogger = loggerFor<Loader>()
        private val mapper = fxJacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(JavaTimeModule())
        val PROFILE_DIR: Path = Wai2k.CONFIG_DIR.resolve("profiles")
        const val DEFAULT_NAME = "Default"

        fun profileExists(name: String) = PROFILE_DIR.resolve("$name.json").exists()

        fun load(name: String): Wai2kProfile {
            return load(PROFILE_DIR.resolve(
                "${name.takeIf { it.isNotBlank() } ?: DEFAULT_NAME}.json")
            ).also { it.name = name }
        }

        fun load(path: Path): Wai2kProfile {
            loaderLogger.info("Attempting to load profile")
            loaderLogger.debug("Profile path: $path")
            if (path.notExists()) {
                loaderLogger.info("Profile not found, creating empty file")
                PROFILE_DIR.createDirectories()
                path.createFile()
            }

            return try {
                mapper.readValue<Wai2kProfile>(path.toFile()).apply {
                    loaderLogger.info("Profile loaded")
                    name = path.nameWithoutExtension
                    printDebugInfo()
                }
            } catch (e: Exception) {
                loaderLogger.warn("Error occurred while loading the profile:", e)
                loaderLogger.info("Using default profile")
                Wai2kProfile().apply { save() }
            }
        }
    }

    private val logger = loggerFor<Wai2kProfile>()

    val nameProperty = DEFAULT_NAME.toProperty()
    @get:JsonIgnore var name by nameProperty

    @get:JsonIgnore
    val path: Path
        get() = PROFILE_DIR.resolve("$name.json")

    fun save(path: Path = this.path) {
        logger.info("Saving WAI2K profile")
        if (path.notExists()) {
            logger.debug("Profile not found, creating file $path")
            path.parent.createDirectories()
            path.createFile()
        }
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), this)
        logger.info("Profile saved!")
        printDebugInfo()
    }

    fun delete() {
        if (path.deleteIfExists()) {
            logger.info("Profile deleted")
            printDebugInfo()
        }
    }

    private fun printDebugInfo() {
        logger.debug("Profile path: $path")
        logger.debug("Profile: $this")
    }

    override fun toString(): String = mapper.writeValueAsString(this)
}

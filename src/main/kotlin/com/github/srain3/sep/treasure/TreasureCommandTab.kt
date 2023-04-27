package com.github.srain3.sep.treasure

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

object TreasureCommandTab: TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        if (command.name != "treasure") return null
        if (args.size <= 1) {
            return mutableListOf(
                "setting", "event"
            )
        } else {
            return when (args[0]) {
                "setting" -> {
                    if (args.size >= 3) {
                        return null
                    }
                    mutableListOf(
                        "start", "end", "info", "add", "remove"
                    )
                }

                "event" -> {
                    if (args.size >= 3) {
                        if (args[1] == "ranking") {
                            return when (args.size) {
                                3 -> {
                                    mutableListOf(
                                        "<NUM>"
                                    )
                                }

                                4 -> {
                                    mutableListOf(
                                        "true", "false"
                                    )
                                }

                                else -> {
                                    null
                                }
                            }
                        } else if (args[1] == "radar") {
                            return when (args.size) {
                                3 -> {
                                    mutableListOf(
                                        "<0～3>"
                                    )
                                }

                                else -> {
                                    null
                                }
                            }
                        } else {
                            return null
                        }
                    }
                    mutableListOf(
                        "start", "stop", "ranking", "radar"
                    )
                }

                else -> {
                    null
                }
            }
        }
    }
}
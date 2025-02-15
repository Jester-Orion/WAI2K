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

package com.waicool20.wai2k.views.tabs

import com.waicool20.waicoolutils.DesktopUtils
import com.waicool20.waicoolutils.javafx.CoroutineScopeView
import javafx.scene.control.Hyperlink
import javafx.scene.layout.VBox


class WelcomeTabView : CoroutineScopeView() {
    override val root: VBox by fxml("/views/tabs/welcome.fxml")
    private val wiki: Hyperlink by fxid()
    private val discord: Hyperlink by fxid()

    init {
        title = "Welcome"
    }

    override fun onDock() {
        super.onDock()
        wiki.setOnAction { DesktopUtils.browse("https://github.com/waicool20/WAI2K/wiki") }
        discord.setOnAction { DesktopUtils.browse("https://discord.gg/2tt5Der") }
    }
}

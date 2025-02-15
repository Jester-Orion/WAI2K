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

package com.waicool20.wai2k.views.tabs.profile

import com.waicool20.wai2k.Wai2k
import com.waicool20.wai2k.events.EventBus
import com.waicool20.wai2k.events.ProfileUpdateEvent
import com.waicool20.wai2k.views.ViewNode
import com.waicool20.waicoolutils.javafx.CoroutineScopeView
import com.waicool20.waicoolutils.javafx.addListener
import javafx.geometry.Pos
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.controlsfx.control.MasterDetailPane
import tornadofx.*

class ProfileTabView : CoroutineScopeView() {
    override val root: HBox by fxml("/views/tabs/profile/profile-tab.fxml")
    private val profileTreeView: TreeView<String> by fxid()
    private val profilePane: MasterDetailPane by fxid()

    private val defaultMasterNode = hbox(alignment = Pos.CENTER) {
        label("Choose something to configure on the left!")
    }

    private val defaultDetailsNode = hbox(alignment = Pos.CENTER) {
        //label("Go away, don't look ヽ( `д´*)ノ")
        label(graphic = ImageView(Image("images/wa-pudding.png")))
    }

    init {
        title = "Profile"
        profilePane.masterNode = defaultMasterNode
        profilePane.detailNode = defaultDetailsNode
    }

    override fun onDock() {
        super.onDock()
        EventBus.subscribe<ProfileUpdateEvent>()
            .onEach { profileTreeView.root.valueProperty().bind(Wai2k.profile.nameProperty) }
            .launchIn(this)
        initializeTree()
    }

    fun initializeTree() {
        profileTreeView.root = TreeItem<String>().apply {
            valueProperty().bind(Wai2k.profile.nameProperty)
            isExpanded = true
        }
        ProfileViewMappings.list.forEach { node ->
            node.isExpanded = true
            if (node.parent == null) {
                profileTreeView.root.children.add(node)
            } else {
                ProfileViewMappings.list.find { it.masterView == node.parent }?.children?.add(node)
            }
        }
        profileTreeView.focusModel.focusedItemProperty().addListener("ProfileFocused") { newVal ->
            profilePane.apply {
                if (newVal is ViewNode) {
                    val pos = profilePane.dividerPosition
                    masterNode = find(newVal.masterView).root
                    detailNode = newVal.detailsView?.let { find(it).root } ?: defaultDetailsNode
                    dividerPosition = pos
                    isAnimated = true
                    isShowDetailNode = true
                } else {
                    masterNode = defaultMasterNode
                    detailNode = defaultDetailsNode
                    isAnimated = false
                    isShowDetailNode = false
                }
            }
        }
    }
}

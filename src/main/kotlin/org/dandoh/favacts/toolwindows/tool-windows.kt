package org.dandoh.favacts.toolwindows

import com.intellij.ide.DataManager
import org.dandoh.favacts.actions.AddActionToFavoritesAction
import com.intellij.ide.util.gotoByName.GotoActionModel.defaultActionForeground
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.ui.CollectionListModel
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ReorderableListController
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.UIUtil
import com.jetbrains.rd.swing.mouseClicked
import org.dandoh.favacts.actions.DeleteActionFromFavoritesAction
import org.dandoh.favacts.services.ActionId
import org.dandoh.favacts.services.FavoriteActionsService
import org.dandoh.favacts.ui.ActionItemForm
import org.dandoh.favacts.ui.FavoriteActionsToolWindowForm
import org.dandoh.favacts.utils.invokeAction
import org.dandoh.favacts.utils.logIDE
import org.dandoh.favacts.utils.updateUI
import java.awt.Component
import java.awt.event.*
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.SwingConstants


class ActionRenderer() : ListCellRenderer<ActionId> {
  override fun getListCellRendererComponent(list: JList<out ActionId>,
                                            actionId: ActionId, index: Int,
                                            isSelected: Boolean, cellHasFocus: Boolean): Component {
    val action = ActionManager.getInstance().getAction(actionId)
    val ui = ActionItemForm()
    val bg = UIUtil.getListBackground(isSelected, cellHasFocus)
    val fg = UIUtil.getListForeground(isSelected, cellHasFocus)

    ui.content.isOpaque = true
    ui.content.background = bg
    ui.actionIcon.icon = action.templatePresentation.icon
    val shortcuts = KeymapUtil.getActiveKeymapShortcuts(actionId)
    val shortcutText = KeymapUtil.getPreferredShortcutText(shortcuts.shortcuts)
    val str = action.templateText ?: ""
    ui.actionName.background = bg
    ui.actionName.append(str, SimpleTextAttributes(STYLE_PLAIN, fg))
    val groupFg = if (isSelected) UIUtil.getListSelectionForeground(true) else UIUtil.getInactiveTextColor()
    if (shortcutText.isNotEmpty()) {
      val style = SimpleTextAttributes.STYLE_SMALLER or
          SimpleTextAttributes.STYLE_BOLD
      ui.shortcut.append(" $shortcutText", SimpleTextAttributes(style, groupFg))
    }


    // Handle events

    return ui.content
  }

}

class FavoriteActionsToolWindow(project: Project) : FavoriteActionsService.Listener {

  val ui = FavoriteActionsToolWindowForm()

  init {
    val favoriteActionsService =
        ServiceManager.getService(project, FavoriteActionsService::class.java)
    with(ui.actionList) {
      model = CollectionListModel(favoriteActionsService.getActionIds())
      cellRenderer = ActionRenderer()
    }

    ui.actionList.addKeyListener(object : KeyAdapter() {
      override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
          KeyEvent.VK_ENTER -> invokeAction(e, getSelectedActionId())
          KeyEvent.VK_DELETE -> invokeAction(e, DeleteActionFromFavoritesAction())
        }
      }
    })
    ui.actionList.addMouseListener(object : MouseAdapter() {
      override fun mouseClicked(e: MouseEvent) {
        if (e.clickCount >= 2) {
          invokeAction(e, getSelectedActionId())
        }
      }
    })
    favoriteActionsService.addListener(this)

  }

  fun getSelectedActionId(): ActionId? {
    return ui.actionList.selectedValue
  }


  override fun actionListChange(newActionIds: List<ActionId>) {
    updateUI {
      ui.actionList.model = CollectionListModel(newActionIds)
      ui.actionList.revalidate()
    }
  }
}


class FavoriteActionsToolWindowFactory : ToolWindowFactory, DumbAware {
  companion object {
    var favoriteActionsToolWindow: FavoriteActionsToolWindow? = null
  }

  /**
   * Only call once
   */
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val myToolWindow = FavoriteActionsToolWindow(project)
    favoriteActionsToolWindow = myToolWindow
    val contentFactory = ContentFactory.SERVICE.getInstance()
    val content = contentFactory.createContent(myToolWindow.ui.content, "", false)
    toolWindow.contentManager.addContent(content);
    when (toolWindow) {
      is ToolWindowEx -> {
        toolWindow.setTitleActions(AddActionToFavoritesAction(), DeleteActionFromFavoritesAction())
      }
    }
  }
}

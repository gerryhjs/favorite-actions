package toolwindows

import actions.AddActionToFavoritesAction
import com.intellij.ide.util.gotoByName.GotoActionModel.defaultActionForeground
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.openapi.wm.ex.ToolWindowManagerEx
import com.intellij.ui.CollectionListModel
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import services.ActionId
import services.FavoriteActionsService
import utils.cutName
import utils.updateUI
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer


class ActionRenderer : ListCellRenderer<ActionId> {
  override fun getListCellRendererComponent(list: JList<out ActionId>,
                                            actionId: ActionId, index: Int,
                                            isSelected: Boolean, cellHasFocus: Boolean): Component {
    val action = ActionManager.getInstance().getAction(actionId)
    val panel = JPanel(BorderLayout())
    val bg = UIUtil.getListBackground(isSelected, cellHasFocus)
    val nameComponent = SimpleColoredComponent()
    panel.add(nameComponent, BorderLayout.CENTER)
    panel.add(JBLabel(action.templatePresentation.icon), BorderLayout.WEST)
    panel.border = JBUI.Borders.empty(2)
    panel.isOpaque = true
    panel.background = bg
    val shortcuts = KeymapUtil.getActiveKeymapShortcuts(actionId)
    val shortcutText = KeymapUtil.getPreferredShortcutText(shortcuts.shortcuts)
    val str = cutName(action.templateText ?: "", shortcutText, list, panel, nameComponent)
    nameComponent.background = bg
    nameComponent.append(str, SimpleTextAttributes(STYLE_PLAIN,
        defaultActionForeground(isSelected, cellHasFocus, null)))
    val groupFg = if (isSelected) UIUtil.getListSelectionForeground(true) else UIUtil.getInactiveTextColor()
    if (shortcutText.isNotEmpty()) {
      nameComponent.append(" $shortcutText",
          SimpleTextAttributes(SimpleTextAttributes.STYLE_SMALLER or SimpleTextAttributes.STYLE_BOLD, groupFg))
    }
    return panel
  }

}

class FavoriteActionsToolWindow(project: Project) : FavoriteActionsService.Listener {


  val toolWindowPanel: SimpleToolWindowPanel
  private val actionList: JBList<ActionId>

  init {
    val favoriteActionsService =
        ServiceManager.getService(project, FavoriteActionsService::class.java)
    actionList = JBList(favoriteActionsService.getActionIds())
        .apply { cellRenderer = ActionRenderer() }
    toolWindowPanel = SimpleToolWindowPanel(true, false)
    toolWindowPanel.setContent(actionList)
    favoriteActionsService.addListener(this)
  }

  override fun actionListChange(newActionIds: List<ActionId>) {
    updateUI {
      actionList.model = CollectionListModel(newActionIds)
      actionList.revalidate()
    }
  }
}


class FavoriteActionsToolWindowFactory : ToolWindowFactory, DumbAware {
  /**
   * Only call once
   */
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val myToolWindow = FavoriteActionsToolWindow(project)
    val contentFactory = ContentFactory.SERVICE.getInstance()
    val content = contentFactory.createContent(myToolWindow.toolWindowPanel, "", false)
    toolWindow.contentManager.addContent(content);
    when (toolWindow) {
      is ToolWindowEx -> {
        toolWindow.setTitleActions(AddActionToFavoritesAction())
      }
    }
  }
}

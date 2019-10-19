package toolwindows

import com.intellij.ide.actions.ApplyIntentionAction
import com.intellij.ide.util.gotoByName.GotoActionModel
import com.intellij.ide.util.gotoByName.GotoActionModel.defaultActionForeground
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionManagerImpl
import com.intellij.openapi.keymap.KeymapUtil
import com.intellij.openapi.keymap.KeymapUtil.getActiveKeymapShortcuts
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.SimpleTextAttributes.STYLE_PLAIN
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.Icon
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer
import kotlin.random.Random

typealias ActionId = String

class ActionRenderer : ListCellRenderer<ActionId> {
  override fun getListCellRendererComponent(list: JList<out ActionId>,
                                            action: ActionId,
                                            index: Int,
                                            isSelected: Boolean, cellHasFocus: Boolean): Component {
    val anAction = ActionManager.getInstance().getAction(action)
    val panel = JPanel(BorderLayout())
    panel.border = JBUI.Borders.empty(2)
    panel.isOpaque = true
    val bg = UIUtil.getListBackground(isSelected, cellHasFocus)
    panel.background = bg
    val nameComponent = SimpleColoredComponent()
    nameComponent.background = bg
    panel.add(nameComponent, BorderLayout.CENTER)
    val str = anAction.templatePresentation.description ?: ""
    nameComponent.append(str, SimpleTextAttributes(STYLE_PLAIN, defaultActionForeground(isSelected, cellHasFocus, null)))
    val shortcuts = getActiveKeymapShortcuts(ActionManager.getInstance().getId(anAction)).getShortcuts()
    panel.add(JBLabel(anAction.templatePresentation.icon), BorderLayout.EAST)
    return panel
  }

}

class FavoriteActionsToolWindow() {
  val ui: SimpleToolWindowPanel

  init {
    val actionManager = ActionManager.getInstance()
    val actions = actionManager.getActionIds("")
        .take(10)
        .filter { actionManager.getAction(it).templatePresentation.description != null }
    val mList = JBList(actions)
    val renderer = ActionRenderer()
    mList.cellRenderer = renderer
    ui = SimpleToolWindowPanel(true, false)
    ui.setContent(mList)
  }
}


class FavoriteActionsToolWindowFactory : ToolWindowFactory {
  /**
   * Only call once
   */
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val myToolWindow = FavoriteActionsToolWindow()
    val contentFactory = ContentFactory.SERVICE.getInstance()
    val content = contentFactory.createContent(myToolWindow.ui, "", false)
    toolWindow.contentManager.addContent(content);
  }
}

package org.dandoh.favacts.utils

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.SimpleColoredComponent
import org.dandoh.favacts.services.ActionId
import java.awt.BorderLayout
import java.awt.Font
import java.util.logging.Logger
import javax.swing.JList
import javax.swing.JPanel

fun logIDE(anything: Any) {
  Logger.getGlobal().info(anything.toString())
}


fun cutName(input: String, shortcutText: String, list: JList<*>, panel: JPanel, nameComponent: SimpleColoredComponent): String {
  var name = input
  if (!list.isShowing || list.width <= 0) {
    return StringUtil.first(name, 60, true) //fallback to previous behaviour
  }
  val freeSpace = calcFreeSpace(list, panel, nameComponent, shortcutText)

  if (freeSpace <= 0) {
    return name
  }

  val fm = nameComponent.getFontMetrics(nameComponent.font)
  val strWidth = fm.stringWidth(name)
  if (strWidth <= freeSpace) {
    return name
  }

  var cutSymbolIndex = ((freeSpace.toDouble() - fm.stringWidth("...")) / strWidth * name.length).toInt()
  cutSymbolIndex = Integer.max(1, cutSymbolIndex)
  name = name.substring(0, cutSymbolIndex)
  while (fm.stringWidth("$name...") > freeSpace && name.length > 1) {
    name = name.substring(0, name.length - 1)
  }

  return name.trim { it <= ' ' } + "..."
}

fun calcFreeSpace(list: JList<*>, panel: JPanel, nameComponent: SimpleColoredComponent, shortcutText: String): Int {
  val layout = panel.layout as BorderLayout
  val eastComponent = layout.getLayoutComponent(BorderLayout.EAST)
  val westComponent = layout.getLayoutComponent(BorderLayout.WEST)
  var freeSpace = (list.width
      - (list.insets.right + list.insets.left)
      - (panel.insets.right + panel.insets.left)
      - (eastComponent?.preferredSize?.width ?: 0)
      - (westComponent?.preferredSize?.width ?: 0)
      - (nameComponent.insets.right + nameComponent.insets.left)
      - (nameComponent.ipad.right + nameComponent.ipad.left)
      - nameComponent.iconTextGap)

  if (StringUtil.isNotEmpty(shortcutText)) {
    val fm = nameComponent.getFontMetrics(nameComponent.font.deriveFont(Font.BOLD))
    freeSpace -= fm.stringWidth(" $shortcutText")
  }

  return freeSpace
}

fun updateUI(updater: () -> Unit) {
  ApplicationManager.getApplication().invokeLater(updater)
}

fun isAction(actionId: ActionId) : Boolean {
  return ActionManager.getInstance().getAction(actionId) != null
}


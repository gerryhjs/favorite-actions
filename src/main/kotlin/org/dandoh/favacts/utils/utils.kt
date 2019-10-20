package org.dandoh.favacts.utils

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.SimpleColoredComponent
import org.dandoh.favacts.services.ActionId
import java.awt.BorderLayout
import java.awt.Font
import java.awt.event.InputEvent
import java.util.logging.Logger
import javax.swing.JList
import javax.swing.JPanel

fun logIDE(anything: Any) {
  Logger.getGlobal().info(anything.toString())
}


fun updateUI(updater: () -> Unit) {
  ApplicationManager.getApplication().invokeLater(updater)
}

fun isAction(actionId: ActionId): Boolean {
  return ActionManager.getInstance().getAction(actionId) != null
}

fun invokeAction(inputEvent: InputEvent, actionId: ActionId?) {
  actionId ?: return
  val manager = ActionManager.getInstance()
  val action = manager.getAction(actionId)
  val context = DataManager.getInstance().getDataContext(inputEvent.component)
  val actionEvent = AnActionEvent.createFromAnAction(action, inputEvent, ActionPlaces.TOOLWINDOW_CONTENT, context)
  ActionUtil.performActionDumbAware(action, actionEvent)
}

fun invokeAction(inputEvent: InputEvent, action: AnAction) {
  val manager = ActionManager.getInstance()
  val context = DataManager.getInstance().getDataContext(inputEvent.component)
  val actionEvent = AnActionEvent.createFromAnAction(action, inputEvent, ActionPlaces.TOOLWINDOW_CONTENT, context)
  ActionUtil.performActionDumbAware(action, actionEvent)
}

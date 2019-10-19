package actions

import com.intellij.ide.actions.GotoActionBase
import com.intellij.ide.util.gotoByName.ChooseByNamePopup
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent
import com.intellij.ide.util.gotoByName.GotoActionItemProvider
import com.intellij.ide.util.gotoByName.GotoActionModel
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.impl.ActionMenu
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.progress.util.ProgressWindow
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.util.ui.UIUtil
import java.util.*
import javax.swing.SwingConstants


/**
 * Base on GotoActionAction.java of Intellij
 */
abstract class SelectActionBase : GotoActionBase(), DumbAware {

  private fun getElementAction(element: Any?): AnAction? {
    if (element is GotoActionModel.MatchedValue) {
      when (val value = element.value) {
        is AnAction -> return value
        is GotoActionModel.ActionWrapper -> return value.action
      }
    }
    return null;
  }

  private inner class SelectActionCallback() : GotoActionCallback<Any>() {
    override fun elementChosen(popup: ChooseByNamePopup, element: Any) {
      getElementAction(element)?.let { actionSelected(it) }
    }
  }

  public override fun gotoActionPerformed(e: AnActionEvent) {
    val project = e.project
    val component = e.getData(PlatformDataKeys.CONTEXT_COMPONENT)
    val editor = e.getData(CommonDataKeys.EDITOR)

    val model = GotoActionModel(project, component, editor)
    val callback = SelectActionCallback()
    val start = getInitialText(false, e)
    val searchActionPopup = createPopup(project, model, start.first, start.second)
    showNavigationPopup(callback, null, searchActionPopup, true)
  }


  private fun createPopup(project: Project?,
                          model: GotoActionModel,
                          initialText: String,
                          initialIndex: Int): ChooseByNamePopup {
    val oldPopup = project?.getUserData(ChooseByNamePopup.CHOOSE_BY_NAME_POPUP_IN_PROJECT_KEY)
    oldPopup?.close(false)
    val disposable = Disposer.newDisposable()
    val popup = object : ChooseByNamePopup(project, model, GotoActionItemProvider(model),
        oldPopup, initialText, false, initialIndex) {
      override fun initUI(callback: ChooseByNamePopupComponent.Callback,
                          modalityState: ModalityState?,
                          allowMultipleSelection: Boolean) {
        super.initUI(callback, modalityState, allowMultipleSelection)
        myList.addListSelectionListener {
          val value = myList.selectedValue
          val description = getValueDescription(value)
          description?.let {
            myDropdownPopup?.setAdText(it, SwingConstants.LEFT)
            ActionMenu.showDescriptionInStatusBar(true, myList, it)
          }
        }
      }

      private fun getValueDescription(value: Any?): String? {
        return getElementAction(value)?.templatePresentation?.description
      }

      override fun closeForbidden(ok: Boolean): Boolean {
        return true
      }

      /**
       * Only display actions
       */
      override fun filter(elements: Set<Any>): Set<Any> {
        val set = TreeSet(model)
        elements.filter { getElementAction(it) != null }.forEach { set.add(it) }
        return super.filter(set)
      }

      override fun setDisposed(disposedFlag: Boolean) {
        super.setDisposed(disposedFlag)
        Disposer.dispose(disposable)

        ActionMenu.showDescriptionInStatusBar(true, myList, null)

        for (listener in myList.listSelectionListeners) {
          myList.removeListSelectionListener(listener)
        }
        UIUtil.dispose(myList)
      }
    }

    ApplicationManager
        .getApplication()
        .messageBus
        .connect(disposable)
        .subscribe<ProgressWindow.Listener>(ProgressWindow.TOPIC, ProgressWindow.Listener { pw ->
          Disposer.register(pw, Disposable {
            if (!popup.checkDisposed()) {
              popup.repaintList()
            }
          })
        })

    return popup
  }

  abstract fun actionSelected(action: AnAction)

  override fun requiresProject(): Boolean {
    return true
  }
}
package toolwindows

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.*


class DumbToolWindows(toolWindow: ToolWindow) {
    private var button1: JButton? = null
    private var radioButton1: JRadioButton? = null
    private var jPanel: JPanel? = null

    init {

    }

    fun getContent() : JPanel {
        return jPanel!!
    }

}

class DumbWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val dumbToolWindows = DumbToolWindows(toolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory?.createContent(dumbToolWindows.getContent(), "", false)
        toolWindow.contentManager.addContent(content!!)
    }

}
package components

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil
import java.util.logging.Logger

data class SavedActionsData(var counter: Int);

@State(name = "SavedActions", storages = [Storage("whatever.xml")])
class SavedActionsComponent : PersistentStateComponent<SavedActionsData> {
    val savedActionsData = SavedActionsData(0);
    override fun getState(): SavedActionsData? {
        Logger.getGlobal().info("Calling get state");
        return XmlSerializerUtil.createCopy(savedActionsData)
    }

    override fun loadState(state: SavedActionsData) {
        Logger.getGlobal().info("Calling load state");
        XmlSerializerUtil.copyBean(state, savedActionsData);
    }
}


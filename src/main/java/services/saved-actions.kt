package services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage
import com.intellij.util.xml.Attribute
import com.intellij.util.xmlb.XmlSerializerUtil
import java.util.logging.Logger

class SavedActionsData(var counter: Int);

@State(name = "SavedActionsService", storages = [Storage("SavedActionsService.xml")])
class SavedActionsService : PersistentStateComponent<SavedActionsService> {
    var counter = 0;

    override fun getState(): SavedActionsService? {
        return this;
    }

    override fun loadState(state: SavedActionsService) {
        XmlSerializerUtil.copyBean(state, this);
    }


}


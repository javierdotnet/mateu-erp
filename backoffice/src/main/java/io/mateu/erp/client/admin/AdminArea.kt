package io.mateu.erp.client.admin

import io.mateu.ui.core.client.app.AbstractArea
import io.mateu.ui.core.client.app.AbstractModule
import java.util.*

/**
 * Created by miguel on 3/1/17.
 */
class AdminArea : AbstractArea("Admin") {

    override fun buildModules(): List<AbstractModule> {
        return Arrays.asList<AbstractModule>(AdminModule())
    }
}

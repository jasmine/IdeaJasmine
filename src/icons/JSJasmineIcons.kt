package icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object JSJasmineIcons {
    val Jasmine = load("/io/pivotal/intellij/jasmine/icons/jasmine.png") // 16x16
    private fun load(path: String): Icon {
        return IconLoader.getIcon(path, JSJasmineIcons::class.java)
    }
}

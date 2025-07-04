package io.pivotal.intellij.jasmine.util

class JsRegexUtil {
    companion object {
        fun escape(s: String): String {
            // Java's Pattern.escape relies on escaping features that JS regexes
            // don't support, so we do it by hand.
            // This is a little excessive (some of these only need escaping in
            // certain contexts) but should be safe.
            val basicSpecialChars = arrayOf('\\', '[', ']', '{', '}', '(', ')',
                '.', ',', '*', '|', '^', '$', '+', '?')
            val sb = StringBuilder()

            s.forEach( {c ->
                if (c == '-') {
                    sb.append("\\x2d")
                } else {
                    if (basicSpecialChars.contains(c)) {
                        sb.append('\\')
                    }

                    sb.append(c)
                }
            })

            return sb.toString()
        }
    }
}
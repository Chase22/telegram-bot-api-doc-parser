package io.github.chase22.docparser

import org.jsoup.nodes.Element


fun parseTable(table: Element): List<Parameter> {
    if (table.tagName() != "table") {
        throw IllegalArgumentException("given parameter is not a table")
    }
    return table
        .getElementsByTag("tbody")
        .first()
        .getElementsByTag("tr")
        .map {
            val name = it.child(0).text()
            val type = it.child(1).text()
            val description = it.children().last().text()

            val optional = when {
                it.childrenSize() == 4 -> it.child(2).text() == "Optional"
                it.childrenSize() == 3 -> description.startsWith("Optional")
                else -> throw IllegalArgumentException("Tables with ${it.childrenSize()} columns are not supported")
            }
            return@map Parameter(name, type, optional, description)
        }
}

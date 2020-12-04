package io.github.chase22.docparser

import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File

fun main(args: Array<String>) {
    val document = Jsoup.connect("https://core.telegram.org/bots/api").get()
    val sections = document
        .getElementById("dev_page_content")
        .getElementsByTag("h3")
        .map { section ->
            Section(section.text(), section, section.nextElementSiblings()
                .takeWhile { it.tagName() != "h3" }
                .filter { it.tagName() == "h4" }
                .map { subSection ->
                    Section(
                        subSection.text(),
                        subSection,
                        null,
                        subSection.nextElementSiblings().takeWhile { it.tagName() != "h4" })
                }
            )
        }

    val subsections = sections.filter { it.subSections != null }.flatMap { it.subSections!! }
    val methods = subsections.filter { it.title.first().isLowerCase() }
        .map { section ->
            Method(
                section.title,
                "",
                mapParameters(section)
            )
        }

    val types = subsections.filter { it.title.first().isUpperCase() && !it.title.contains(' ') }
        .map { section ->
            Type(section.title, "", mapParameters(section))
        }

    ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(
        File("build/output.json"),
        Definition(
            types.map { it.name to it }.toMap(),
            methods.map { it.name to it }.toMap()
        )
    )

    println(sections.map { it.title })
}

fun mapParameters(section: Section) = section.content
    ?.find { it.tagName() == "table" }
    ?.let { table -> parseTable(table) }
    ?.map { it.name to it }
    ?.toMap() ?: emptyMap()

data class Section(
    val title: String,
    val elem: Element,
    val subSections: List<Section>?,
    val content: List<Element>? = null
) {
    override fun toString(): String {
        return title
    }
}

data class Definition(
    val types: Map<String, Type>,
    val methods: Map<String, Method>
)

data class Method(
    val name: String,
    val description: String,
    val parameters: Map<String, Parameter>
)

data class Type(
    val name: String,
    val description: String,
    val fields: Map<String, Parameter>
)

data class Parameter(
    val name: String,
    val type: String,
    val optional: Boolean,
    val description: String
)

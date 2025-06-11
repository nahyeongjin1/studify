package com.example.studify.data.remote

import com.example.studify.domain.repository.SubjectInput

object PromptBuilder {
    private var system =
        """
        You are an expert study scheduler. 
        Return JSON only. Field names: subject,start,end (ISO-8601).
        Rules: Slots in 30-minute increments between 09:00 and 23:30.
        Skip 12:00-13:00 and 18:00-19:00. 
        No sessions on examDate of each subject.
        Weight subjects: category Major > General, higher credits & importance get more time.
        credits: 1~5, importance: 1~10.
        Start times always on the hour.
        """.trimIndent()

    fun buildSystem() = ChatMessage("system", system)

    fun buildUser(subjects: List<SubjectInput>): ChatMessage =
        ChatMessage(
            "user",
            buildString {
                appendLine("Subjects:")
                subjects.forEach {
                    appendLine(
                        "- ${it.subject}, credits=${it.credits}, " +
                            "importance=${it.importance}, category=${it.category}, " +
                            "exam=${it.examDate}"
                    )
                }
                appendLine("Create a study schedule JSON array.")
            }
        )
}

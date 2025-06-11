package com.example.studify.data.remote

import com.example.studify.domain.repository.SubjectInput
import java.time.LocalDate

object PromptBuilder {
    private val system =
        """
                You are an expert study-scheduler.

        ◎ OUTPUT
        Return **only** a JSON array.  
        Each element object must contain:
          • subject  – string  
          • start    – ISO-8601 date-time with offset(+09:00)  
          • end      – ISO-8601 date-time with offset(+09:00)

        ◎ CONSTRAINTS
        1. Today is ${LocalDate.now()}(KST).  
           - All sessions must start **after the current time**.  
        2. Work-hours: 09:00 – 23:30, 30-minute grid.  
           - No sessions at 12:00-13:00 (lunch) or 18:00-19:00 (dinner).  
        3. On any subject’s `examDate` create **no sessions**.  
        4. Each subject needs **multiple daily sessions** proportionally:  
           weight = credits × importance × categoryWeight  
           - categoryWeight: Major = 2, General = 1  
           Distribute weights so that heavier subjects get more 30-minute slots.  
        5. Start times on the hour (09:00, 10:00 …), end time = start+30 min, 1 h, 1.5 h…  
        6. Schedule until the latest examDate.
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

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

        ◎ PLANNING RULES
        1. Today is ${LocalDate.now()}(KST).
            - Every session must start **after the current time**.
            - Generate sessions **every day up to each subject's examDate** (inclusive-1). 
        
        2. Daily working window: **09:00 - 23:30**  
            - No sessions at 12:00-13:00 (lunch) or 18:00-19:00 (dinner).
        
        3. **Never create sessions on a subject's own examDate**.
        
        4. **Every session must be exactly 1 hour** (e.g. 09:00-10:00).
            - Start times must fall on **:00** only.
            - 'start' and 'end' MUST include the `+09:00` timezone offset
            (ISO-8601 with offset), e.g. `2025-06-20T09:00:00+09:00`.
            
        5. Allocate study quota *proportionally* each day:  
            `quota = credits × importance × categoryWeight × daysUntilExam⁻¹`  
            where categoryWeight = 2 (Major) or 1 (General).

        6. **Total sessions per day ≤ 10** to keep output compact.  
            If more time is needed, extend to additional days rather than over-crowding a day.
          
        7. Schedule until the latest examDate.
        
        8. Distribute sessions *across all subjects* each day.
            - A single subject may take **max 4 sessions/day**
            - Mix different subjects chronologically.
            
        9. Each subject must appear at least once every two days until its examDate.
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

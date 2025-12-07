package br.com.rodrigo.brainforge.services.AIResources.factory;

import org.springframework.stereotype.Component;

@Component
public class AIQuestionPromptFactory {

    public String create(String theme, String type, String difficulty, long num, String desc) {

        return String.format("""
                THE QUESTIONS MUST BE IN PORTUGUESE.
                Generate %d %s questions about the theme "%s"
                with difficulty level "%s".
                The questions must be related to the following description: "%s".
                The value type must be EQUAL to one of these options: MULTIPLE_CHOICE or DISCURSIVE.
                If value type is ANY, mix 50%% MULTIPLE_CHOICE and 50%% DISCURSIVE questions.

                Each question must have a "score" field so that the total sum of all scores equals 10.
                For example:
                  - If there are 10 questions, each question must have score = 1.
                  - If there are 5 questions, each question must have score = 2.
                  - If there are 4 questions, each question must have score = 2.5.

                For MULTIPLE_CHOICE questions:
                  - "options" must be an array of possible answers (strings).
                  - "correctAnswer" must be exactly one of the values present in "options".

                For DISCURSIVE questions:
                  - Do not include the "options" field.
                  - "correctAnswer" must contain a short, clear, and correct written answer.

                Return ONLY JSON in this structure:
                [
                  {
                    "statement": "Question text here",
                    "options": ["Option 1", "Option 2", "Option 3"],
                    "correctAnswer": "Option 2",
                    "type": "%s",
                    "score": 1,
                    "explanation": "explanation of the question the correct answer"
                  }
                ]
                """,
                num, type, theme, difficulty, desc);
    }
}

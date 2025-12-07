package br.com.rodrigo.brainforge.services.AIResources.factory;

import org.springframework.stereotype.Component;

@Component
public class AIQuestionPromptFactory {

    public String create(String theme, String type, String difficulty, long num, String desc) {

        return String.format("""
                THE QUESTIONS MUST BE IN PORTUGUESE.

                Generate %d %s questions about "%s".

                The difficulty level is: %s.

                Description about the theme: %s.

                The return MUST be in JSON ONLY.

                JSON format:
                {
                    "questions":[
                        {
                            "title": "",
                            "options": ["", "", "", ""],
                            "correctAnswer": "",
                            "explanation": "",
                            "score": number
                        }
                    ]
                }

                DO NOT RETURN ANY TEXT OUTSIDE THE JSON.
                """,
                num, type, theme, difficulty, desc);
    }
}

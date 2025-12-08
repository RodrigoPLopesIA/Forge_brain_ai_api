package br.com.rodrigo.brainforge.services.AIResources.factory;

import org.springframework.stereotype.Component;

@Component
public class AIVerifyAnswerPromptFactory {

  public String create(String question, String givenAnswer, String correctAnswer) {

    return String.format("""
                        Check whether the given answer is correct based on the correct answer provided.
        Both answers are in Portuguese.
        Question: "%s"
        Given answer: "%s"
        Correct answer: "%s"

        Return true if the given answer is correct; otherwise, return false.
                        """,
        question, givenAnswer, correctAnswer);
  }
}

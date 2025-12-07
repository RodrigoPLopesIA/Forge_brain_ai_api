import java.util.List;

import org.springframework.stereotype.Service;

import br.com.rodrigo.brainforge.dtos.ResponseAIQuestionDTO;
import br.com.rodrigo.brainforge.services.AIResources.cleaner.AIResponseCleaner;
import br.com.rodrigo.brainforge.services.AIResources.client.AIClient;
import br.com.rodrigo.brainforge.services.AIResources.factory.AIQuestionPromptFactory;
import br.com.rodrigo.brainforge.services.AIResources.parser.AIQuestionParser;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AIQuestionService {

    private final AIClient aiClient;
    private final AIQuestionPromptFactory promptFactory;
    private final AIResponseCleaner responseCleaner;
    private final AIQuestionParser parser;

    public AIQuestionService(
            AIClient aiClient,
            AIQuestionPromptFactory promptFactory,
            AIResponseCleaner responseCleaner,
            AIQuestionParser parser) {

        this.aiClient = aiClient;
        this.promptFactory = promptFactory;
        this.responseCleaner = responseCleaner;
        this.parser = parser;
    }

    public List<ResponseAIQuestionDTO> generateQuestions(
            String theme,
            String type,
            String difficulty,
            long numberOfQuestions,
            String description) {

        String prompt = promptFactory.create(theme, type, difficulty, numberOfQuestions, description);

        String raw = aiClient.ask(prompt);

        String cleaned = responseCleaner.clean(raw);

        return parser.parseQuestions(cleaned);
    }
}

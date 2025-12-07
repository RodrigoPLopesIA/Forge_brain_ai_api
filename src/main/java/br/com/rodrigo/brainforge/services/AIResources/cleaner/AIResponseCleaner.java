package br.com.rodrigo.brainforge.services.AIResources.cleaner;

import org.springframework.stereotype.Component;

@Component
public class AIResponseCleaner {

    public String clean(String raw) {
        String r = raw.trim();

        if (r.startsWith("json")) r = r.substring(4).trim();
        if (r.startsWith("```json")) r = r.substring(7).trim();
        if (r.endsWith("```")) r = r.substring(0, r.length() - 3).trim();

        return r;
    }
}

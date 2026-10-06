package io.github.ragassistant.triage;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record EmailTriage(

        @JsonPropertyDescription("Catégorie principale de la demande")
        Category category,

        @JsonPropertyDescription("Urgence de 1 (faible) à 5 (critique)")
        int priority,

        @JsonPropertyDescription("Numéro de commande au format CMD-XXXXX, ou null s'il est absent")
        String orderNumber,

        @JsonPropertyDescription("Résumé en une phrase, en français")
        String summary
) {
    public enum Category { DELIVERY, REFUND, PRODUCT_ISSUE, OTHER }
}
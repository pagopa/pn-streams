package it.pagopa.pn.stream.dto.legalfacts;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DocumentComposition {

    public static TemplateType retrieveTemplateFromLang(TemplateType templateType, String additionalLang) {
        String finalTemplateName = templateType.name() + "_" + additionalLang;
        log.info("Retrieve template [{}] from lang: [{}]", finalTemplateName, additionalLang);
        return TemplateType.valueOf(finalTemplateName);
    }

    public enum TemplateType {
        REQUEST_ACCEPTED("documents_composition_templates/NotificationReceivedLegalFact.html"),
        REQUEST_ACCEPTED_DE("documents_composition_templates/NotificationReceivedLegalFact_de.html"),
        REQUEST_ACCEPTED_SL("documents_composition_templates/NotificationReceivedLegalFact_sl.html"),
        REQUEST_ACCEPTED_FR("documents_composition_templates/NotificationReceivedLegalFact_fr.html"),


        DIGITAL_NOTIFICATION_WORKFLOW("documents_composition_templates/PecDeliveryWorkflowLegalFact.html"),
        DIGITAL_NOTIFICATION_WORKFLOW_DE("documents_composition_templates/PecDeliveryWorkflowLegalFact_de.html"),
        DIGITAL_NOTIFICATION_WORKFLOW_SL("documents_composition_templates/PecDeliveryWorkflowLegalFact_sl.html"),
        DIGITAL_NOTIFICATION_WORKFLOW_FR("documents_composition_templates/PecDeliveryWorkflowLegalFact_fr.html"),


        ANALOG_NOTIFICATION_WORKFLOW_FAILURE("documents_composition_templates/AnalogDeliveryWorkflowFailureLegalFact.html"),
        ANALOG_NOTIFICATION_WORKFLOW_FAILURE_DE("documents_composition_templates/AnalogDeliveryWorkflowFailureLegalFact_de.html"),
        ANALOG_NOTIFICATION_WORKFLOW_FAILURE_SL("documents_composition_templates/AnalogDeliveryWorkflowFailureLegalFact_sl.html"),
        ANALOG_NOTIFICATION_WORKFLOW_FAILURE_FR("documents_composition_templates/AnalogDeliveryWorkflowFailureLegalFact_fr.html"),


        NOTIFICATION_VIEWED("documents_composition_templates/NotificationViewedLegalFact.html"),
        NOTIFICATION_VIEWED_DE("documents_composition_templates/NotificationViewedLegalFact_de.html"),
        NOTIFICATION_VIEWED_SL("documents_composition_templates/NotificationViewedLegalFact_sl.html"),
        NOTIFICATION_VIEWED_FR("documents_composition_templates/NotificationViewedLegalFact_fr.html"),


        AAR_NOTIFICATION("documents_composition_templates/NotificationAAR.html"),
        AAR_NOTIFICATION_DE("documents_composition_templates/NotificationAAR_de.html"),
        AAR_NOTIFICATION_SL("documents_composition_templates/NotificationAAR_sl.html"),
        AAR_NOTIFICATION_FR("documents_composition_templates/NotificationAAR_fr.html"),


        AAR_NOTIFICATION_RADD("documents_composition_templates/NotificationAAR_RADD.html"),


        AAR_NOTIFICATION_RADD_ALT("documents_composition_templates/NotificationAAR_RADDalt.html"),
        AAR_NOTIFICATION_RADD_ALT_DE("documents_composition_templates/NotificationAAR_RADDalt_de.html"),
        AAR_NOTIFICATION_RADD_ALT_SL("documents_composition_templates/NotificationAAR_RADDalt_sl.html"),
        AAR_NOTIFICATION_RADD_ALT_FR("documents_composition_templates/NotificationAAR_RADDalt_fr.html"),


        AAR_NOTIFICATION_EMAIL("documents_composition_templates/NotificationAARForEMAIL.html"),
        AAR_NOTIFICATION_EMAIL_DE("documents_composition_templates/NotificationAARForEMAIL_de.html"),
        AAR_NOTIFICATION_EMAIL_SL("documents_composition_templates/NotificationAARForEMAIL_sl.html"),
        AAR_NOTIFICATION_EMAIL_FR("documents_composition_templates/NotificationAARForEMAIL_fr.html"),


        AAR_NOTIFICATION_PEC("documents_composition_templates/NotificationAARForPEC.html"),
        AAR_NOTIFICATION_PEC_DE("documents_composition_templates/NotificationAARForPEC_de.html"),
        AAR_NOTIFICATION_PEC_SL("documents_composition_templates/NotificationAARForPEC_sl.html"),
        AAR_NOTIFICATION_PEC_FR("documents_composition_templates/NotificationAARForPEC_fr.html"),

        NOTIFICATION_CANCELLED("documents_composition_templates/NotificationCancelledLegalFact.html"),
        NOTIFICATION_CANCELLED_DE("documents_composition_templates/NotificationCancelledLegalFact_de.html"),
        NOTIFICATION_CANCELLED_SL("documents_composition_templates/NotificationCancelledLegalFact_sl.html"),
        NOTIFICATION_CANCELLED_FR("documents_composition_templates/NotificationCancelledLegalFact_fr.html"),

        AAR_NOTIFICATION_SUBJECT("documents_composition_templates/NotificationAARSubject.txt"),
        AAR_NOTIFICATION_SMS("documents_composition_templates/NotificationAARForSMS.txt");

        private final String htmlTemplate;

        TemplateType(String htmlTemplate) {
            this.htmlTemplate = htmlTemplate;
        }
    }
}

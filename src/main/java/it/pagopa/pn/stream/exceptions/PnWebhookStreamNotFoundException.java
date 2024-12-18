package it.pagopa.pn.stream.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;

public class PnWebhookStreamNotFoundException extends PnRuntimeException {


    public PnWebhookStreamNotFoundException(String message) {
        super(message, PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_NOT_FOUND, 404, PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_NOT_FOUND, null, null);

    }
}

package it.pagopa.pn.stream.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;

public class PnWebhookForbiddenException extends PnRuntimeException {

    public PnWebhookForbiddenException(String message) {
        super(message, PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_FORBIDDEN, 403, PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_FORBIDDEN, null, null);
    }

}

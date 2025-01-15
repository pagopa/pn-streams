package it.pagopa.pn.stream.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;

public class PnWebhookTooManyRequestException extends PnRuntimeException {
    public PnWebhookTooManyRequestException(String message) {
        super(message, PnStreamExceptionCodes.ERROR_CODE_STREAM_RETRYAFTER_FAILED, 429, PnStreamExceptionCodes.ERROR_CODE_STREAM_RETRYAFTER_FAILED, null, null);
    }
}

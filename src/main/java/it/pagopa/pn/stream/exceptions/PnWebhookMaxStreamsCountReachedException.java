package it.pagopa.pn.stream.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;

public class PnWebhookMaxStreamsCountReachedException extends PnRuntimeException {

    public PnWebhookMaxStreamsCountReachedException() {
        super("Max streams count reached for PA", PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_MAXSTREAMSCOUNTREACHED, 409, PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_MAXSTREAMSCOUNTREACHED, null, null);
    }

}

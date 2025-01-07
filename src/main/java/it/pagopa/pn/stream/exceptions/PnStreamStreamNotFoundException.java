package it.pagopa.pn.stream.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;

public class PnStreamStreamNotFoundException extends PnRuntimeException {


    public PnStreamStreamNotFoundException(String message) {
        super(message, PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_NOT_FOUND, 404, PnStreamExceptionCodes.ERROR_CODE_WEBHOOK_NOT_FOUND, null, null);

    }
}

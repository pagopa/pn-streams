package it.pagopa.pn.stream.exceptions;


import static it.pagopa.pn.stream.exceptions.PnStreamExceptionCodes.ERROR_CODE_STREAM_ROOTIDNOTFOUND;

public class PnRootIdNonFountException extends PnNotFoundException {

    public PnRootIdNonFountException(String description) {
        super("RootId not found", description, ERROR_CODE_STREAM_ROOTIDNOTFOUND);
    }

}

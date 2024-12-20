package it.pagopa.pn.stream.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PnStreamExceptionCodesTest {

    private PnStreamExceptionCodes code;

    @Test
    void checkAll() {
        Assertions.assertAll(
                () -> Assertions.assertEquals("PN_STREAM_NOTFOUND", PnStreamExceptionCodes.ERROR_CODE_STREAM_NOTFOUND),
                () -> Assertions.assertEquals("PN_STREAM_GETFILEERROR", PnStreamExceptionCodes.ERROR_CODE_STREAM_GETFILEERROR)
        );
    }

}
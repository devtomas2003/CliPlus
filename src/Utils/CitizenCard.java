package Utils;

import pt.gov.cartaodecidadao.*;

public class CitizenCard {
    static {
        System.loadLibrary("pteidlibj");
    }
    private static PTEID_ReaderSet readerSet = null;
    private static PTEID_ReaderContext readerContext = null;
    private static PTEID_EIDCard eidCard = null;

    public static PTEID_EId initiate() throws PTEID_Exception {
        PTEID_ReaderSet.initSDK();
        readerSet = PTEID_ReaderSet.instance();
        readerContext = readerSet.getReader();
        eidCard = readerContext.getEIDCard();
        return eidCard.getID();
    }

    public static void release() {
        try {
            PTEID_ReaderSet.releaseSDK();
        } catch (PTEID_Exception ex) {
            System.out.println("Caught exception in releaseSDK(). Error: " + ex.GetMessage());
        }
    }
}

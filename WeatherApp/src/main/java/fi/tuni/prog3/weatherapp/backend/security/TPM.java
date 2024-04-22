package fi.tuni.prog3.weatherapp.backend.security;

import tss.*;
import tss.tpm.*;

import java.util.Random;


public class TPM {
    static Random rand;
    static boolean useSim = false;
    public static byte[] RandomBytes(int numBytes)
    {
        if (rand==null)
            rand = new Random();

        byte[] res = new byte[numBytes];
        rand.nextBytes(res);
        return res;
    }
    public static byte[] getQuote() {
        Tpm tpm = !useSim ? TpmFactory.platformTpm() : TpmFactory.localTpmSimulator();;

        TPMT_PUBLIC rsaTemplate = new TPMT_PUBLIC(TPM_ALG_ID.SHA256,
                new TPMA_OBJECT(TPMA_OBJECT.sign, TPMA_OBJECT.sensitiveDataOrigin, TPMA_OBJECT.userWithAuth,
                        TPMA_OBJECT.restricted),
                new byte[0], new TPMS_RSA_PARMS(new TPMT_SYM_DEF_OBJECT(),
                new TPMS_SIG_SCHEME_RSASSA(TPM_ALG_ID.SHA256),  2048, 65537),
                new TPM2B_PUBLIC_KEY_RSA());

        CreatePrimaryResponse quotingKey = tpm.CreatePrimary(TPM_HANDLE.from(TPM_RH.ENDORSEMENT),
                new TPMS_SENSITIVE_CREATE(new byte[0], new byte[0]), rsaTemplate, new byte[0],
                new TPMS_PCR_SELECTION[0]);

        System.out.println("RSA Primary quoting Key: \n" + quotingKey.toString());

        // Set some PCR to non-zero values
        tpm.PCR_Event(TPM_HANDLE.pcr(10), new byte[] { 0, 1, 2 });
        tpm.PCR_Event(TPM_HANDLE.pcr(11), new byte[] { 3, 4, 5 });
        tpm.PCR_Event(TPM_HANDLE.pcr(12), new byte[] { 6, 7, 8 });

        TPMS_PCR_SELECTION[] pcrToQuote = new TPMS_PCR_SELECTION[] {
                new TPMS_PCR_SELECTION(TPM_ALG_ID.SHA256, new int[] { 10, 11, 12 }) };

        // Get the PCR so that we can validate the quote
        PCR_ReadResponse pcrs = tpm.PCR_Read(pcrToQuote);

        // Quote these PCR
        byte[] dataToSign = RandomBytes(10);
        QuoteResponse quote = tpm.Quote(quotingKey.handle, dataToSign, new TPMS_NULL_SIG_SCHEME(), pcrToQuote);

        System.out.println("Quote signature: \n" + quote.toString());

        // Validate the quote using tss.Java support functions
        boolean quoteOk = quotingKey.outPublic.validateQuote(pcrs, dataToSign, quote);
        System.out.println("Quote validated:" + String.valueOf(quoteOk));
        if (!quoteOk)
            throw new RuntimeException("Quote validation failed!");
        tpm.FlushContext(quotingKey.handle);
        return null;
    }
}

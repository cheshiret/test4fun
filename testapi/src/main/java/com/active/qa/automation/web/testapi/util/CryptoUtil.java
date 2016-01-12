package com.active.qa.automation.web.testapi.util;

import com.active.qa.automation.web.testapi.TestApiConstants;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by tchen on 1/11/2016.
 */

public class CryptoUtil {
    public static final String AES = "AES";

    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(128);
        SecretKey sk = keyGen.generateKey();
        byte[] b=sk.getEncoded();
        String key= StringUtil.byteArrayToHexString(b);

        return key;
    }

    public static String encrypt(String value){
        return encrypt(value, TestApiConstants.KEY);
    }

    /**
     * encrypt a value and generate a key
     * @param value
     * @param key
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String encrypt(String value, String key){
        try {
            SecretKeySpec sks = getSecretKeySpec(key);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return StringUtil.byteArrayToHexString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String value){
        return decrypt(value,TestApiConstants.KEY);
    }

    /**
     * decrypt a value
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String decrypt(String value, String keyString) {
        try {
            SecretKeySpec sks = getSecretKeySpec(keyString);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decrypted = cipher.doFinal(StringUtil.hexStringToByteArray(value));
            return new String(decrypted);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static SecretKeySpec getSecretKeySpec(String keyString) {
        byte[] key = StringUtil.hexStringToByteArray(keyString);
        return new SecretKeySpec(key,AES);
    }

    /**
     * The method used to decrypt information from DB,such as customer identifier,gift card number etc.
     * @param secret
     * @return
     */
    public static String decryptInfoInDB(String secret) {
        String sKey1 = ddigitHex2Str("5374407254247072336144216E47644E33775A5F4050702165");
        String sKey2 = "Dav3&gR@GRBr1!!iaNt&p0$$3s$a#oF1mp0rTAnt_sKi1!$Ne3Ded4Go0dw0rk";
        byte[] lsEnc1 = new byte[secret.length() / 2];
        String lsEnc1S;
        String lsEnc2 = "";
        String lsHex1;
        int j = 0, len = secret.length();

        for (int i = 0; i < len; i += 2) {
            // System.out.println("i = " + i );
            lsHex1 = secret.substring(i, i + 2);
            lsEnc1[j++] = ((byte) Hex2Int(lsHex1));
        }

        lsEnc1S = new String(encrypt(new String(lsEnc1).getBytes(), sKey2.getBytes()));

        for (int i = 0; i < lsEnc1S.length(); i += 2) {
            lsHex1 = lsEnc1S.substring(i, i + 2);
            try {
                lsEnc2 += (char) Hex2Int(lsHex1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        lsEnc2 = new String(encrypt(lsEnc2.getBytes(), sKey1.getBytes()));

        lsEnc2 = sufCalcCard(lsEnc2);

        return lsEnc2;

    }

    private static String ddigitHex2Str(String psCode) {
        String lsHex1;
        String lsEnc1 = "";
        int num = psCode.length();

        for (int i = 0; i < num; i += 2) {
            lsHex1 = psCode.substring(i, i + 2);
            lsEnc1 += (char) Hex2Int(lsHex1);
        }

        return lsEnc1;
    }

    /**
     * check if the String only contains numbers
     * @param value
     * @return
     * @Return boolean
     * @Throws
     */
    private static boolean onlyNumbers(String value) {
        for (char c : value.toCharArray())
            if (!Character.isDigit(c))
                return false;
        return value.length() > 0;
    }

    private static String sufCalcCard(String psCard) {
        if (psCard.length() < 8 || !onlyNumbers(psCard))
            return psCard;

        String lsFirst;
        String lsRest;
        int liFirst;
        int liLast;

        lsRest = psCard.substring(5);

        liFirst = iVal(left(psCard, 5),0);
        liLast = iVal(right(psCard, 4),0);

        DecimalFormat NMF = new DecimalFormat("0000");

        lsFirst = NMF.format(liFirst - liLast);

        return lsFirst + lsRest;
    }

    /**
     * get sub string of target String from right of the element which index is given
     * @param string
     * @param count
     * @return
     * @Return String
     * @Throws
     */
    private static String right(String string, int count) {
        String rightString = null;

        if (string != null) {
            if (string.length() >= count && count > 0)
                rightString = string.substring(string.length() - count, string
                        .length());
            else
                rightString = string;
        }

        return rightString;
    }

    /**
     * parse string to int, if psNum is null, return defaultValue
     * @param psNum
     * @param defaultValue
     * @return
     * @Return int
     * @Throws
     */
    private static int iVal(String psNum, int defaultValue) {
        if (psNum == null)
            return defaultValue;
        int liVal;

        try {
            liVal = Integer.valueOf(psNum).intValue();
        } catch (Exception e) {
            liVal = defaultValue;
        }

        return liVal;
    }

    private static int iVal(String psNum){
        return iVal(psNum,0);
    }

    /**
     * get subString of target string from left of the given index
     * @param string
     * @param count
     * @return
     * @Return String
     * @Throws
     */
    private static String left(String string, int count) {
        String leftString = null;

        if (string != null) {
            if (string.length() > count) {
                if (count > 0)
                    leftString = string.substring(0, count);
                else
                    leftString = "";
            } else {
                leftString = string;
            }
        }

        return leftString;
    }

    /**
     * encrypt the psValue
     * @param psValue
     * @param psKey
     * @return
     * @Return byte[]
     * @Throws
     */
    private static byte[] encrypt(byte[] psValue, byte[] psKey) {
        int liLen = psKey.length;
        int X;
        int liPosition;
        byte Char;
        byte[] lsTemp = new byte[psValue.length];

        // ' Loop from 1 to the length of the data to encrypt
        for (X = 0; X < psValue.length; X++) {

            // 'Char is set to the Xth character in the password
            // Char = Asc(Mid$(PassWord$, ((X - 1) Mod liLen) + 1, 1))
            liPosition = X % liLen;
            Char = psKey[liPosition];

            // ' Add the Xored character to the Final Encrypted String
            lsTemp[X] = (byte) (psValue[X] ^ Char);
            // Chr$(Asc(Mid$(secret$, X, 1)) Xor Char)

        }

        return lsTemp;
    }

    /**
     * turn hex to int
     * @param psHex
     * @return
     * @Return int
     * @Throws
     */
    private static int Hex2Int(String psHex) {
        int liIntCharValue;
        int liCharPosition;
        int liInt = 0;
        String Char;

        int num = psHex.length();

        for (int i = 0; i < num; i++) {
            Char = psHex.substring(i, i + 1).toLowerCase();

            if (Char.charAt(0) >= 48 && Char.charAt(0) <= 57) {
                liIntCharValue = (Char.charAt(0)) - 48;
            } else {
                liIntCharValue = ((Char.charAt(0)) - 97) + 10;
            }

            liCharPosition = psHex.length() - (i + 1);
            liInt += liIntCharValue * Math.pow(16, liCharPosition);
        }
        return liInt;
    }
    //**************************************************************************

    private static byte[] encryptGiftCard( String psValue, String psKey ) {
        // System.out.println( "len1 " + psValue.getBytes().length + " str " + psValue.length() );
        // System.out.println( "len2 " + psKey.getBytes().length );
        return encrypt( psValue.getBytes(), psKey.getBytes() );
    }

    public static String encryptGiftCard( String creditCardNumber ) {
        try {
            String sKey1 = ddigitHex2Str( "5374407254247072336144216E47644E33775A5F4050702165" );
            String sKey2 = "Dav3&gR@GRBr1!!iaNt&p0$$3s$a#oF1mp0rTAnt_sKi1!$Ne3Ded4Go0dw0rk";

            return encode( creditCardNumber, sKey1, sKey2 );
        } catch( Exception p_oEx ) {
            AutomationLogger.getInstance().error( "CCDbCard.iceCard ", p_oEx );
        }

        return creditCardNumber;

    }

    private static String preCalcCard( String psCard ) {
        if ( psCard.length() < 8 || !onlyNumbers( psCard ) ) return psCard;
        String lsFirst;
        String lsRest;
        int liFirst;
        int liLast;


        lsRest = psCard.substring( 4 );

        liFirst =iVal( left( psCard, 4 ));
        // System.out.println( "First " + liFirst + " " + fStr.left(psCard, 4) );
        liLast = iVal( right( psCard, 4 ) );
        // System.out.println( "Last " + liLast + " " + fStr.right(psCard, 4) );
        DecimalFormat NMF = new DecimalFormat( "00000" );

        lsFirst = NMF.format( liFirst + liLast );
        // System.out.println( "newFirst " + lsFirst );
        return lsFirst + lsRest;
    }

    private static String int2HexStr( int piInt ) {
        StringBuffer lsHex = new StringBuffer();
        int liPlaceHolder;

        liPlaceHolder = (int) Math.floor( piInt / 16 );

        // System.out.println("place holder = " + liPlaceHolder);
        if( liPlaceHolder > 9 )
            lsHex.append( (char) ( 65 + ( liPlaceHolder - 10 ) ) );
        else
            lsHex.append( String.valueOf( liPlaceHolder ).trim() );

        liPlaceHolder = piInt % 16;

        if( liPlaceHolder > 9 )
            lsHex.append( (char) ( 65 + ( liPlaceHolder - 10 ) ) );
        else
            lsHex.append( String.valueOf( liPlaceHolder ).trim() );

        // System.out.println( "In " + piInt + " Out >" + lsHex.toString() + "<" );

        return lsHex.toString();
    }

    public static String encode( String psCard, String psKey1, String psKey2 ) {
        byte[] EncCardNum;
        byte[] lsEnc1Char;
        String lsEnc1 = "";
        String lsEnc2 = "";
        String lsTmp;

        // System.out.println(preCalcCard( psCard ));
        EncCardNum = encryptGiftCard( preCalcCard( psCard ), psKey1 );
        // System.out.println( new String(EncCardNum) );

        int len = EncCardNum.length;

        for( int i = 0; i < len; i++ ) {
            lsTmp = int2HexStr( EncCardNum[i] );
            lsEnc1 += lsTmp;
        }

        // System.out.println( "EncStep1: " + lsEnc1 );

        lsEnc1Char = encryptGiftCard( lsEnc1, psKey2 );

        len = lsEnc1.length();
        for( int i = 0; i < len; i++ ) {
            lsTmp = int2HexStr( lsEnc1Char[i] );
            lsEnc2 = lsEnc2 + lsTmp;
        }

        // System.out.println( "Step 2: " +  lsEnc2);

        return lsEnc2;
    }

    public static String decode( String psCardEnc, String psKey1, String psKey2 ) {
        byte[] lsEnc1 = new byte[psCardEnc.length() / 2];
        String lsEnc1S;
        String lsEnc2 = "";
        String lsHex1;
        int j = 0, len = psCardEnc.length();

        for( int i = 0; i < len; i += 2 ) {
            // System.out.println("i = " + i );
            lsHex1 = psCardEnc.substring( i, i + 2 );
            lsEnc1[j++] = ( (byte) Hex2Int( lsHex1 ) );
        }

        lsEnc1S = new String( encryptGiftCard( new String( lsEnc1 ), psKey2 ) );
        // Debug.Print "1 - Hex " & lsEnc1S
        // System.out.println("Half way " + lsEnc1S );
        for( int i = 0; i < lsEnc1S.length(); i += 2 ) {
            lsHex1 = lsEnc1S.substring( i, i + 2 );
            try {
                lsEnc2 += (char) Hex2Int( lsHex1 );
            } catch( Exception e ) {
                AutomationLogger.getInstance().error("INVALID CC - Decrypt ", e );
                // System.out.println( "INVALID CC - Decrypt " + e );
            }

        }

        lsEnc2 = new String( encryptGiftCard( lsEnc2, psKey1 ) );
        // System.out.println( "2 - CardNum " + lsEnc2 );
        lsEnc2 = sufCalcCard( lsEnc2 );

        return lsEnc2;
    }

    public static String decryptGiftCard( String encryptedCreditCardNumber ) {

        try {
            String sKey1 = ddigitHex2Str( "5374407254247072336144216E47644E33775A5F4050702165" );
            String sKey2 = "Dav3&gR@GRBr1!!iaNt&p0$$3s$a#oF1mp0rTAnt_sKi1!$Ne3Ded4Go0dw0rk";

            return decode( encryptedCreditCardNumber, sKey1, sKey2 );
        } catch( Exception p_oEx ) {
            AutomationLogger.getInstance().error( "CCDbCard.deiceCard ", p_oEx );
        }

        return encryptedCreditCardNumber;
    }

}



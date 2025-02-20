/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.jss.crypto;

import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Vector;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;

import org.mozilla.jss.asn1.OBJECT_IDENTIFIER;

/**
 * An algorithm for performing symmetric encryption.
 */
public class EncryptionAlgorithm extends Algorithm {

    public static class Mode {
        private String name;

        private static Hashtable<String, Mode> nameHash = new Hashtable<>();

        private Mode() {
        }

        private Mode(String name) {
            this.name = name;
            nameHash.put(name.toLowerCase(), this);
        }

        public static Mode fromString(String name)
                throws NoSuchAlgorithmException {
            Mode m = nameHash.get(name.toLowerCase());
            if (m == null) {
                throw new NoSuchAlgorithmException(
                        "Unrecognized mode \"" + name + "\"");
            }
            return m;
        }

        @Override
        public String toString() {
            return name;
        }

        public static final Mode NONE = new Mode("NONE");
        public static final Mode ECB = new Mode("ECB");
        public static final Mode CBC = new Mode("CBC");
    }

    public static class Alg {
        private String name;

        private static Hashtable<String, Alg> nameHash = new Hashtable<>();

        private Alg() {
        }

        private Alg(String name) {
            this.name = name;
            nameHash.put(name.toLowerCase(), this);
        }

        private static Alg fromString(String name)
                throws NoSuchAlgorithmException {
            Alg a = nameHash.get(name.toLowerCase());
            if (a == null) {
                throw new NoSuchAlgorithmException("Unrecognized algorithm \""
                        + name + "\"");
            }
            return a;
        }

        @Override
        public String toString() {
            return name;
        }

        public static final Alg RC4 = new Alg("RC4");
        public static final Alg DES = new Alg("DES");
        public static final Alg DESede = new Alg("DESede");
        public static final Alg AES = new Alg("AES");
        public static final Alg RC2 = new Alg("RC2");
    }

    public static class Padding {
        private String name;

        private static Hashtable<String, Padding> nameHash = new Hashtable<>();

        private Padding() {
        }

        private Padding(String name) {
            this.name = name;
            nameHash.put(name.toLowerCase(), this);
        }

        @Override
        public String toString() {
            return name;
        }

        public static Padding fromString(String name)
                throws NoSuchAlgorithmException {
            Padding p = nameHash.get(name.toLowerCase());
            if (p == null) {
                throw new NoSuchAlgorithmException("Unrecognized Padding " +
                        "type \"" + name + "\"");
            }
            return p;
        }

        public static final Padding NONE = new Padding("NoPadding");
        public static final Padding PKCS5 = new Padding("PKCS5Padding");
    }

    private static String makeName(Alg alg, Mode mode, Padding padding) {
        StringBuffer buf = new StringBuffer();
        buf.append(alg.toString());
        buf.append('/');
        buf.append(mode.toString());
        buf.append('/');
        buf.append(padding.toString());
        return buf.toString();
    }

    protected EncryptionAlgorithm(int oidTag, Alg alg, Mode mode,
            Padding padding, Class<?> paramClass, int blockSize,
            OBJECT_IDENTIFIER oid, int keyStrength) {
        super(oidTag, makeName(alg, mode, padding), oid, paramClass);
        this.alg = alg;
        this.mode = mode;
        this.padding = padding;
        this.blockSize = blockSize;
        if (oid != null) {
            oidMap.put(oid, this);
        }
        if (name != null) {
            nameMap.put(name.toLowerCase(), this);
        }
        this.keyStrength = keyStrength;
        algList.addElement(this);
    }

    protected EncryptionAlgorithm(int oidTag, Alg alg, Mode mode,
            Padding padding, Class<?>[] paramClasses, int blockSize,
            OBJECT_IDENTIFIER oid, int keyStrength) {
        super(oidTag, makeName(alg, mode, padding), oid, paramClasses);
        this.alg = alg;
        this.mode = mode;
        this.padding = padding;
        this.blockSize = blockSize;

        if (oid != null) {
            oidMap.put(oid, this);
        }
        if (name != null) {
            nameMap.put(name.toLowerCase(), this);
        }
        this.keyStrength = keyStrength;
        algList.addElement(this);
    }

    protected EncryptionAlgorithm(int oidTag, Alg alg, Mode mode,
        Padding padding, Class<?> paramClass, int blockSize,
        OBJECT_IDENTIFIER oid, int keyStrength,String name)
    {
        super(oidTag, name, oid, paramClass);
        this.alg = alg;
        this.mode = mode;
        this.padding = padding;
        this.blockSize = blockSize;
        if(oid!=null) {
            oidMap.put(oid, this);
        }
        if( name != null ) {
            nameMap.put(name.toLowerCase(), this);
        }
        this.keyStrength = keyStrength;
        algList.addElement(this);
    }

    protected EncryptionAlgorithm(int oidTag, Alg alg, Mode mode,
        Padding padding, Class<?> []paramClasses, int blockSize,
        OBJECT_IDENTIFIER oid, int keyStrength,String name)
    {
        super(oidTag, name, oid, paramClasses);
        this.alg = alg;
        this.mode = mode;
        this.padding = padding;
        this.blockSize = blockSize;

        if(oid!=null) {
            oidMap.put(oid, this);
        }
        if( name != null ) {
            nameMap.put(name.toLowerCase(), this);
        }
        this.keyStrength = keyStrength;
        algList.addElement(this);
    }


    private int blockSize;
    private Alg alg;
    private Mode mode;
    private Padding padding;
    private int keyStrength;

    /**
     * @return The base algorithm, without the parameters. For example,
     *         the base algorithm of "AES/CBC/NoPadding" is "AES".
     */
    public Alg getAlg() {
        return alg;
    }

    /**
     * @return The mode of this algorithm.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @return The padding type of this algorithm.
     */
    public Padding getPadding() {
        return padding;
    }

    /**
     * @return The key strength of this algorithm in bits. Algorithms that
     *         use continuously variable key sizes (such as RC4) will return 0 to
     *         indicate they can use any key size.
     */
    public int getKeyStrength() {
        return keyStrength;
    }

    ///////////////////////////////////////////////////////////////////////
    // mapping
    ///////////////////////////////////////////////////////////////////////
    private static Hashtable<OBJECT_IDENTIFIER, EncryptionAlgorithm> oidMap = new Hashtable<>();
    private static Hashtable<String, EncryptionAlgorithm> nameMap = new Hashtable<>();
    private static Vector<EncryptionAlgorithm> algList = new Vector<>();

    public static EncryptionAlgorithm fromOID(OBJECT_IDENTIFIER oid)
            throws NoSuchAlgorithmException {
        Object alg = oidMap.get(oid);
        if (alg == null) {
            throw new NoSuchAlgorithmException("OID: " + oid.toString());
        } else {
            return (EncryptionAlgorithm) alg;
        }
    }

    // Note: after we remove this deprecated method, we can remove
    // nameMap.
    /**
     * @param name Algorithm name.
     * @return Encryption algorithm.
     * @throws NoSuchAlgorithmException If the algorithm is not found.
     * @deprecated This method is deprecated because algorithm strings
     *             don't contain key length, which is necessary to distinguish between
     *             AES algorithms.
     */
    @Deprecated
    public static EncryptionAlgorithm fromString(String name)
            throws NoSuchAlgorithmException {
        Object alg = nameMap.get(name.toLowerCase());
        if (alg == null) {
            throw new NoSuchAlgorithmException();
        } else {
            return (EncryptionAlgorithm) alg;
        }
    }

    public static EncryptionAlgorithm lookup(String algName, String modeName,
            String paddingName, int keyStrength)
            throws NoSuchAlgorithmException {
        int len = algList.size();
        Alg alg = Alg.fromString(algName);
        Mode mode = Mode.fromString(modeName);
        Padding padding = Padding.fromString(paddingName);

        if (paddingName == null || paddingName.equals(""))
            padding = Padding.NONE;

        int i;
        for (i = 0; i < len; ++i) {
            EncryptionAlgorithm cur = algList.elementAt(i);
            if (cur.alg == alg && cur.mode == mode && cur.padding == padding) {
                if (cur.keyStrength == 0 || cur.keyStrength == keyStrength) {
                    break;
                }
            }
        }
        if (i == len) {
            throw new NoSuchAlgorithmException(algName + "/" + modeName + "/"
                    + paddingName + " with key strength " + keyStrength +
                    " not found");
        }
        return algList.elementAt(i);
    }

    /**
     * @return The blocksize of the algorithm in bytes. Stream algorithms (such as
     *         RC4) have a blocksize of 1.
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * @return <code>true</code> if this algorithm performs padding.
     */
    public boolean isPadded() {
        return !Padding.NONE.equals(padding);
    }

    /**
     * @return The type of padding for this algorithm.
     */
    public Padding getPaddingType() {
        return padding;
    }

    private static Class<?>[] IVParameterSpecClasses = null;
    static {
        IVParameterSpecClasses = new Class[2];
        IVParameterSpecClasses[0] = IVParameterSpec.class;
        IVParameterSpecClasses[1] = IvParameterSpec.class;
    }

    /**
     * Returns the number of bytes that this algorithm expects in
     * its initialization vector.
     *
     * @return The size in bytes of the IV for this algorithm. A size of
     *         0 means this algorithm does not take an IV.
     */
    public native int getIVLength();

    public static final EncryptionAlgorithm RC4 = new EncryptionAlgorithm(SEC_OID_RC4, Alg.RC4, Mode.NONE, Padding.NONE,
            (Class<?>) null, 1, OBJECT_IDENTIFIER.RSA_CIPHER.subBranch(4), 0);

    public static final EncryptionAlgorithm DES_ECB = new EncryptionAlgorithm(SEC_OID_DES_ECB, Alg.DES, Mode.ECB,
            Padding.NONE, (Class<?>) null, 8, OBJECT_IDENTIFIER.ALGORITHM.subBranch(6),
            56);

    public static final EncryptionAlgorithm DES_CBC = new EncryptionAlgorithm(SEC_OID_DES_CBC, Alg.DES, Mode.CBC,
            Padding.NONE, IVParameterSpecClasses, 8,
            OBJECT_IDENTIFIER.ALGORITHM.subBranch(7), 56);

    public static final EncryptionAlgorithm DES_CBC_PAD = new EncryptionAlgorithm(CKM_DES_CBC_PAD, Alg.DES, Mode.CBC,
            Padding.PKCS5, IVParameterSpecClasses, 8, null, 56); // no oid

    public static final EncryptionAlgorithm DES3_ECB = new EncryptionAlgorithm(CKM_DES3_ECB, Alg.DESede, Mode.ECB,
            Padding.NONE, (Class<?>) null, 8, null, 168); // no oid

    public static final EncryptionAlgorithm DES3_CBC = new EncryptionAlgorithm(SEC_OID_DES_EDE3_CBC, Alg.DESede,
            Mode.CBC, Padding.NONE, IVParameterSpecClasses, 8,
            OBJECT_IDENTIFIER.RSA_CIPHER.subBranch(7), 168);

    public static final EncryptionAlgorithm DES3_CBC_PAD = new EncryptionAlgorithm(CKM_DES3_CBC_PAD, Alg.DESede,
            Mode.CBC, Padding.PKCS5, IVParameterSpecClasses, 8,
            null, 168); //no oid

    public static final EncryptionAlgorithm RC2_CBC = new EncryptionAlgorithm(SEC_OID_RC2_CBC, Alg.RC2, Mode.CBC,
            Padding.NONE, RC2ParameterSpec.class, 8,
            null, 0); // no oid, see comment below

    // Which algorithm should be associated with this OID, RC2_CBC or
    // RC2_CBC_PAD? NSS says RC2_CBC, but PKCS #5 v2.0 says RC2_CBC_PAD.
    // See NSS bug 202925.
    public static final EncryptionAlgorithm RC2_CBC_PAD = new EncryptionAlgorithm(CKM_RC2_CBC_PAD, Alg.RC2, Mode.CBC,
            Padding.PKCS5, RC2ParameterSpec.class, 8,
            OBJECT_IDENTIFIER.RSA_CIPHER.subBranch(2), 0);

    public static final OBJECT_IDENTIFIER AES_ROOT_OID = new OBJECT_IDENTIFIER(
            new long[] { 2, 16, 840, 1, 101, 3, 4, 1 });

    public static final EncryptionAlgorithm AES_128_ECB = new EncryptionAlgorithm(SEC_OID_AES_128_ECB,
            Alg.AES, Mode.ECB,
            Padding.NONE, (Class<?>) null, 16,
            AES_ROOT_OID.subBranch(1), 128);

    public static final EncryptionAlgorithm AES_128_CBC = new EncryptionAlgorithm(SEC_OID_AES_128_CBC,
            Alg.AES, Mode.CBC,
            Padding.NONE, IVParameterSpecClasses, 16,
            AES_ROOT_OID.subBranch(2), 128);

    public static final EncryptionAlgorithm AES_128_CBC_PAD = new EncryptionAlgorithm(SEC_OID_AES_128_CBC,
            Alg.AES, Mode.CBC,
            Padding.PKCS5, IVParameterSpecClasses, 16,
            AES_ROOT_OID.subBranch(2), 128);

    public static final EncryptionAlgorithm AES_192_ECB = new EncryptionAlgorithm(SEC_OID_AES_192_ECB,
            Alg.AES, Mode.ECB,
            Padding.NONE, (Class<?>) null, 16, AES_ROOT_OID.subBranch(21), 192);

    public static final EncryptionAlgorithm AES_192_CBC = new EncryptionAlgorithm(SEC_OID_AES_192_CBC,
            Alg.AES, Mode.CBC,
            Padding.NONE, IVParameterSpecClasses, 16,
            AES_ROOT_OID.subBranch(22), 192);

    public static final EncryptionAlgorithm AES_192_CBC_PAD = new EncryptionAlgorithm(SEC_OID_AES_192_CBC,
            Alg.AES, Mode.CBC,
            Padding.PKCS5, IVParameterSpecClasses, 16,
            AES_ROOT_OID.subBranch(22), 192);

    public static final EncryptionAlgorithm AES_256_ECB = new EncryptionAlgorithm(SEC_OID_AES_256_ECB,
            Alg.AES, Mode.ECB,
            Padding.NONE, (Class<?>) null, 16, AES_ROOT_OID.subBranch(41), 256);

    public static final EncryptionAlgorithm AES_256_CBC = new EncryptionAlgorithm(SEC_OID_AES_256_CBC,
            Alg.AES, Mode.CBC,
            Padding.NONE, IVParameterSpecClasses, 16,
            AES_ROOT_OID.subBranch(42), 256);

    public static final EncryptionAlgorithm AES_CBC_PAD = new EncryptionAlgorithm(CKM_AES_CBC_PAD, Alg.AES, Mode.CBC,
            Padding.PKCS5, IVParameterSpecClasses, 16, null, 256); // no oid

    public static final EncryptionAlgorithm AES_256_CBC_PAD = new EncryptionAlgorithm(SEC_OID_AES_256_CBC,
            Alg.AES, Mode.CBC,
            Padding.PKCS5, IVParameterSpecClasses, 16,
            AES_ROOT_OID.subBranch(42), 256);

  public static final EncryptionAlgorithm
   AES_128_KEY_WRAP_KWP = new EncryptionAlgorithm(SEC_OID_AES_128_KEY_WRAP_KWP,
        Alg.AES,Mode.NONE,
        Padding.PKCS5, IVParameterSpecClasses, 16,
        AES_ROOT_OID.subBranch(8), 128,"AES/None/PKCS5Padding/Kwp/128");

  public static final EncryptionAlgorithm
   AES_192_KEY_WRAP_KWP = new EncryptionAlgorithm(SEC_OID_AES_192_KEY_WRAP_KWP,
        Alg.AES,Mode.NONE,
        Padding.PKCS5, IVParameterSpecClasses, 16,
        AES_ROOT_OID.subBranch(28), 192,"AES/None/PKCSPadding/Kwp/192");

  public static final EncryptionAlgorithm
   AES_256_KEY_WRAP_KWP = new EncryptionAlgorithm(SEC_OID_AES_256_KEY_WRAP_KWP,
        Alg.AES,Mode.NONE,
        Padding.PKCS5, IVParameterSpecClasses, 16,
        AES_ROOT_OID.subBranch(48), 256,"AES/None/PKCS5Padding/Kwp/256");

}

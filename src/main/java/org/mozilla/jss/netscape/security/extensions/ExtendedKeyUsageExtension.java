// --- BEGIN COPYRIGHT BLOCK ---
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; version 2 of the License.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
// (C) 2007 Red Hat, Inc.
// All rights reserved.
// --- END COPYRIGHT BLOCK ---
package org.mozilla.jss.netscape.security.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Vector;

import org.mozilla.jss.netscape.security.util.DerOutputStream;
import org.mozilla.jss.netscape.security.util.DerValue;
import org.mozilla.jss.netscape.security.util.ObjectIdentifier;
import org.mozilla.jss.netscape.security.x509.CertAttrSet;
import org.mozilla.jss.netscape.security.x509.Extension;
import org.mozilla.jss.netscape.security.x509.OIDMap;

/**
 * This represents the extended key usage extension.
 */
public class ExtendedKeyUsageExtension extends Extension implements CertAttrSet {

    /**
     *
     */
    private static final long serialVersionUID = 765403075764697489L;
    public static final String OID = "2.5.29.37";
    public static final String NAME = OIDMap.EXT_KEY_USAGE_NAME;
    public static final String OID_OCSPSigning = "1.3.6.1.5.5.7.3.9";
    public static final String OID_CODESigning = "1.3.6.1.5.5.7.3.3";

    public static final int OID_OCSP_SIGNING_STR[] =
        { 1, 3, 6, 1, 5, 5, 7, 3, 9 };
    public static final ObjectIdentifier OID_OCSP_SIGNING = new
            ObjectIdentifier(OID_OCSP_SIGNING_STR);

    public static final int OID_CODE_SIGNING_STR[] =
        { 1, 3, 6, 1, 5, 5, 7, 3, 3 };
    public static final ObjectIdentifier OID_CODE_SIGNING = new
            ObjectIdentifier(OID_OCSP_SIGNING_STR);

    private Vector<ObjectIdentifier> oidSet = null;
    private byte mCached[] = null;

    static {
        try {
            OIDMap.addAttribute(ExtendedKeyUsageExtension.class.getName(),
                    OID, ExtendedKeyUsageExtension.NAME);
        } catch (CertificateException e) {
        }
    }

    public ExtendedKeyUsageExtension() throws IOException {
        this(false, null);
    }

    public ExtendedKeyUsageExtension(boolean crit, Vector<ObjectIdentifier> oids) throws IOException {
        try {
            extensionId = ObjectIdentifier.getObjectIdentifier(OID);
        } catch (IOException e) {
            // never here
        }
        critical = crit;
        if (oids != null) {
            oidSet = new Vector<>(oids);
        } else {
            oidSet = new Vector<>();
        }
        encodeExtValue();
    }

    public ExtendedKeyUsageExtension(Boolean crit, Object byteVal)
            throws IOException {
        extensionId = ObjectIdentifier.getObjectIdentifier(OID);
        critical = crit.booleanValue();
        extensionValue = ((byte[]) byteVal).clone();
        decodeThis();
    }

    @Override
    public void setCritical(boolean newValue) {
        if (critical != newValue) {
            critical = newValue;
            mCached = null;
        }
    }

    public Enumeration<ObjectIdentifier> getOIDs() {
        if (oidSet == null)
            return null;
        return oidSet.elements();
    }

    public void deleteAllOIDs() {
        if (oidSet == null)
            return;
        oidSet.clear();
    }

    public void addOID(ObjectIdentifier oid) {
        if (oidSet == null) {
            oidSet = new Vector<>();
        }

        if (oidSet.contains(oid))
            return;
        oidSet.addElement(oid);
        mCached = null;
    }

    @Override
    public void encode(DerOutputStream out) throws IOException {
        if (mCached == null) {
            encodeExtValue();
            super.encode(out);
            mCached = out.toByteArray();
        }
    }

    @Override
    public String toString() {
        String presentation = "oid=" + ExtendedKeyUsageExtension.OID + " ";

        if (critical) {
            presentation += "critical=true";
        }
        if (extensionValue != null) {
            StringBuffer extByteValue = new StringBuffer(" val=");
            for (int i = 0; i < extensionValue.length; i++) {
                extByteValue.append(extensionValue[i] + " ");
            }
            presentation += extByteValue.toString();
        }
        return presentation;
    }

    @Override
    public void decode(InputStream in)
            throws CertificateException, IOException {
    }

    @Override
    public void encode(OutputStream out)
            throws CertificateException, IOException {
        if (mCached == null) {
            DerOutputStream temp = new DerOutputStream();

            encode(temp);
        }
        out.write(mCached);
    }

    @Override
    public void set(String name, Object obj)
            throws CertificateException, IOException {
        // NOT USED
    }

    @Override
    public Object get(String name) throws CertificateException, IOException {
        // NOT USED
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void delete(String name)
            throws CertificateException, IOException {
        // NOT USED
    }

    private void decodeThis() throws IOException {
        DerValue val = new DerValue(this.extensionValue);

        if (val.tag != DerValue.tag_Sequence) {
            throw new IOException("Invalid encoding of AuthInfoAccess extension");
        }
        if (oidSet == null)
            oidSet = new Vector<>();
        while (val.data.available() != 0) {
            DerValue oidVal = val.data.getDerValue();

            oidSet.addElement(oidVal.getOID());
        }
    }

    private void encodeExtValue() throws IOException {
        DerOutputStream out = new DerOutputStream();
        DerOutputStream temp = new DerOutputStream();

        if (!oidSet.isEmpty()) {
            Enumeration<ObjectIdentifier> oidList = oidSet.elements();

            try {
                while (oidList.hasMoreElements()) {
                    temp.putOID(oidList.nextElement());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            out.write(DerValue.tag_Sequence, temp);
        } catch (IOException ex) {
        } finally {
            out.close();
        }

        extensionValue = out.toByteArray();
    }
}

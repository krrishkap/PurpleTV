package com.amazon.identity.auth.device.utils;

import android.content.Context;
import android.content.pm.Signature;

import tv.purple.monolith.core.SpoofingUtil;
import tv.purple.monolith.models.exception.VirtualImpl;

public class PackageSignatureUtil {
    /* ... */

    private static Signature[] getAndroidSignaturesFor(String str, Context context) {
        Signature[] res = SpoofingUtil.spoofPackageSignature(str); // TODO: __INJECT_CODE
        if (res != null) {
            return res;
        }

        throw new VirtualImpl();
    }

    /* ... */
}

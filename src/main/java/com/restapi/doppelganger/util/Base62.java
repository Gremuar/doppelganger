package com.restapi.doppelganger.util;

public class Base62 {
  private static final io.seruco.encoding.base62.Base62 base62 =
      io.seruco.encoding.base62.Base62.createInstance();

  public static String getHash(String str) {
    final byte[] encoded = base62.encode(str.getBytes());
    return new String(encoded);
  }

  public static Long getUnHash(String str) {
    final byte[] encoded = base62.decode(str.getBytes());
    return Long.valueOf(new String(encoded));
  }
}

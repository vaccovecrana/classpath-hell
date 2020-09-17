package io.vacco.gradle.plugin.cphell;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.zip.*;

public class CphIoUtil {

  public static String getHashOfResource(File f, String resourcePath) {
    try {
      ZipFile zf = new ZipFile(f);
      ZipEntry ze = zf.getEntry(resourcePath);
      InputStream zi = zf.getInputStream(ze);
      MessageDigest instance = MessageDigest.getInstance("MD5");
      DigestInputStream digestInputStream = new DigestInputStream(zi, instance);
      byte[] buffer = new byte[4096];

      while (digestInputStream.read(buffer) > -1) {} // pass

      MessageDigest md1 = digestInputStream.getMessageDigest();
      byte[] digestBytes = md1.digest();

      digestInputStream.close();
      return new BigInteger(1, digestBytes).toString(16);
    } catch (Exception e) { throw new IllegalStateException(e); }
  }
}

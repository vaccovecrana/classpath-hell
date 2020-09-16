package io.vacco.gradle.plugin.cphell;

import org.gradle.api.logging.Logger;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.util.*;
import java.util.regex.Pattern;
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

  public static Collection<String> getResourcesFromJarFile(final File jarFile, final Pattern pattern) {
    try {
      final ArrayList<String> resourceFiles = new ArrayList<>();
      ZipFile zf = new ZipFile(jarFile);
      final Enumeration<?> e = zf.entries();

      while (e.hasMoreElements()) {
        final ZipEntry ze = (ZipEntry) e.nextElement();
        final String resourceFileName = ze.getName();
        final boolean accept = pattern.matcher(resourceFileName).matches();
        if (accept) {
          resourceFiles.add(resourceFileName);
        }
      }
      zf.close();
      return resourceFiles;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  public static List<String> getResources(File location, Logger log) {
    ArrayList<String> files = new ArrayList<>();
    if (location.isFile()) {
      if (location.getName().toLowerCase().endsWith(".jar") || location.getName().toLowerCase().endsWith(".zip")) {
        Collection<String> resourcesInJar = getResourcesFromJarFile(location, Pattern.compile(".*"));
        files.addAll(resourcesInJar);
      } else {
        files.add(location.getPath());
      }
    } else if (location.isDirectory()) {
      for (File fileOrDir : Objects.requireNonNull(location.listFiles())) {
        Collection<String> resourcesInJar = getResources(fileOrDir, log);
        files.addAll(resourcesInJar);
      }
    } else if (log.isDebugEnabled()) {
      log.debug("classpathHell: skipping location as is neither a file nor a directory: {}", location);
    }
    return files;
  }
}

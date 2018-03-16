package uyun.xianglong.examples.sclaz

import java.io.{File, FileOutputStream}
import java.util.jar.{JarEntry, JarOutputStream}

import com.google.common.io.ByteStreams

import scala.collection.mutable

object JarUtils {

  private def listClasses(file: File): Seq[String] = {
    val basePath = file.getAbsolutePath
    listFiles(file).map(file => file.substring(basePath.length + 1).replace("\\", "/"))
  }

  private def listFiles(file: File): Seq[String] = {
    val buffer = mutable.Buffer[String]()
    if (file.isDirectory) {
      buffer ++= file.listFiles().flatMap(listFiles)
    } else {
      buffer += file.getAbsolutePath
    }
    buffer
  }

  def getJars: Seq[String] = {
    val classPath = System.getProperty("java.class.path")
    val paths =
      classPath.split(";")
        .filterNot(_.startsWith(System.getProperty("java.home")))
        .filterNot(_.contains("IntelliJ"))
    val artifact = "test.jar"
    val stream = new JarOutputStream(new FileOutputStream(artifact))
    val jars = mutable.Buffer[String]() ++= paths.filter(_.endsWith(".jar"))
    paths
      .filter(_.contains("classes")).filterNot(_.endsWith(".jar"))
      .reverse
      .map(file => new File(file))
      .flatMap(listClasses)
      .foreach(clazz => {
        val entry = new JarEntry(clazz)
        stream.putNextEntry(entry)
        val in = ClassLoader.getSystemResourceAsStream(clazz)
        ByteStreams.copy(in, stream)
      })
    stream.flush()
    stream.close()
    jars prepend artifact
    jars
  }
}

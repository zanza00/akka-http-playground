package net.zanzapla.drone.management.serializers

import java.text.SimpleDateFormat

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, native}

trait JsonSupport extends Json4sSupport {

  implicit val serialization: Serialization.type = native.Serialization

  implicit def json4sFormats: Formats = customDateFormat ++ JodaTimeSerializers.all ++ CustomSerializers.all

  val customDateFormat = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
  }
  
}

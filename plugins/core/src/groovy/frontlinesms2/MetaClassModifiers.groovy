package frontlinesms2

import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

import org.apache.camel.Exchange

class MetaClassModifiers {
	static def addZipMethodToFile() {
		File.metaClass.zip = { output, filter=null ->
			new ZipOutputStream(output).withStream { zipOutStream ->
				delegate.eachFileRecurse { f ->
					if(!f.isDirectory() && (!filter || filter.call(f))) {
						zipOutStream.putNextEntry(new ZipEntry(f.path))
						new FileInputStream(f).withStream { inStream ->
							def buffer = new byte[1024]
							def count
							while((count = inStream.read(buffer, 0, 1024)) != -1) {
								zipOutStream.write(buffer, 0, count)
							}
						}
						zipOutStream.closeEntry()
					}
				}
			}
		}
	}
	
	static def addTruncateMethodToStrings() {
		String.metaClass.truncate = { max=16 ->
			delegate.size() <= max? delegate: delegate.substring(0, max-1) + '…'
		}
	}

	static def addRoundingMethodsToDates() {
		def setTime = { Date d, int h, int m, int s ->
			def calc = Calendar.getInstance()
			calc.setTime(d)
			calc.set(Calendar.HOUR_OF_DAY, h)
			calc.set(Calendar.MINUTE, m)
			calc.set(Calendar.SECOND, s)
			calc.getTime()
		}
		
		Date.metaClass.getStartOfDay = {
			setTime(delegate, 0, 0, 0)
		}

		Date.metaClass.getEndOfDay = {
			setTime(delegate, 23, 59, 59)
		}
	}
	
	static def addCamelMethods() {
		Exchange.metaClass.getFconnectionId = {
			def routeId = delegate.unitOfWork?.routeContext?.route?.id
			final def ID_REGEX = /.*-(\d+)$/
			if(routeId && routeId==~ID_REGEX) {
				return (routeId =~ ID_REGEX)[0][1]
			}
		}
	}
	
	static def addMapMethods() {
		LinkedHashMap.metaClass.getAllKeys = {
		   def c
		   c = { map ->
			   def values = []
				map.each { k, v ->
				    if(v instanceof Map) {
				        values << k
				       values << c(v)
				    } else {
				        values << k
				    }
				}
				return values.flatten()
		   }
		   c(delegate)
		}
		
		LinkedHashMap.metaClass.getAllValues = {
		   def c
		   c = { map ->
			   def values = []
				map.each { k, v ->
				    if(v instanceof Map) {
				       values << c(v)
				    } else {
				        values << v
				    }
				}
				return values.flatten() - null
		   }
		   c(delegate)
		}
		
	}
	
}


package frontlinesms2

import java.util.zip.ZipOutputStream
import java.util.zip.ZipEntry

class MetaClassModifiers {
	static def addZipMethodToFile() {
		File.metaClass.zip = { output ->
			new ZipOutputStream(output).withStream { zipOutStream ->
				delegate.eachFileRecurse { f ->
					if(!f.isDirectory()) {
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

	static def addFilterMethodToList() {
		List.metaClass.filter = { Closure c ->
			def r = []
			delegate.each {
				if(c(it)) r << it
			}
			r
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
}


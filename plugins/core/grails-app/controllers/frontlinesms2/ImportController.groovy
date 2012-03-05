package frontlinesms2
import java.io.File;
import java.io.InputStream;
import java.io.Reader;

class ImportController {
	def homeDir = "${System.properties.'user.home'}/.frontlinesms2/"
	def userDir = new File(homeDir, "/importedcsvfiles")
	
	def uploadCSVFile() {
		def fileextension = "" 
		def uploadedFile = request.getFile('importedcsvfile')
		if ( !uploadedFile.empty ) {
			fileextension = uploadedFile.originalFilename
			def splitArray = fileextension.split("\\.")
			fileextension = splitArray[splitArray.size()-1]
			
			if ( fileextension!="csv" ) {
				flash.message = "Please upload a csv file."
				redirect (controller: "settings", action: 'general')
				return ""
			}
			userDir.mkdirs()
			uploadedFile.transferTo( new File( userDir, uploadedFile.originalFilename))
			return "${homeDir}importedcsvfiles/${uploadedFile.originalFilename}"
		}
		return ""
	}

	def importedContacts = {
		def headerList = []
		def x = 0
		def savedCount = 0
		def failedCount = 0
		def uploadedCSVFile = ""
		uploadedCSVFile = uploadCSVFile()

		if ( uploadedCSVFile != "" ) {
			new File(uploadedCSVFile).eachLine { //csvLine ->
				
				def contactProperties = [:]
				def customField = [:]
				def importedGroup = ""
				def csvLine = it.split(",")

				if ( x!=0 ) {
					def y = 0
					csvLine.each {	
						if ( headerList[y].trim()!='"Name"' && headerList[y].trim()!='"Mobile Number"' && headerList[y].trim()!='"Other Mobile Number"' && headerList[y].trim()!='"E-mail Address"' && headerList[y].trim()!='"Notes"' && headerList[y].trim()!='"Group(s)"' ) {
							customField.put(headerList[y].trim().replace('"',""), csvLine[y].trim().replace('"',""))
						} else {
							if (headerList[y]=='"Group(s)"' ) {
								importedGroup = csvLine[y].trim().replace('"',"")					
							} else {
								if ( headerList[y]=='"Name"' ) {
									contactProperties.name = csvLine[y].trim().replace('"',"")
								} else if ( headerList[y]=='"Mobile Number"' ) {
									contactProperties.primaryMobile = csvLine[y].trim().replace('"',"")
								} else if ( headerList[y]=='"Other Mobile Number"' ) {
									contactProperties.secondaryMobile = csvLine[y].trim().replace('"',"")
								} else if ( headerList[y]=='"E-mail Address"' ) {
									def email = csvLine[y].trim().replace('"',"")
									if(email != '""') contactProperties.email = email
								} else if ( headerList[y]=='"Notes"' ) {
									contactProperties.notes = csvLine[y].trim().replace('"',"")
								} 
							}
						}
						y++
						
						if (y==csvLine.length) {
							def contact = new Contact(contactProperties)
							if (contact.save(failOnError: true)) {
								savedCount++ 
								customField.eachWithIndex {
									new CustomField(name: it.key, value: customField.get(it.key), contact: contact).save()
								}
								def contactGroups = importedGroup.split("\\\\")
								def nestedGroup = []
								contactGroups.each {
									if ( !it.equals("") ) nestedGroup.add(it)
								}
								def singleImportedGroup = []
								nestedGroup.each {
									singleImportedGroup = it.split("/")
									def longGroup = ""
									singleImportedGroup.each {
										if ( !it.equals("") ) {
											def flsmsGroup = Group.findByName(it)
											if ( longGroup == "" ) { longGroup=it } else { longGroup=longGroup+ "-" +it }
											
											if ( flsmsGroup!=null ) { contact.addToGroups(flsmsGroup)
											} else {
												createGroup(it)
												contact.addToGroups(Group.findByName(it))
											}
											if ( !longGroup.equals(it) && !longGroup.equals("") ) {
												def flsmsGroupLong = Group.findByName(longGroup)
												if (flsmsGroupLong!=null) {
													contact.addToGroups(flsmsGroupLong)
												} else {
													createGroup(longGroup)
													contact.addToGroups(Group.findByName(longGroup))
												}
											}
										}
									}
								}
							} else {
								failedCount ++
							}
						}
					}
				} else {
					headerList = csvLine
				}	
				x++
			}
			userDir.deleteDir()
			flash.message = "$savedCount contacts were imported; $failedCount failed" 
			redirect (controller: "settings", action: 'general')
		} else {
 			flash.message = "Invalid file."
			redirect (controller: "settings", action: 'general')
		}
	}
	
	private def createGroup(String n) {
		new Group(name: n).save(failOnError: true)
	}
	
	private def createC(String n) {
		new Group(name: n).save(failOnError: true)
	}
}